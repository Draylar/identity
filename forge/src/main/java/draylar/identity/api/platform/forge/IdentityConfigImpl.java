package draylar.identity.api.platform.forge;

import draylar.identity.forge.IdentityForge;
import draylar.identity.api.platform.IdentityConfig;

public class IdentityConfigImpl {

    public static IdentityConfig getInstance() {
        return IdentityForge.CONFIG;
    }
}
