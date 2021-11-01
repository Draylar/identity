package draylar.identity.api.platform.forge;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.Implements;
import draylar.identity.api.platform.PlayerAbilities;
import draylar.identity.forge.impl.PlayerDataProvider;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

@Implements(PlayerAbilities.class)
public class PlayerAbilitiesImpl {

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
