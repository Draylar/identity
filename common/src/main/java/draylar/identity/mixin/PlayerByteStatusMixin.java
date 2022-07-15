package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
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

    // When a player receives a handleStatus byte, pass it on to their Identity.
    @Inject(method = "handleStatus", at = @At("RETURN"))
    private void identity$passByteStatus(byte status, CallbackInfo ci) {
        @Nullable LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);
        if(identity != null) {
            identity.handleStatus(status);
        }
    }
}
