package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBruteBrain.class)
public class PiglinBruteBrainMixin {

    /**
     * @author Draylar
     *
     * @reason method_30255 is the desugared lambda used by method_30249 that searches for a nearby player to aggro on.
     * This mixin modifies the search logic to exclude players disguised as anything besides a Wither Skeleton or Wither.
     */
    @Inject( method = "method_30255", at = @At("HEAD"), expect = 0, cancellable = true)
    private static void identity$method_30249FilterLambdaIntermediary(AbstractPiglinEntity abstractPiglinEntity, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if(identity != null && !(identity instanceof WitherSkeletonEntity) && !(identity instanceof WitherEntity)) {
                cir.setReturnValue(false);
            }
        }
    }
}
