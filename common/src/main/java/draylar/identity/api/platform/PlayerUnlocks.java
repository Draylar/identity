package draylar.identity.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerUnlocks {

    @ExpectPlatform
    public static boolean unlock(ServerPlayerEntity player, EntityType<?> granted) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean has(PlayerEntity player, EntityType<?> granted) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void revoke(ServerPlayerEntity player, EntityType<?> type) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sync(ServerPlayerEntity player) {
        throw new AssertionError();
    }
}
