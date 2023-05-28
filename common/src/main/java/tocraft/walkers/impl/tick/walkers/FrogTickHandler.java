package tocraft.walkers.impl.tick.walkers;

import tocraft.walkers.api.WalkersTickHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.player.PlayerEntity;

public class FrogTickHandler implements WalkersTickHandler<FrogEntity> {

    @Override
    public void tick(PlayerEntity player, FrogEntity frog) {
        if(player.world.isClient) {
            boolean walk = player.isOnGround() && player.getVelocity().horizontalLengthSquared() > 1.0E-6 && !player.isInsideWaterOrBubbleColumn();
            boolean swim = player.getVelocity().horizontalLengthSquared() > 1.0E-6 && player.isInsideWaterOrBubbleColumn();

            // Jumping
            if(!player.isOnGround() && !swim && !walk && !player.isInsideWaterOrBubbleColumn()) {
                frog.longJumpingAnimationState.startIfNotRunning(frog.age);
            } else {
                frog.longJumpingAnimationState.stop();
            }

            // Swimming
            if (swim) {
                frog.idlingInWaterAnimationState.stop();
            } else if (player.isInsideWaterOrBubbleColumn()) {
                frog.idlingInWaterAnimationState.startIfNotRunning(frog.age);
            } else {
                frog.idlingInWaterAnimationState.stop();
            }

            // Random croaking
            if(player.world.random.nextDouble() <= 0.001) {
                frog.croakingAnimationState.start(player.age);
            }

            // Tongue
            if(player.handSwinging) {
                frog.usingTongueAnimationState.startIfNotRunning(player.age);
            } else {
                frog.usingTongueAnimationState.stop();
            }
        } else {
            // Buffs - jump boost
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 2, 2, true, false));
        }
    }
}
