package tocraft.walkers.api.platform.fabric;

import tocraft.walkers.fabric.WalkersFabric;
import tocraft.walkers.api.platform.WalkersConfig;

public class WalkersConfigImpl {

    public static WalkersConfig getInstance() {
        return WalkersFabric.CONFIG;
    }
}
