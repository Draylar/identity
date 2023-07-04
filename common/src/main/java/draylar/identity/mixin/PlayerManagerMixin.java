package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.event.PlayerJoinCallback;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.impl.DimensionsRefresher;
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
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
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
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        // refresh entity hitbox dimensions after death
        ((DimensionsRefresher) player).identity_refreshDimensions();

        // Re-sync max health for identity
        if(identity != null && IdentityConfig.getInstance().scalingHealth()) {
            player.setHealth(Math.min(player.getHealth(), identity.getMaxHealth()));
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.min(IdentityConfig.getInstance().maxHealth(), identity.getMaxHealth()));
            player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getId(), player.getAttributes().getAttributesToSend()));
        }
    }
}
