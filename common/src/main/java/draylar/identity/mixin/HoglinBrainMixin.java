package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HoglinBrain;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoglinBrain.class)
public class HoglinBrainMixin {

    @Inject(
            method = "getNearestVisibleTargetablePlayer",
            at = @At("RETURN"),
    cancellable = true)
    private static void getNearestVisibleTargetablePlayer(HoglinEntity hoglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Optional<? extends LivingEntity> ret = cir.getReturnValue();
        if(ret.isPresent()) {
            LivingEntity target = ret.get();

            // Check if Hoglin target is player
            if(target instanceof PlayerEntity player) {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                // Ensure player identity is valid
                if(identity != null) {
                    if(identity instanceof HoglinEntity) {
                        cir.setReturnValue(Optional.empty());
                    }
                }
            }
        }
    }
}
