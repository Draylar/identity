package draylar.identity.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerFavorites {

    @ExpectPlatform
    public static boolean has(PlayerEntity player, EntityType<?> type) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void favorite(ServerPlayerEntity player, EntityType<?> type) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void unfavorite(ServerPlayerEntity player, EntityType<?> type) {
        throw new AssertionError();
    }
}
