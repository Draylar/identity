package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.event.PlayerJoinCallback;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.registry.Components;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
            ordinal = 0
    ), method = "onPlayerConnect")
    private void connect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerJoinCallback.EVENT.invoker().onPlayerJoin(player);
    }

    @Inject(
            method = "respawnPlayer",
            at = @At("RETURN")
    )
    private void onRespawn(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

        // refresh entity hitbox dimensions after death
        ((DimensionsRefresher) player).identity_refreshDimensions();

        // Re-sync max health for identity
        if (identity != null && Identity.CONFIG.scalingHealth) {
            player.setHealth(Math.min(player.getHealth(), identity.getMaxHealth()));
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.min(Identity.CONFIG.maxHealth, identity.getMaxHealth()));
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new EntityAttributesS2CPacket(player.getEntityId(), player.getAttributes().getAttributesToSend()));
        }
    }
}
