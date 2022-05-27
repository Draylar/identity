package draylar.identity.mixin;

import draylar.identity.api.PlayerHostility;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.registry.IdentityEntityTags;
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

        if(shouldAttack && target instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);
            boolean hasHostility = PlayerHostility.hasHostility(player);

            if(identity != null) {
                // Piglins should not attack Piglins or Piglin Brutes, unless they have hostility
                if (identity.getType().isIn(IdentityEntityTags.PIGLIN_FRIENDLY)) {
                    cir.setReturnValue(false);
                }

                // Player has an Identity but is not a piglin, check config for what to do
                else {
                    if (IdentityConfig.getInstance().hostilesIgnoreHostileIdentityPlayer() && identity instanceof Monster) {

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
