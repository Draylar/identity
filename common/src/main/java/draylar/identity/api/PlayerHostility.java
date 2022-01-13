package draylar.identity.api;

import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerHostility {

    /**
     * Returns whether the player this component is attached to will be targeted by hostile mobs, regardless of Identity.
     *
     * <p>Hostility works on a timer, and is set when the player attacks a hostile mob.
     *
     * @return  whether this component's player will be targeted by hostile mobs, regardless of Identity
     */
    public static boolean hasHostility(PlayerEntity player) {
        return ((PlayerDataProvider) player).getRemainingHostilityTime() > 0;
    }

    /**
     * Sets this components' hostility timer to the given time in ticks.
     *
     * @param hostilityTime  time, in ticks, to set hostility timer to
     */
    public static void set(PlayerEntity player, int hostilityTime) {
        ((PlayerDataProvider) player).setRemainingHostilityTime(hostilityTime);
    }
}
