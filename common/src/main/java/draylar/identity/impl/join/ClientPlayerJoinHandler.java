package draylar.identity.impl.join;

import dev.architectury.event.events.client.ClientPlayerEvent;
import draylar.identity.IdentityClient;
import draylar.identity.api.ApplicablePacket;
import net.minecraft.client.network.ClientPlayerEntity;

public class ClientPlayerJoinHandler implements ClientPlayerEvent.ClientPlayerJoin {

    @Override
    public void join(ClientPlayerEntity player) {
        for (ApplicablePacket packet : IdentityClient.getSyncPacketQueue()) {
            packet.apply(player);
        }

        IdentityClient.getSyncPacketQueue().clear();
    }
}
