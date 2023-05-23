package tocraft.walkers.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.SwapPackets;
import net.minecraft.client.MinecraftClient;

public class TransformKeyPressHandler implements ClientTickEvent.Client {
    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.TRANSFORM_KEY.wasPressed()) {
            for (WalkersType<?> unlocked : ((PlayerDataProvider) client.player).getUnlocked()) {
                if (PlayerWalkers.getWalkers(client.player) == null)
                    SwapPackets.sendSwapRequest(unlocked);
                else
                    SwapPackets.sendSwapRequest(null);
                break;
            }
        }
    }
}
