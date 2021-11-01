package draylar.identity.fabric.cca;

import dev.architectury.event.EventResult;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import draylar.identity.Identity;
import draylar.identity.api.event.IdentitySwapCallback;
import draylar.identity.api.platform.FlightHelper;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.platform.PlayerIdentity;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.registry.EntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

/**
 * This component provides information about a {@link PlayerEntity}'s identity status.
 *
 * <p>{@link IdentityComponent#identity} being null represents "no identity," and accessors should check for this before using the field.
 */
public class IdentityComponent implements AutoSyncedComponent {

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
     * @return the current {@link LivingEntity} identity associated with this component's player owner, or null if they have no identity equipped
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
     * @param identity {@link LivingEntity} new identity for this component, or null to clear
     */
    public boolean setIdentity(LivingEntity identity) {
        EventResult result = IdentitySwapCallback.EVENT.invoker().swap((ServerPlayerEntity) player, identity);
        if(result.isFalse()) {
            return false;
        }

        this.identity = identity;

        // refresh entity hitbox dimensions
        ((DimensionsRefresher) player).identity_refreshDimensions();

        // Identity is valid and scaling health is on; set entity's max health and current health to reflect identity.
        if(identity != null && IdentityConfig.getInstance().scalingHealth()) {
            player.setHealth(Math.min(player.getHealth(), identity.getMaxHealth()));
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.min(IdentityConfig.getInstance().maxHealth(), identity.getMaxHealth()));
        }

        // If the identity is null (going back to player), set the player's base health value to 20 (default) to clear old changes.
        if(identity == null) {
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20);
        }

        // update flight properties on player depending on identity
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
        if(Identity.hasFlyingPermissions(serverPlayerEntity)) {
            FlightHelper.grantFlightTo(serverPlayerEntity);
            player.getAbilities().setFlySpeed(IdentityConfig.getInstance().flySpeed());
            player.sendAbilitiesUpdate();
        } else {
            FlightHelper.revokeFlight(serverPlayerEntity);
            player.getAbilities().setFlySpeed(0.05f);
            player.sendAbilitiesUpdate();
        }

        // If the player is riding a Ravager and changes into an Identity that cannot ride Ravagers, kick them off.
        if(player.getVehicle() instanceof RavagerEntity && (identity == null || !EntityTags.RAVAGER_RIDING.contains(identity.getType()))) {
            player.stopRiding();
        }

        // sync with client
        if(player instanceof ServerPlayerEntity sPlayer) {
            PlayerIdentity.sync(sPlayer);
        }

        return true;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        Optional<EntityType<?>> type = EntityType.fromNbt(tag);

        // set identity to null (no identity) if the entity id is "minecraft:empty"
        if(tag.getString("id").equals("minecraft:empty")) {
            this.identity = null;
            ((DimensionsRefresher) player).identity_refreshDimensions();
        }

        // if entity type was valid, deserialize entity data from tag
        else if(type.isPresent()) {
            NbtCompound entityTag = tag.getCompound("EntityData");

            // ensure entity data exists
            if(entityTag != null) {
                if(identity == null || !type.get().equals(identity.getType())) {
                    identity = (LivingEntity) type.get().create(player.world);

                    // refresh player dimensions/hitbox on client
                    ((DimensionsRefresher) player).identity_refreshDimensions();
                }

                identity.readNbt(entityTag);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound entityTag = new NbtCompound();

        // serialize current identity data to tag if it exists
        if(identity != null) {
            identity.writeNbt(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no identity is equipped (or the identity entity type is invalid)
        tag.putString("id", identity == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getId(identity.getType()).toString());
        tag.put("EntityData", entityTag);
    }
}
