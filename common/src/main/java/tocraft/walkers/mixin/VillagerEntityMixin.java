package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerWalkers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @Shadow protected abstract void sayNo();

    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        LivingEntity walkers = PlayerWalkers.getCurrentShape(player);

        if(walkers != null && walkers.isUndead()) {
            this.sayNo();
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
