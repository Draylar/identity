package draylar.identity.fabric;

import draylar.identity.IdentityClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class IdentityFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new IdentityClient().initialize();
    }
}
