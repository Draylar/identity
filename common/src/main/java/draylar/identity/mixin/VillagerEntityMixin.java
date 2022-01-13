package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
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
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if(identity != null && identity.isUndead()) {
            this.sayNo();
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
