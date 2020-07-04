package draylar.identity.cca;

import draylar.identity.Identity;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

/**
 * This component provides information about a {@link PlayerEntity}'s identity status.
 *
 * <p>{@link IdentityComponent#identity} being null represents "no identity," and accessors should check for this before using the field.
 */
public class IdentityComponent implements EntitySyncedComponent {

    private final PlayerEntity player;
    private LivingEntity identity = null;

    public IdentityComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public Entity getEntity() {
        return player;
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

        // update flight properties on player depending on identity
        if(identity != null && Identity.CONFIG.enableFlight && EntityTags.FLYING.contains(identity.getType())) {
            player.abilities.allowFlying = true;
            player.sendAbilitiesUpdate();
        } else if(!player.isCreative() && Identity.CONFIG.enableFlight && !player.isSpectator()) {
            player.abilities.allowFlying = false;
            player.abilities.flying = false;
            player.sendAbilitiesUpdate();
        }

        // sync with client
        sync();
    }

    @Override
    public ComponentType<?> getComponentType() {
        return Components.CURRENT_IDENTITY;
    }

    @Override
    public void fromTag(CompoundTag tag) {
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

                    // refresh flight abilities
                    if(EntityTags.FLYING.contains(identity.getType()) && Identity.CONFIG.enableFlight) {
                        player.abilities.allowFlying = true;
                    }
                }

                identity.fromTag(entityTag);
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag entityTag = new CompoundTag();

        // serialize current identity data to tag if it exists
        if(identity != null) {
            identity.toTag(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no identity is equipped (or the identity entity type is invalid)
        tag.putString("id", identity == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getId(identity.getType()).toString());
        tag.put("EntityData", entityTag);

        return tag;
    }
}