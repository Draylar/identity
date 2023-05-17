package tocraft.walkers.mixin.player;

import tocraft.walkers.api.PlayerFavorites;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.PlayerUnlocks;
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
        PlayerWalkers.sync(player);
    }
}
