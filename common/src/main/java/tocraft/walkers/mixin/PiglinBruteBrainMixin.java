package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerWalkers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBruteBrain.class)
public class PiglinBruteBrainMixin {

    /**
     * @author To_Craft
     *
     * @reason method_30255 is the desugared lambda used by method_30249 that searches for a nearby player to aggro on.
     * This mixin modifies the search logic to exclude players disguised as anything besides a Wither Skeleton or Wither.
     * We target fabric and forge separately due to arch not being able to find the backed methods of the targeted lambdas
     */
    @Group(name = "method_30249FilterLambda", min = 1, max = 1)
    @Inject( method = "method_30255", at = @At("HEAD"), expect = 0, cancellable = true)
    private static void walkers$method_30249FilterLambdaIntermediary(AbstractPiglinEntity abstractPiglinEntity, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof PlayerEntity player) {
            LivingEntity walkers = PlayerWalkers.getWalkers(player);

            if(walkers != null && !(walkers instanceof WitherSkeletonEntity) && !(walkers instanceof WitherEntity)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Group(name = "method_30249FilterLambda", min = 1, max = 1)
    @Inject(method = "m_35106_", at = @At("HEAD"), remap = false, expect = 0, cancellable = true)
    private static void walkers$method_30249FilterLambdaSRG(AbstractPiglinEntity abstractPiglinEntity, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof PlayerEntity player) {
            LivingEntity walkers = PlayerWalkers.getWalkers(player);

            if(walkers != null && !(walkers instanceof WitherSkeletonEntity) && !(walkers instanceof WitherEntity)) {
                cir.setReturnValue(false);
            }
        }
    }
}
