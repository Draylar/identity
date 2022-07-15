package draylar.identity.impl.tick.identity;

import draylar.identity.api.IdentityTickHandler;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.player.PlayerEntity;

public class FrogTickHandler implements IdentityTickHandler<FrogEntity> {

    @Override
    public void tick(PlayerEntity player, FrogEntity frog) {
        if(player.world.isClient) {
            boolean walk = player.isOnGround() && player.getVelocity().horizontalLengthSquared() > 1.0E-6 && !player.isInsideWaterOrBubbleColumn();
            boolean swim = player.getVelocity().horizontalLengthSquared() > 1.0E-6 && player.isInsideWaterOrBubbleColumn();

            // Walking implementation
            if (walk) {
                frog.walkingAnimationState.startIfNotRunning(frog.age);
            } else {
                frog.walkingAnimationState.stop();
            }

            // Swimming
            if (swim) {
                frog.idlingInWaterAnimationState.stop();
                frog.swimmingAnimationState.startIfNotRunning(frog.age);
            } else if (player.isInsideWaterOrBubbleColumn()) {
                frog.swimmingAnimationState.stop();
                frog.idlingInWaterAnimationState.startIfNotRunning(frog.age);
            } else {
                frog.swimmingAnimationState.stop();
                frog.idlingInWaterAnimationState.stop();
            }

            // Random croaking
            if(player.world.random.nextDouble() <= 0.001) {
                frog.croakingAnimationState.start(player.age);
            }
        }
    }
}
