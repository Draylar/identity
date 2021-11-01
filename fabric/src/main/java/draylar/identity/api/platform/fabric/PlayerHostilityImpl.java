package draylar.identity.api.platform.fabric;

import draylar.identity.fabric.registry.Components;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerHostilityImpl {

    public static boolean hasHostility(PlayerEntity player) {
        return Components.HOSTILITY.get(player).hasHostility();
    }

    public static void set(PlayerEntity player, int hostilityTime) {
        Components.HOSTILITY.get(player).setHostility(hostilityTime);
    }
}
