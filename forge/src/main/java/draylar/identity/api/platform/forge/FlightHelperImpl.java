package draylar.identity.api.platform.forge;

import draylar.identity.api.Implements;
import draylar.identity.api.platform.FlightHelper;
import net.minecraft.server.network.ServerPlayerEntity;

@Implements(FlightHelper.class)
public class FlightHelperImpl {

    public static void grantFlightTo(ServerPlayerEntity player) {
        player.getAbilities().allowFlying = true;
    }

    public static boolean hasFlight(ServerPlayerEntity player) {
        return player.getAbilities().allowFlying;
    }

    public static void revokeFlight(ServerPlayerEntity player) {
        player.getAbilities().allowFlying = false;
        player.getAbilities().flying = false;
    }
}
