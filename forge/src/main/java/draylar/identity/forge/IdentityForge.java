package draylar.identity.forge;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import draylar.identity.Identity;
import draylar.identity.api.platform.forge.PlayerFavoritesImpl;
import draylar.identity.api.platform.forge.PlayerUnlocksImpl;
import draylar.identity.forge.config.ConfigLoader;
import draylar.identity.forge.config.IdentityForgeConfig;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.common.Mod;

@Mod("identity")
public class IdentityForge {

    public static final IdentityForgeConfig CONFIG = ConfigLoader.read();

    public IdentityForge() {
        new Identity().initialize();

        PlayerEvent.PLAYER_JOIN.register(player -> {
            // Send config sync packet
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBoolean(CONFIG.enableClientSwapMenu);
            packet.writeBoolean(CONFIG.showPlayerNametag);
            NetworkManager.sendToPlayer(player, NetworkHandler.CONFIG_SYNC, packet);

            // Sync unlocked Identity
            PlayerUnlocksImpl.sync(player);
            
            // Sync favorites
            PlayerFavoritesImpl.sync(player);
        });

        if(Platform.getEnv().isClient()) {
            new IdentityForgeClient();
        }
    }
}
