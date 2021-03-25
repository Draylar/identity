package draylar.identity.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworking implements NetworkHandler {

    public static void sendAbilityRequest() {
        ClientPlayNetworking.send(USE_ABILITY, new PacketByteBuf(Unpooled.buffer()));
    }

    public static void init() {

    }

    private ClientNetworking() {
        // NO-OP
    }
}
