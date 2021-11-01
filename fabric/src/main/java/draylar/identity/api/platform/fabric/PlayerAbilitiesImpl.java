package draylar.identity.api.platform.fabric;

import draylar.identity.api.Implements;
import draylar.identity.api.platform.PlayerAbilities;
import draylar.identity.fabric.registry.Components;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Implements(PlayerAbilities.class)
public class PlayerAbilitiesImpl {

    public static int getCooldown(PlayerEntity player) {
        return Components.ABILITY.get(player).getCooldown();
    }

    public static boolean canUseAbility(PlayerEntity player) {
        return Components.ABILITY.get(player).canUseAbility();
    }

    public static void setCooldown(PlayerEntity player, int cooldown) {
        Components.ABILITY.get(player).setCooldown(cooldown);
    }

    public static void sync(ServerPlayerEntity player) {
        Components.ABILITY.sync(player);
    }
}
