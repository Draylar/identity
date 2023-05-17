package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.PlayerFavorites;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class FavoritePackets {

    public static void sendFavoriteRequest(WalkersType<?> type, boolean favorite) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(Registry.ENTITY_TYPE.getId(type.getEntityType()));
        packet.writeInt(type.getVariantData());
        packet.writeBoolean(favorite);
        NetworkManager.sendToServer(ClientNetworking.FAVORITE_UPDATE, packet);
    }

    public static void registerFavoriteRequestHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.FAVORITE_UPDATE, (buf, context) -> {
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(buf.readIdentifier());
            int variant = buf.readInt();
            boolean favorite = buf.readBoolean();
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                @Nullable WalkersType<?> type = WalkersType.from(entityType, variant);

                if(type != null) {
                    if(favorite) {
                        PlayerFavorites.favorite(player, type);
                    } else {
                        PlayerFavorites.unfavorite(player, type);
                    }
                }
            });
        });
    }

    public static void sendFavoriteSync(ServerPlayerEntity player) {
        Set<WalkersType<?>> favorites = ((PlayerDataProvider) player).getFavorites();
        NbtCompound tag = new NbtCompound();
        NbtList idList = new NbtList();
        favorites.forEach(type -> idList.add(type.writeCompound()));
        tag.put("FavoriteIdentities", idList);

        // Create & send packet with NBT
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeNbt(tag);
        NetworkManager.sendToPlayer(player, NetworkHandler.FAVORITE_SYNC, packet);
    }

    public static void handleFavoriteSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        NbtCompound tag = packet.readNbt();

        ClientNetworking.runOrQueue(context, player -> {
            PlayerDataProvider data = (PlayerDataProvider) player;
            data.getFavorites().clear();
            NbtList idList = tag.getList("FavoriteIdentities", NbtElement.COMPOUND_TYPE);
            idList.forEach(compound -> data.getFavorites().add(WalkersType.from((NbtCompound) compound)));
        });
    }
}
