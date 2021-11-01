package draylar.identity.forge;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.NbtType;
import draylar.identity.IdentityClient;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.forge.impl.PlayerDataProvider;
import draylar.identity.network.NetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class IdentityForgeClient {

    public IdentityForgeClient() {
        new IdentityClient().initialize();

        // TODO: merge with IdentityComponent#readFromNbt
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.IDENTITY_SYNC, IdentityForgeClient::handleIdentitySyncPacket);

        // TODO: merge with FavoriteIdentitiesComponent#readFromNbt
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.FAVORITE_SYNC, IdentityForgeClient::handleFavoriteSyncPacket);

        // TODO: merge with AbilityComponent#readFromNbt
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.ABILITY_SYNC, IdentityForgeClient::handleAbilitySyncPacket);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.UNLOCK_SYNC, IdentityForgeClient::handleUnlockSyncPacket);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.CONFIG_SYNC, IdentityForgeClient::handleConfigurationSyncPacket);
    }

    private static void handleUnlockSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        PlayerEntity player = context.getPlayer();
        ((PlayerDataProvider) player).getUnlocked().clear();
        NbtCompound nbt = packet.readNbt();
        NbtList list = nbt.getList("UnlockedMorphs", NbtType.STRING);

        MinecraftClient.getInstance().execute(() -> {
            list.forEach(idTag -> {
                ((PlayerDataProvider) player).getUnlocked().add(new Identifier(idTag.asString()));
            });
        });
    }

    private static void handleIdentitySyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        final String id = packet.readString();
        final NbtCompound entityNbt = packet.readNbt();

        MinecraftClient.getInstance().execute(() -> {
            PlayerEntity player = context.getPlayer();
            PlayerDataProvider data = (PlayerDataProvider) player;

            // set identity to null (no identity) if the entity id is "minecraft:empty"
            if(id.equals("minecraft:empty")) {
                data.setIdentity(null);
                ((DimensionsRefresher) player).identity_refreshDimensions();
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
                        identity = (LivingEntity) type.get().create(player.world);
                        data.setIdentity(identity);

                        // refresh player dimensions/hitbox on client
                        ((DimensionsRefresher) player).identity_refreshDimensions();
                    }

                    if(identity != null) {
                        identity.readNbt(entityNbt);
                    }
                }
            }
        });
    }

    private static void handleFavoriteSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        NbtCompound tag = packet.readNbt();

        MinecraftClient.getInstance().execute(() -> {
            PlayerDataProvider data = (PlayerDataProvider) context.getPlayer();
            data.getFavorites().clear();
            NbtList idList = tag.getList("FavoriteIdentities", NbtType.STRING);
            idList.forEach(idTag -> data.getFavorites().add(new Identifier(idTag.asString())));
        });
    }

    private static void handleAbilitySyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        int cooldown = packet.readInt();
        ((PlayerDataProvider) context.getPlayer()).setAbilityCooldown(cooldown);
    }

    private static void handleConfigurationSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        boolean enableClientSwapMenu = packet.readBoolean();
        boolean showPlayerNametag = packet.readBoolean();
        IdentityForge.CONFIG.enableClientSwapMenu = enableClientSwapMenu;
        IdentityForge.CONFIG.showPlayerNametag = showPlayerNametag;
        // TODO: UNDO THIS WHEN THE PLAYER LEAVES - OMEGA CONFIG HANDLES THIS, BUT OUR BUDGET FORGE IMPLEMENTATION DOES NOT
    }
}
