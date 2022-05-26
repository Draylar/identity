package draylar.identity.fabric;

import draylar.identity.Identity;
import draylar.identity.fabric.config.IdentityFabricConfig;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;

public class IdentityFabric implements ModInitializer {

    public static final IdentityFabricConfig CONFIG = OmegaConfig.register(IdentityFabricConfig.class);

    @Override
    public void onInitialize() {
        new Identity().initialize();
    }
}
