package draylar.identity.mixin;

import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlayerSwimmingMixin {

    @Inject(
            method = "swimUpward", at = @At("HEAD"), cancellable = true)
    private void onGolemSwimUp(Tag<Fluid> fluid, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if(thisEntity instanceof PlayerEntity) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get((PlayerEntity) thisEntity).getIdentity();

            if(identity != null && EntityTags.CANT_SWIM.contains(identity.getType())) {
                ci.cancel();
            }
        }
    }
}
