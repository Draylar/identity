package draylar.identity.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class FlightHelper {

    @ExpectPlatform
    public static void grantFlightTo(ServerPlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean hasFlight(ServerPlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void revokeFlight(ServerPlayerEntity player) {
        throw new AssertionError();
    }
}
