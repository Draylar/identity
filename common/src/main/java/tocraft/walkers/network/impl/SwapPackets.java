package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.PlayerUnlocks;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class SwapPackets {

    public static void registerWalkersRequestPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.WALKERS_REQUEST, (buf, context) -> {
            boolean validType = buf.readBoolean();
            if(validType) {
                EntityType<?> entityType = Registry.ENTITY_TYPE.get(buf.readIdentifier());
                int variant = buf.readInt();

                context.getPlayer().getServer().execute(() -> {
                    // player type shouldn't be sent, but we still check regardless
                    if(entityType.equals(EntityType.PLAYER)) {
                        PlayerWalkers.updateShapes((ServerPlayerEntity) context.getPlayer(), null, null);
                    } else {
                        @Nullable ShapeType<LivingEntity> type = ShapeType.from(entityType, variant);
                        if(type != null) {
                            // unlock walker
                            PlayerUnlocks.unlock((ServerPlayerEntity) context.getPlayer(), type);
                            // update Player
                            PlayerWalkers.updateShapes((ServerPlayerEntity) context.getPlayer(), type, type.create(context.getPlayer().getWorld()));
                        }
                    }
                    
                    // Refresh player dimensions
                    context.getPlayer().calculateDimensions();
                });
            } else {
                // Swap back to player if server allows it
                context.getPlayer().getServer().execute(() -> {
                    PlayerWalkers.updateShapes((ServerPlayerEntity) context.getPlayer(), null, null);

                    context.getPlayer().calculateDimensions();
                });
            }
        });
    }

    public static void sendSwapRequest(@Nullable ShapeType<?> type) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeBoolean(type != null);
        if(type != null) {
            packet.writeIdentifier(Registry.ENTITY_TYPE.getId(type.getEntityType()));
            packet.writeInt(type.getVariantData());
        }

        NetworkManager.sendToServer(ClientNetworking.WALKERS_REQUEST, packet);
    }
}
