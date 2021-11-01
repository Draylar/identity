package draylar.identity.api.platform.fabric;

import draylar.identity.fabric.registry.Components;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerFavoritesImpl {

    public static boolean has(PlayerEntity player, EntityType<?> type) {
        return Components.FAVORITE_IDENTITIES.get(player).has(type);
    }

    public static void favorite(ServerPlayerEntity player, EntityType<?> type) {
        Components.FAVORITE_IDENTITIES.get(player).favorite(type);
    }

    public static void unfavorite(ServerPlayerEntity player, EntityType<?> type) {
        Components.FAVORITE_IDENTITIES.get(player).unfavorite(type);
    }
}
