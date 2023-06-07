package draylar.identity.mixin.player;

import draylar.identity.impl.PlayerDataProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayerDataCacheMixin {

    @Shadow @Final private MinecraftClient client;
    @Unique private PlayerDataProvider dataCache = null;

    // This inject caches the custom data attached to this client's player before it is reset when changing dimensions.
    // For example, switching from The End => Overworld will reset the player's NBT.
    @Inject(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;createPlayer(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)Lnet/minecraft/client/network/ClientPlayerEntity;"))
    private void beforePlayerReset(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        dataCache = ((PlayerDataProvider) client.player);
    }

    // This inject applies data cached from the previous inject.
    // Re-applying on the client will help to prevent sync blips which occur when wiping data and waiting for the server to send a sync packet.
    @Inject(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getRegistryKey()Lnet/minecraft/registry/RegistryKey;", ordinal = 1))
    private void afterPlayerReset(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        if(dataCache != null && client.player != null) {
            ((PlayerDataProvider) client.player).setIdentity(dataCache.getIdentity());
            ((PlayerDataProvider) client.player).setUnlocked(dataCache.getUnlocked());
            ((PlayerDataProvider) client.player).setFavorites(dataCache.getFavorites());
            ((PlayerDataProvider) client.player).setAbilityCooldown(dataCache.getAbilityCooldown());
            ((PlayerDataProvider) client.player).setRemainingHostilityTime(dataCache.getRemainingHostilityTime());
        }

        dataCache = null;
    }
}
