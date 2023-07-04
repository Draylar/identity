package draylar.identity.impl.tick.identity;

import draylar.identity.api.IdentityTickHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class JumpBoostTickHandler<T extends LivingEntity> implements IdentityTickHandler<T> {

    private final int level;

    public JumpBoostTickHandler(int level) {
        this.level = level;
    }

    @Override
    public void tick(PlayerEntity player, LivingEntity entity) {
        if(!player.getWorld().isClient) {
            if(player.age % 5 == 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 2, level, true, false));
            }
        }
    }
}
