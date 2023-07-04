package draylar.identity.impl.tick.identity;

import draylar.identity.api.IdentityTickHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.player.PlayerEntity;

public class FrogTickHandler implements IdentityTickHandler<FrogEntity> {

    @Override
    public void tick(PlayerEntity player, FrogEntity frog) {
        if(player.getWorld().isClient) {
            boolean walk = player.isOnGround() && player.getVelocity().horizontalLengthSquared() > 1.0E-6 && !player.isInsideWaterOrBubbleColumn();
            boolean swim = player.getVelocity().horizontalLengthSquared() > 1.0E-6 && player.isInsideWaterOrBubbleColumn();

            // Walking implementation
            if (walk) {
//                frog.limbAnimator.startIfNotRunning(frog.age);
            } else {
//                frog.walkingAnimationState.stop();
            }

            // Jumping
            if(!player.isOnGround() && !swim && !walk && !player.isInsideWaterOrBubbleColumn()) {
                frog.longJumpingAnimationState.startIfNotRunning(frog.age);
            } else {
                frog.longJumpingAnimationState.stop();
            }

            // Swimming
            if (swim) {
                frog.idlingInWaterAnimationState.stop();
//                frog.swimmingAnimationState.startIfNotRunning(frog.age);
            } else if (player.isInsideWaterOrBubbleColumn()) {
//                frog.swimmingAnimationState.stop();
                frog.idlingInWaterAnimationState.startIfNotRunning(frog.age);
            } else {
//                frog.swimmingAnimationState.stop();
                frog.idlingInWaterAnimationState.stop();
            }

            // Random croaking
            if(player.getWorld().random.nextDouble() <= 0.001) {
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
