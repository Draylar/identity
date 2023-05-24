package tocraft.walkers.network;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.ApplicablePacket;
import tocraft.walkers.impl.DimensionsRefresher;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.UnlockPackets;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ClientNetworking implements NetworkHandler {

    public static void registerPacketHandlers() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.WALKERS_SYNC, ClientNetworking::handleWalkersSyncPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.ABILITY_SYNC, ClientNetworking::handleAbilitySyncPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.UNLOCK_SYNC, UnlockPackets::handleUnlockSyncPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.CONFIG_SYNC, ClientNetworking::handleConfigurationSyncPacket);
    }

    public static void runOrQueue(NetworkManager.PacketContext context, ApplicablePacket packet) {
        if(context.getPlayer() == null) {
            WalkersClient.getSyncPacketQueue().add(packet);
        } else {
            context.queue(() -> packet.apply(context.getPlayer()));
        }
    }

    public static void sendAbilityRequest() {
        NetworkManager.sendToServer(USE_ABILITY, new PacketByteBuf(Unpooled.buffer()));
    }

    public static void handleWalkersSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        final UUID uuid = packet.readUuid();
        final String id = packet.readString();
        final NbtCompound entityNbt = packet.readNbt();

        runOrQueue(context, player -> {
            @Nullable PlayerEntity syncTarget = player.getEntityWorld().getPlayerByUuid(uuid);

            if(syncTarget != null) {
                PlayerDataProvider data = (PlayerDataProvider) syncTarget;

                // set walkers to null (no walkers) if the entity id is "minecraft:empty"
                if(id.equals("minecraft:empty")) {
                    data.setCurrentShape(null);
                    ((DimensionsRefresher) syncTarget).walkers_refreshDimensions();
                    return;
                }

                // If entity type was valid, deserialize entity data from tag/
                if(entityNbt != null) {
                    entityNbt.putString("id", id);
                    Optional<EntityType<?>> type = EntityType.fromNbt(entityNbt);
                    if(type.isPresent()) {
                        LivingEntity walkers = data.getCurrentShape();

                        // ensure entity data exists
                        if(walkers == null || !type.get().equals(walkers.getType())) {
                            walkers = (LivingEntity) type.get().create(syncTarget.world);
                            data.setCurrentShape(walkers);

                            // refresh player dimensions/hitbox on client
                            ((DimensionsRefresher) syncTarget).walkers_refreshDimensions();
                        }

                        if(walkers != null) {
                            walkers.readNbt(entityNbt);
                        }
                    }
                }
            }
        });
    }

    public static void handleAbilitySyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        int cooldown = packet.readInt();
        runOrQueue(context, player -> ((PlayerDataProvider) player).setAbilityCooldown(cooldown));
    }

    public static void handleConfigurationSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        boolean showPlayerNametag = packet.readBoolean();

        // TODO: re-handle sync packet
//        WalkersConfig.getInstance().showPlayerNametag() = showPlayerNametag;
        // TODO: UNDO THIS WHEN THE PLAYER LEAVES - OMEGA CONFIG HANDLES THIS, BUT OUR BUDGET FORGE IMPLEMENTATION DOES NOT
    }

    private ClientNetworking() {
        // NO-OP
    }
}
