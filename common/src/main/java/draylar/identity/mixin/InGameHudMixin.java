package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.registry.IdentityEntityTags;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @ModifyArg(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    private TagKey<Fluid> shouldRenderBreath(TagKey<Fluid> tag) {
        PlayerEntity player = this.getCameraPlayer();
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if(identity != null) {
            if(Identity.isAquatic(identity) || identity.getType().isIn(IdentityEntityTags.UNDROWNABLE) && player.isSubmergedIn(FluidTags.WATER)) {
                return FluidTags.LAVA;    // will cause isSubmergedIn to return false, preventing air render
            }
        }

        return tag;
    }
}
