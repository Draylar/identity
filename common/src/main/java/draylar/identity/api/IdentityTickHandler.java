package draylar.identity.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface IdentityTickHandler<Z extends Entity> {

    void tick(PlayerEntity player, Z entity);
}
