package tocraft.walkers.impl.join;

import dev.architectury.event.events.client.ClientPlayerEvent;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.ApplicablePacket;
import net.minecraft.client.network.ClientPlayerEntity;

public class ClientPlayerJoinHandler implements ClientPlayerEvent.ClientPlayerJoin {

    @Override
    public void join(ClientPlayerEntity player) {
        for (ApplicablePacket packet : WalkersClient.getSyncPacketQueue()) {
            packet.apply(player);
        }

        WalkersClient.getSyncPacketQueue().clear();
    }
}
