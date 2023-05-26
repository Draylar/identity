package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntity.class)
public class PlayerByteStatusMixin {

    // When a player receives a handleStatus byte, pass it on to their Walkers.
    @Inject(method = "handleStatus", at = @At("RETURN"))
    private void walkers$passByteStatus(byte status, CallbackInfo ci) {
        @Nullable LivingEntity walkers = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);
        if(walkers != null) {
            walkers.handleStatus(status);
        }
    }
}
