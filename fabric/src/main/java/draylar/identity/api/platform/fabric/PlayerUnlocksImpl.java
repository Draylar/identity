package draylar.identity.api.platform.fabric;

import draylar.identity.fabric.registry.Components;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerUnlocksImpl {

    public static boolean unlock(ServerPlayerEntity player, EntityType<?> granted) {
        return Components.UNLOCKED_IDENTITIES.get(player).unlock(granted);
    }

    public static boolean has(PlayerEntity player, EntityType<?> type) {
        return Components.UNLOCKED_IDENTITIES.get(player).has(type);
    }

    public static void revoke(ServerPlayerEntity player, EntityType<?> type) {
        Components.UNLOCKED_IDENTITIES.get(player).revoke(type);
    }

    public static void sync(ServerPlayerEntity player) {
        Components.UNLOCKED_IDENTITIES.sync(player);
    }
}
