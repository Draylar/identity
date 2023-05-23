package tocraft.walkers.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.screen.WalkersScreen;
import net.minecraft.client.MinecraftClient;

public class MenuOnJoinHandler implements ClientTickEvent.Client {
    public static boolean menuIsOpen = false;

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;
        if (!menuIsOpen)
            if (client.world != null && !(MinecraftClient.getInstance().currentScreen instanceof WalkersScreen))
                if(WalkersConfig.getInstance().autoUnlockShapes() && ((PlayerDataProvider) client.player).getUnlocked().isEmpty()) {
                    MinecraftClient.getInstance().setScreen(new WalkersScreen());
                }
    }
}
