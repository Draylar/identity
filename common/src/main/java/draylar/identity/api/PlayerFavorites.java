package draylar.identity.api;

import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.impl.FavoritePackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;

public class PlayerFavorites {

    public static boolean has(PlayerEntity player, IdentityType type) {
        return type.getEntityType().equals(EntityType.PLAYER) || getFavorites(player).contains(type);
    }

    public static void favorite(ServerPlayerEntity player, IdentityType type) {
        if(!getFavorites(player).contains(type)) {
            getFavorites(player).add(type);
            PlayerAbilities.sync(player);
        }

        sync(player);
    }

    public static void unfavorite(ServerPlayerEntity player, IdentityType type) {
        if(getFavorites(player).contains(type)) {
            getFavorites(player).remove(type);
            PlayerAbilities.sync(player);
        }

        sync(player);
    }

    public static Set<IdentityType<?>> getFavorites(PlayerEntity player) {
        return ((PlayerDataProvider) player).getFavorites();
    }

    public static void sync(ServerPlayerEntity player) {
        FavoritePackets.sendFavoriteSync(player);
    }
}
