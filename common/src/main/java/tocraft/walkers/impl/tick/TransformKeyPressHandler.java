package tocraft.walkers.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.SwapPackets;
import net.minecraft.client.MinecraftClient;

public class TransformKeyPressHandler implements ClientTickEvent.Client {
    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.TRANSFORM_KEY.wasPressed()) {
            if (PlayerWalkers.getCurrentShape(client.player) == null)
                SwapPackets.sendSwapRequest(((PlayerDataProvider) client.player).get2ndShape());
            else
                SwapPackets.sendSwapRequest(null);
        }
    }
}
