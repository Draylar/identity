package tocraft.walkers.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface WalkersTickHandler<Z extends Entity> {

    void tick(PlayerEntity player, Z entity);
}
