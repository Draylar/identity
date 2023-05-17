package tocraft.walkers.api;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.NetworkHandler;
import tocraft.walkers.network.impl.FavoritePackets;
import io.netty.buffer.Unpooled;
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
import java.util.Set;

public class PlayerFavorites {

    public static boolean has(PlayerEntity player, WalkersType type) {
        return type.getEntityType().equals(EntityType.PLAYER) || getFavorites(player).contains(type);
    }

    public static void favorite(ServerPlayerEntity player, WalkersType type) {
        if(!getFavorites(player).contains(type)) {
            getFavorites(player).add(type);
            PlayerAbilities.sync(player);
        }

        sync(player);
    }

    public static void unfavorite(ServerPlayerEntity player, WalkersType type) {
        if(getFavorites(player).contains(type)) {
            getFavorites(player).remove(type);
            PlayerAbilities.sync(player);
        }

        sync(player);
    }

    public static Set<WalkersType<?>> getFavorites(PlayerEntity player) {
        return ((PlayerDataProvider) player).getFavorites();
    }

    public static void sync(ServerPlayerEntity player) {
        FavoritePackets.sendFavoriteSync(player);
    }
}
