package draylar.identity.api.platform.fabric;

import draylar.identity.api.Implements;
import draylar.identity.api.platform.PlayerIdentity;
import draylar.identity.fabric.registry.Components;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Implements(PlayerIdentity.class)
public class PlayerIdentityImpl {

    public static LivingEntity getIdentity(PlayerEntity player) {
        return Components.CURRENT_IDENTITY.get(player).getIdentity();
    }

    public static boolean updateIdentity(ServerPlayerEntity player, LivingEntity entity) {
        return Components.CURRENT_IDENTITY.get(player).setIdentity(entity);
    }

    public static void sync(ServerPlayerEntity player) {
        Components.CURRENT_IDENTITY.sync(player);
    }
}
