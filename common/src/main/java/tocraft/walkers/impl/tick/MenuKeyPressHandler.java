package tocraft.walkers.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.screen.WalkersScreen;
import net.minecraft.client.MinecraftClient;

public class MenuKeyPressHandler implements ClientTickEvent.Client {

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.MENU_KEY.wasPressed()) {
            MinecraftClient.getInstance().setScreen(new WalkersScreen());
        }
    }
}
