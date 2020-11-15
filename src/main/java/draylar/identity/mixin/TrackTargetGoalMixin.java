package draylar.identity.mixin;

import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackTargetGoal.class)
public abstract class TrackTargetGoalMixin {
    @Shadow @Final protected MobEntity mob;

    @Shadow public abstract void stop();

    @Inject(method = "shouldContinue", at = @At("RETURN"), cancellable = true)
    protected void identity_shouldContinue(CallbackInfoReturnable<Boolean> cir) {
        // NO-OP
    }
}
