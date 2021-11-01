package draylar.identity.api.platform.forge;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.Implements;
import draylar.identity.api.platform.PlayerAbilities;
import draylar.identity.api.platform.PlayerFavorites;
import draylar.identity.forge.impl.PlayerDataProvider;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

@Implements(PlayerFavorites.class)
public class PlayerFavoritesImpl {

    public static boolean has(PlayerEntity player, EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        return type.equals(EntityType.PLAYER) || getFavorites(player).contains(id);
    }

    public static void favorite(ServerPlayerEntity player, EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        if(!getFavorites(player).contains(id)) {
            getFavorites(player).add(id);
            PlayerAbilities.sync(player);
        }

        sync(player);
    }

    public static void unfavorite(ServerPlayerEntity player, EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        if(getFavorites(player).contains(id)) {
            getFavorites(player).remove(id);
            PlayerAbilities.sync(player);
        }

        sync(player);
    }

    public static List<Identifier> getFavorites(PlayerEntity player) {
        return ((PlayerDataProvider) player).getFavorites();
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        List<Identifier> favorites = ((PlayerDataProvider) player).getFavorites();
        NbtCompound tag = new NbtCompound();
        NbtList idList = new NbtList();
        favorites.forEach(entityId -> idList.add(NbtString.of(entityId.toString())));
        tag.put("FavoriteIdentities", idList);
        packet.writeNbt(tag);
        NetworkManager.sendToPlayer(player, NetworkHandler.FAVORITE_SYNC, packet);
    }
}
