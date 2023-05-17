package tocraft.walkers.fabric;

import tocraft.walkers.Walkers;
import tocraft.walkers.fabric.config.WalkersFabricConfig;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;

public class WalkersFabric implements ModInitializer {

    public static final WalkersFabricConfig CONFIG = OmegaConfig.register(WalkersFabricConfig.class);

    @Override
    public void onInitialize() {
        new Walkers().initialize();
    }
}
