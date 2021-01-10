package draylar.identity.network;

import draylar.identity.IdentityClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworking implements NetworkHandler {

    public static void sendAbilityRequest() {
        ClientPlayNetworking.send(USE_ABILITY, new PacketByteBuf(Unpooled.buffer()));
    }

    public static void init() {
        ClientSidePacketRegistry.INSTANCE.register(CAN_OPEN_MENU, ((context, packet) -> {
            IdentityClient.enableMenu = packet.readBoolean();
            IdentityClient.showNametags = packet.readBoolean();
        }));
    }

    private ClientNetworking() {
        // NO-OP
    }
}
