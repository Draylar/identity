package draylar.identity.forge.mixin;

import dev.architectury.event.EventResult;
import dev.architectury.utils.NbtType;
import draylar.identity.Identity;
import draylar.identity.api.event.IdentitySwapCallback;
import draylar.identity.api.platform.FlightHelper;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.platform.PlayerIdentity;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.forge.impl.PlayerDataProvider;
import draylar.identity.registry.EntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDataMixin extends LivingEntity implements PlayerDataProvider {

    @Unique private static final String ABILITY_COOLDOWN_KEY = "AbilityCooldown";
    @Unique private final List<Identifier> unlocked = new ArrayList<>();
    @Unique private final List<Identifier> favorites = new ArrayList<>();
    @Unique private int remainingTime = 0;
    @Unique private int abilityCooldown = 0;
    @Unique private LivingEntity identity = null;

    private PlayerEntityDataMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readNbt(NbtCompound tag, CallbackInfo info) {
        // Read 'Unlocked' Identity data
        NbtList unlockedIdList = tag.getList("UnlockedMorphs", NbtType.STRING);
        unlockedIdList.forEach(idTag -> unlocked.add(new Identifier(idTag.asString())));

        // Favorites
        favorites.clear();
        NbtList favoriteIdList = tag.getList("FavoriteIdentities", NbtType.STRING);
        favoriteIdList.forEach(idTag -> favorites.add(new Identifier(idTag.asString())));

        // Abilities
        abilityCooldown = tag.getInt(ABILITY_COOLDOWN_KEY);

        // Hostility
        remainingTime = tag.getInt("RemainingHostilityTime");

        // Current Identity
        readCurrentIdentity(tag.getCompound("CurrentIdentity"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeNbt(NbtCompound tag, CallbackInfo info) {
        // Read 'Unlocked' Identity data
        {
            NbtList idList = new NbtList();

            unlocked.forEach(entityId -> {
                idList.add(NbtString.of(entityId.toString()));
            });

            // reminder: do not change this tag
            tag.put("UnlockedMorphs", idList);
        }

        // Favorites
        {
            NbtList idList = new NbtList();
            favorites.forEach(entityId -> idList.add(NbtString.of(entityId.toString())));
            tag.put("FavoriteIdentities", idList);
        }

        // Abilities
        tag.putInt(ABILITY_COOLDOWN_KEY, abilityCooldown);

        // Hostility
        tag.putInt("RemainingHostilityTime", remainingTime);

        // Current Identity
        tag.put("CurrentIdentity", writeCurrentIdentity(new NbtCompound()));
    }

    @Unique
    private NbtCompound writeCurrentIdentity(NbtCompound tag) {
        NbtCompound entityTag = new NbtCompound();

        // serialize current identity data to tag if it exists
        if(identity != null) {
            identity.writeNbt(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no identity is equipped (or the identity entity type is invalid)
        tag.putString("id", identity == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getId(identity.getType()).toString());
        tag.put("EntityData", entityTag);
        return tag;
    }

    @Unique
    public void readCurrentIdentity(NbtCompound tag) {
        Optional<EntityType<?>> type = EntityType.fromNbt(tag);

        // set identity to null (no identity) if the entity id is "minecraft:empty"
        if(tag.getString("id").equals("minecraft:empty")) {
            this.identity = null;
            ((DimensionsRefresher) this).identity_refreshDimensions();
        }

        // if entity type was valid, deserialize entity data from tag
        else if(type.isPresent()) {
            NbtCompound entityTag = tag.getCompound("EntityData");

            // ensure entity data exists
            if(entityTag != null) {
                if(identity == null || !type.get().equals(identity.getType())) {
                    identity = (LivingEntity) type.get().create(world);

                    // refresh player dimensions/hitbox on client
                    ((DimensionsRefresher) this).identity_refreshDimensions();
                }

                identity.readNbt(entityTag);
            }
        }
    }

    @Unique
    @Override
    public List<Identifier> getUnlocked() {
        return unlocked;
    }

    @Unique
    @Override
    public List<Identifier> getFavorites() {
        return favorites;
    }

    @Unique
    @Override
    public int getRemainingHostilityTime() {
        return remainingTime;
    }

    @Unique
    @Override
    public void setRemainingHostilityTime(int max) {
        remainingTime = max;
    }

    @Unique
    @Override
    public int getAbilityCooldown() {
        return abilityCooldown;
    }

    @Unique
    @Override
    public void setAbilityCooldown(int abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
    }

    @Unique
    @Override
    public LivingEntity getIdentity() {
        return identity;
    }

    @Unique
    @Override
    public void setIdentity(LivingEntity identity) {
        this.identity = identity;
    }

    @Unique
    @Override
    public boolean updateIdentity(@Nullable LivingEntity identity) {
        PlayerEntity player = (PlayerEntity) (Object) this;
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
        if(Identity.hasFlyingPermissions((ServerPlayerEntity) player)) {
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
        if(!player.world.isClient) {
            PlayerIdentity.sync((ServerPlayerEntity) player);
        }

        return true;
    }
}
