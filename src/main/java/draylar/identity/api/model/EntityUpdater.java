package draylar.identity.api.model;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Implementers should use the provided {@link PlayerEntity} instance to update the provided {@link Entity}'s properties.
 *
 * <p>{@link EntityUpdater} instances can be registered and retrieved through {@link EntityUpdaters}.
 *
 * @param <Entity>
 */
@FunctionalInterface
public interface EntityUpdater<Entity extends LivingEntity> {

    /**
     * Updates the given {@link Entity} using properties from the given {@link PlayerEntity}.
     *
     * <p>Called once every render update on the client.
     *
     * @param from  {@link PlayerEntity} to copy properties from
     * @param to  {@link Entity} to copy properties to
     */
    void update(PlayerEntity from, Entity to);
}