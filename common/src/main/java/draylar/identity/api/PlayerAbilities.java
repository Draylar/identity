package draylar.identity.api;

import dev.architectury.networking.NetworkManager;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerAbilities {

    /**
     * Returns an integer representing the current ability cooldown of the specified {@link PlayerEntity} in ticks.
     *
     * <p>
     * A return value of {@code 0} represents no cooldown, while 20 is 1 second.
     *
     * @param player player to retrieve ability cooldown for
     * @return cooldown, in ticks, of the specified player's ability
     */
    public static int getCooldown(PlayerEntity player) {
        return ((PlayerDataProvider) player).getAbilityCooldown();
    }

    public static boolean canUseAbility(PlayerEntity player) {
        return ((PlayerDataProvider) player).getAbilityCooldown() <= 0;
    }

    public static void setCooldown(PlayerEntity player, int cooldown) {
        ((PlayerDataProvider) player).setAbilityCooldown(cooldown);
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeInt(((PlayerDataProvider) player).getAbilityCooldown());
        NetworkManager.sendToPlayer(player, NetworkHandler.ABILITY_SYNC, packet);
    }
}
