package tocraft.walkers.fabric;

import tocraft.walkers.WalkersClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WalkersFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new WalkersClient().initialize();
    }
}
