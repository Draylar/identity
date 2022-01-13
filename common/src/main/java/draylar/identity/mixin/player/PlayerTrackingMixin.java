package draylar.identity.mixin.player;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public class PlayerTrackingMixin {

    @Shadow @Final private Entity entity;

    @Inject(method = "startTracking", at = @At("RETURN"))
    private void sendTrackingIdentityPackets(ServerPlayerEntity newlyTracked, CallbackInfo ci) {
        if(this.entity instanceof ServerPlayerEntity player) {
            PlayerIdentity.sync(newlyTracked, player);
            PlayerIdentity.sync(player, newlyTracked);
        }
    }
}
