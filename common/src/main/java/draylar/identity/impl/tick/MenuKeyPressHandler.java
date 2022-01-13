package draylar.identity.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import draylar.identity.IdentityClient;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.screen.IdentityScreen;
import net.minecraft.client.MinecraftClient;

public class MenuKeyPressHandler implements ClientTickEvent.Client {

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(IdentityClient.MENU_KEY.wasPressed()) {
            if(IdentityConfig.getInstance().enableClientSwapMenu() || client.player.hasPermissionLevel(3)) {
                MinecraftClient.getInstance().setScreen(new IdentityScreen());
            }
        }
    }
}
