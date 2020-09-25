package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Redirect(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z")
    )
    private boolean shouldRenderBreath(PlayerEntity player, Tag<Fluid> tag) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

        if(identity != null) {
            if(Identity.isAquatic(identity) && player.isSubmergedIn(FluidTags.WATER)) {
                return false;
            }
        }

        return player.isSubmergedIn(FluidTags.WATER);
    }
}
