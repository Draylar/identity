package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

public class SwapPackets {

    public static void registerIdentityRequestPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.IDENTITY_REQUEST, (buf, context) -> {
            boolean validType = buf.readBoolean();
            if(validType) {
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(buf.readIdentifier());
                int variant = buf.readInt();

                context.getPlayer().getServer().execute(() -> {
                    // Ensure player has permission to switch identities
                    if(IdentityConfig.getInstance().enableSwaps() || context.getPlayer().hasPermissionLevel(3)) {
                        // player type shouldn't be sent, but we still check regardless
                        if(entityType.equals(EntityType.PLAYER)) {
                            PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null, null);
                        } else {
                            @Nullable IdentityType<LivingEntity> type = IdentityType.from(entityType, variant);
                            if(type != null) {
                                PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), type, type.create(context.getPlayer().getWorld()));
                            }
                        }

                        // Refresh player dimensions
                        context.getPlayer().calculateDimensions();
                    }
                });
            } else {
                // Swap back to player if server allows it
                context.getPlayer().getServer().execute(() -> {
                    if(IdentityConfig.getInstance().enableSwaps() || context.getPlayer().hasPermissionLevel(3)) {
                        PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null, null);
                    }

                    context.getPlayer().calculateDimensions();
                });
            }
        });
    }

    public static void sendSwapRequest(@Nullable IdentityType<?> type) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeBoolean(type != null);
        if(type != null) {
            packet.writeIdentifier(Registries.ENTITY_TYPE.getId(type.getEntityType()));
            packet.writeInt(type.getVariantData());
        }

        NetworkManager.sendToServer(ClientNetworking.IDENTITY_REQUEST, packet);
    }
}
