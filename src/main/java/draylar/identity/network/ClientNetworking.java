package draylar.identity.network;

import draylar.identity.IdentityClient;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class ClientNetworking implements NetworkHandler {

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
