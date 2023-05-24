package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.registry.WalkersEntityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlayerSwimmingMixin {

    @Inject(
            method = "swimUpward", at = @At("HEAD"), cancellable = true)
    private void onGolemSwimUp(TagKey<Fluid> fluid, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if(thisEntity instanceof PlayerEntity player) {
            LivingEntity walkers = PlayerWalkers.getCurrentShape(player);

            if(walkers != null && walkers.getType().isIn(WalkersEntityTags.CANT_SWIM)) {
                ci.cancel();
            }
        }
    }
}
