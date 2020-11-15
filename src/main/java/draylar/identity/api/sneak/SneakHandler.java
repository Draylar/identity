package draylar.identity.api.sneak;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface SneakHandler<Entity extends LivingEntity> {
    void onSneak(PlayerEntity player, Entity identity);
}
