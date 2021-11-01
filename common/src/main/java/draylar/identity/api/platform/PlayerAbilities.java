package draylar.identity.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerAbilities {

    @ExpectPlatform
    public static int getCooldown(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canUseAbility(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setCooldown(PlayerEntity player, int cooldown) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sync(ServerPlayerEntity player) {
        throw new AssertionError();
    }
}
