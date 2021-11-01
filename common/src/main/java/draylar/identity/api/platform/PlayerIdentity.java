package draylar.identity.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerIdentity {

    @ExpectPlatform
    public static LivingEntity getIdentity(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean updateIdentity(ServerPlayerEntity player, LivingEntity instanced) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sync(ServerPlayerEntity player) {
        throw new AssertionError();
    }
}
