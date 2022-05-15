package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
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
    private static void shouldAttackIdentity(PiglinEntity piglin, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        boolean shouldAttack = cir.getReturnValue();

        if(shouldAttack && target instanceof PlayerEntity) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(target).getIdentity();
            boolean hasHostility = Components.HOSTILITY.get(target).hasHostility();

            if(identity != null) {
                // Piglins should not attack Piglins or Piglin Brutes, unless they have hostility
                if (identity.getType().isIn(EntityTags.PIGLIN_FRIENDLY)) {
                    cir.setReturnValue(false);
                }

                // Player has an Identity but is not a piglin, check config for what to do
                else {
                    if (Identity.CONFIG.hostilesIgnoreHostileIdentityPlayer && identity instanceof Monster) {

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
