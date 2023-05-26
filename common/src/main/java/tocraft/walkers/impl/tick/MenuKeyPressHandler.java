package tocraft.walkers.impl.tick;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerUnlocks;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.screen.WalkersHelpScreen;
import tocraft.walkers.screen.WalkersScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class MenuKeyPressHandler implements ClientTickEvent.Client {

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.MENU_KEY.wasPressed()) {
            if (((PlayerDataProvider) client.player).get2ndShape() == null)
                MinecraftClient.getInstance().setScreen(new WalkersScreen());
            else
                MinecraftClient.getInstance().setScreen(new WalkersHelpScreen());
        }
    }
}
