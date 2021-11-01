package draylar.identity.api.platform.forge;

import draylar.identity.forge.impl.PlayerDataProvider;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerHostilityImpl {

    public static boolean hasHostility(PlayerEntity player) {
        return ((PlayerDataProvider) player).getRemainingHostilityTime() > 0;
    }
}
