package tocraft.walkers.forge;

import dev.architectury.platform.Platform;
import tocraft.walkers.Walkers;
import tocraft.walkers.forge.config.ConfigLoader;
import tocraft.walkers.forge.config.WalkersForgeConfig;
import net.minecraftforge.fml.common.Mod;

@Mod("walkers")
public class WalkersForge {

    public static final WalkersForgeConfig CONFIG = ConfigLoader.read();

    public WalkersForge() {
        new Walkers().initialize();

        if(Platform.getEnv().isClient()) {
            new WalkersForgeClient();
        }
    }
}
