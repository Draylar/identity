package tocraft.walkers.api.platform.forge;

import tocraft.walkers.forge.WalkersForge;
import tocraft.walkers.api.platform.WalkersConfig;

public class WalkersConfigImpl {

    public static WalkersConfig getInstance() {
        return WalkersForge.CONFIG;
    }
}
