package draylar.identity.fabric;

import draylar.identity.Identity;
import draylar.identity.fabric.config.IdentityFabricConfig;
import draylar.omegaconfig.OmegaConfig;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import net.fabricmc.api.ModInitializer;

public class IdentityFabric implements ModInitializer {

    public static final IdentityFabricConfig CONFIG = OmegaConfig.register(IdentityFabricConfig.class);
    public static final AbilitySource ABILITY_SOURCE = Pal.getAbilitySource(Identity.id("equipped_identity"));

    @Override
    public void onInitialize() {
        new Identity().initialize();
    }
}
