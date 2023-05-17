package tocraft.walkers.impl.tick.walkers;

import tocraft.walkers.api.WalkersTickHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class JumpBoostTickHandler<T extends LivingEntity> implements WalkersTickHandler<T> {

    private final int level;

    public JumpBoostTickHandler(int level) {
        this.level = level;
    }

    @Override
    public void tick(PlayerEntity player, LivingEntity entity) {
        if(!player.world.isClient) {
            if(player.age % 5 == 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 2, level, true, false));
            }
        }
    }
}
