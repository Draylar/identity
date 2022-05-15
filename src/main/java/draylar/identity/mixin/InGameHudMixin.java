package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    // Causes an error. No idea how to fix this.
    /*@ModifyArg(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z")
    )
    private TagKey<Fluid> shouldRenderBreath(TagKey<Fluid> tag) {
        PlayerEntity player = this.getCameraPlayer();
        LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

        if(identity != null) {
            if((Identity.isAquatic(identity) || identity.getType().isIn(EntityTags.UNDROWNABLE)) && player.isSubmergedIn(FluidTags.WATER)) {
                return FluidTags.LAVA;    // will cause isSubmergedIn to return false, preventing air render
            }
        }

        return tag;
    }*/
}
