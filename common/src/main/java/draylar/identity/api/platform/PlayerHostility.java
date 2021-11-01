package draylar.identity.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerHostility {

    @ExpectPlatform
    public static boolean hasHostility(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void set(PlayerEntity player, int hostilityTime) {
        throw new AssertionError();
    }
}
