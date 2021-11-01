package draylar.identity.api.platform.fabric;

import draylar.identity.fabric.IdentityFabric;
import draylar.identity.api.platform.IdentityConfig;

public class IdentityConfigImpl {

    public static IdentityConfig getInstance() {
        return IdentityFabric.CONFIG;
    }
}
