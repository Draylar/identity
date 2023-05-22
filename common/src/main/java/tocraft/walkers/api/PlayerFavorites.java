package tocraft.walkers.api;

import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.FavoritePackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
