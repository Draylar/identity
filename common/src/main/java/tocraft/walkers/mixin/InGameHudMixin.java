package tocraft.walkers.mixin;

import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.registry.WalkersEntityTags;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @ModifyArg(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/tag/TagKey;)Z")
    )
    private TagKey<Fluid> shouldRenderBreath(TagKey<Fluid> tag) {
        PlayerEntity player = this.getCameraPlayer();
        LivingEntity walkers = PlayerWalkers.getCurrentShape(player);

        if(walkers != null) {
            if(Walkers.isAquatic(walkers) || walkers.getType().isIn(WalkersEntityTags.UNDROWNABLE) && player.isSubmergedIn(FluidTags.WATER)) {
                return FluidTags.LAVA;    // will cause isSubmergedIn to return false, preventing air render
            }
        }

        return tag;
    }
}
