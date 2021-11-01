package draylar.identity.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworking implements NetworkHandler {

    public static void sendAbilityRequest() {
        NetworkManager.sendToServer(USE_ABILITY, new PacketByteBuf(Unpooled.buffer()));
    }

    private ClientNetworking() {
        // NO-OP
    }
}
