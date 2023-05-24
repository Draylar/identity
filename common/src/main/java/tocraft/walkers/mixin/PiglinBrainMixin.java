package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.registry.WalkersEntityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(
            method = "isPreferredAttackTarget",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void shouldAttackWalkers(PiglinEntity piglin, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        boolean shouldAttack = cir.getReturnValue();

        if(shouldAttack && target instanceof PlayerEntity player) {
            LivingEntity walkers = PlayerWalkers.getCurrentShape(player);
            boolean hasHostility = PlayerHostility.hasHostility(player);

            if(walkers != null) {
                // Piglins should not attack Piglins or Piglin Brutes, unless they have hostility
                if (walkers.getType().isIn(WalkersEntityTags.PIGLIN_FRIENDLY)) {
                    cir.setReturnValue(false);
                }

                // Player has an Walkers but is not a piglin, check config for what to do
                else {
                    if (WalkersConfig.getInstance().hostilesIgnoreHostileShapedPlayer() && walkers instanceof Monster) {

                        // Check hostility for aggro on non-piglin hostiles
                        if(!hasHostility) {
                            cir.setReturnValue(false);
                        } else {
                            cir.setReturnValue(true);
                        }
                    }
                }
            }
        }
    }
}
