package draylar.identity.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import draylar.identity.Identity;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.mixin.LivingEntityAccessor;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

/**
 * This component provides information about a {@link PlayerEntity}'s identity status.
 *
 * <p>{@link IdentityComponent#identity} being null represents "no identity," and accessors should check for this before using the field.
 */
public class IdentityComponent implements AutoSyncedComponent, ServerTickingComponent {

    private final PlayerEntity player;
    private LivingEntity identity = null;

    public IdentityComponent(PlayerEntity player) {
        this.player = player;
    }

    /**
     * Returns the identity associated with the {@link PlayerEntity} this component is attached to.
     *
     * <p>Note that this method may return null, which represents "no identity."
     *
     * @return  the current {@link LivingEntity} identity associated with this component's player owner, or null if they have no identity equipped
     */
    public LivingEntity getIdentity() {
        return identity;
    }

    /**
     * Sets the identity of this {@link IdentityComponent}.
     *
     * <p>Setting a identity refreshes the player's dimensions/hitbox, and toggles flight capabilities depending on the entity.
     * To clear this component's identity, pass null.
     *
     * @param identity  {@link LivingEntity} new identity for this component, or null to clear
     */
    public void setIdentity(LivingEntity identity) {
        this.identity = identity;

        // refresh entity hitbox dimensions
        ((DimensionsRefresher) player).refresh();

        // Identity is valid and scaling health is on; set entity's max health and current health to reflect identity.
        if(identity != null && Identity.CONFIG.scalingHealth) {
            player.setHealth(Math.min(player.getHealth(), identity.getMaxHealth()));
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.min(Identity.CONFIG.maxHealth, identity.getMaxHealth()));
        }

        // If the identity is null (going back to player), set the player's base health value to 20 (default) to clear old changes.
        if(identity == null) {
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20);
        }

        // update flight properties on player depending on identity
        if(Identity.hasFlyingPermissions((ServerPlayerEntity) player)) {
            Identity.ABILITY_SOURCE.grantTo(player, VanillaAbilities.ALLOW_FLYING);
        } else {
            Identity.ABILITY_SOURCE.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
        }

        // sync with client
        Components.CURRENT_IDENTITY.sync(this.player);
    }

    @Override
    public void serverTick() {
        tickTemperature();
        tickFire();
        tickIdentity();
    }

    private void tickIdentity() {
        // todo: maybe items not working because world is client?
        PlayerEntity player = this.player;
        LivingEntity identity = this.identity;

        // assign basic data to entity from player on server; most data transferring occurs on client
        if (identity != null) {
            identity.setPos(player.getX(), player.getY(), player.getZ());
            identity.setHeadYaw(player.getHeadYaw());
            identity.setJumping(((LivingEntityAccessor) player).isJumping());
            identity.setSprinting(player.isSprinting());
            identity.setStuckArrowCount(player.getStuckArrowCount());
            identity.setInvulnerable(true);
            identity.setNoGravity(true);
            identity.setPose(player.getPose());
            identity.setSwimming(player.isSwimming());
            identity.setCurrentHand(player.getActiveHand());

            ((LivingEntityAccessor) identity).callTickActiveItemStack();
            Components.CURRENT_IDENTITY.sync(player);
        }
    }

    private void tickFire() {
        PlayerEntity player = this.player;

        if (!player.world.isClient && !player.isCreative() && !player.isSpectator()) {
            // check if the player is identity
            if (this.identity != null) {
                EntityType<?> type = this.identity.getType();

                // check if the player's current identity burns in sunlight
                if (EntityTags.BURNS_IN_DAYLIGHT.contains(type)) {
                    boolean bl = this.isInDaylight();
                    if (bl) {

                        // check for helmets to negate burning
                        ItemStack itemStack = this.player.getEquippedStack(EquipmentSlot.HEAD);
                        if (!itemStack.isEmpty()) {
                            if (itemStack.isDamageable()) {

                                // damage stack instead of burning player
                                itemStack.setDamage(itemStack.getDamage() + this.player.getRandom().nextInt(2));
                                if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                                    this.player.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                                    this.player.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                                }
                            }

                            bl = false;
                        }

                        // set player on fire
                        if (bl) {
                            this.player.setOnFireFor(8);
                        }
                    }
                }
            }
        }
    }

    private boolean isInDaylight() {
        PlayerEntity player = this.player;
        if (player.world.isDay() && !player.world.isClient) {
            float brightnessAtEyes = player.getBrightnessAtEyes();
            BlockPos daylightTestPosition = new BlockPos(player.getX(), (double) Math.round(player.getY()), player.getZ());

            // move test position up one block for boats
            if (player.getVehicle() instanceof BoatEntity) {
                daylightTestPosition = daylightTestPosition.up();
            }

            return brightnessAtEyes > 0.5F && player.getRandom().nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F && player.world.isSkyVisible(daylightTestPosition);
        }

        return false;
    }

    private void tickTemperature() {
        PlayerEntity player = this.player;

        if (!player.isCreative() && !player.isSpectator()) {
            // check if the player is identity
            if (this.identity != null) {
                EntityType<?> type = this.identity.getType();

                // damage player if they are an identity that gets hurt by high temps (eg. snow golem in nether)
                if (EntityTags.HURT_BY_HIGH_TEMPERATURE.contains(type)) {
                    if (player.world.getBiome(new BlockPos(player.getX(), 0, player.getZ())).getTemperature(new BlockPos(player.getX(), player.getY(), player.getZ())) > 1.0F) {
                        player.damage(DamageSource.ON_FIRE, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        Optional<EntityType<?>> type = EntityType.fromTag(tag);

        // set identity to null (no identity) if the entity id is "minecraft:empty"
        if(tag.getString("id").equals("minecraft:empty")) {
            this.identity = null;
            ((DimensionsRefresher) player).refresh();
        }

        // if entity type was valid, deserialize entity data from tag
        else if (type.isPresent()) {
            CompoundTag entityTag = tag.getCompound("EntityData");

            // ensure entity data exists
            if (entityTag != null) {
                if(identity == null || !type.get().equals(identity.getType())) {
                    identity = (LivingEntity) type.get().create(player.world);

                    // refresh player dimensions/hitbox on client
                    ((DimensionsRefresher) player).refresh();
                }

                identity.fromTag(entityTag);
            }
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        CompoundTag entityTag = new CompoundTag();

        // serialize current identity data to tag if it exists
        if(identity != null) {
            identity.toTag(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no identity is equipped (or the identity entity type is invalid)
        tag.putString("id", identity == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getId(identity.getType()).toString());
        tag.put("EntityData", entityTag);
    }
}