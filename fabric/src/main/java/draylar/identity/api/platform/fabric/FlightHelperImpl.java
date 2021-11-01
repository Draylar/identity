package draylar.identity.api.platform.fabric;

import draylar.identity.api.Implements;
import draylar.identity.api.platform.FlightHelper;
import draylar.identity.fabric.IdentityFabric;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Implements(FlightHelper.class)
public class FlightHelperImpl {

    public static void grantFlightTo(ServerPlayerEntity player) {
        IdentityFabric.ABILITY_SOURCE.grantTo(player, VanillaAbilities.ALLOW_FLYING);
    }

    public static boolean hasFlight(ServerPlayerEntity player) {
        return IdentityFabric.ABILITY_SOURCE.grants(player, VanillaAbilities.ALLOW_FLYING);
    }

    public static void revokeFlight(ServerPlayerEntity player) {
        IdentityFabric.ABILITY_SOURCE.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
    }
}
