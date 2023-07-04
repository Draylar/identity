package draylar.identity.network;

import dev.architectury.networking.NetworkManager;
import draylar.identity.IdentityClient;
import draylar.identity.api.ApplicablePacket;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.impl.FavoritePackets;
import draylar.identity.network.impl.UnlockPackets;
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
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.IDENTITY_SYNC, ClientNetworking::handleIdentitySyncPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.FAVORITE_SYNC, FavoritePackets::handleFavoriteSyncPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.ABILITY_SYNC, ClientNetworking::handleAbilitySyncPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.UNLOCK_SYNC, UnlockPackets::handleUnlockSyncPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.CONFIG_SYNC, ClientNetworking::handleConfigurationSyncPacket);
    }

    public static void runOrQueue(NetworkManager.PacketContext context, ApplicablePacket packet) {
        if(context.getPlayer() == null) {
            IdentityClient.getSyncPacketQueue().add(packet);
        } else {
            context.queue(() -> packet.apply(context.getPlayer()));
        }
    }

    public static void sendAbilityRequest() {
        NetworkManager.sendToServer(USE_ABILITY, new PacketByteBuf(Unpooled.buffer()));
    }

    public static void handleIdentitySyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        final UUID uuid = packet.readUuid();
        final String id = packet.readString();
        final NbtCompound entityNbt = packet.readNbt();

        runOrQueue(context, player -> {
            @Nullable PlayerEntity syncTarget = player.getEntityWorld().getPlayerByUuid(uuid);

            if(syncTarget != null) {
                PlayerDataProvider data = (PlayerDataProvider) syncTarget;

                // set identity to null (no identity) if the entity id is "minecraft:empty"
                if(id.equals("minecraft:empty")) {
                    data.setIdentity(null);
                    ((DimensionsRefresher) syncTarget).identity_refreshDimensions();
                    return;
                }

                // If entity type was valid, deserialize entity data from tag/
                if(entityNbt != null) {
                    entityNbt.putString("id", id);
                    Optional<EntityType<?>> type = EntityType.fromNbt(entityNbt);
                    if(type.isPresent()) {
                        LivingEntity identity = data.getIdentity();

                        // ensure entity data exists
                        if(identity == null || !type.get().equals(identity.getType())) {
                            identity = (LivingEntity) type.get().create(syncTarget.getWorld());
                            data.setIdentity(identity);

                            // refresh player dimensions/hitbox on client
                            ((DimensionsRefresher) syncTarget).identity_refreshDimensions();
                        }

                        if(identity != null) {
                            identity.readNbt(entityNbt);
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
        boolean enableClientSwapMenu = packet.readBoolean();
        boolean showPlayerNametag = packet.readBoolean();

        // TODO: re-handle sync packet
//        IdentityConfig.getInstance().enableClientSwapMenu() = enableClientSwapMenu;
//        IdentityConfig.getInstance().showPlayerNametag() = showPlayerNametag;
        // TODO: UNDO THIS WHEN THE PLAYER LEAVES - OMEGA CONFIG HANDLES THIS, BUT OUR BUDGET FORGE IMPLEMENTATION DOES NOT
    }

    private ClientNetworking() {
        // NO-OP
    }
}
