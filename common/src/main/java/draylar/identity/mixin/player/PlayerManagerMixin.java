package draylar.identity.mixin.player;

import draylar.identity.api.PlayerFavorites;
import draylar.identity.api.PlayerHostility;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "respawnPlayer", at = @At(value = "RETURN"))
    private void sendResyncPacketOnRespawn(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        PlayerUnlocks.sync(player);
        PlayerFavorites.sync(player);
        PlayerIdentity.sync(player);
    }
}
