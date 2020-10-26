package draylar.identity.registry;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import draylar.identity.Identity;
import draylar.identity.cca.HostilityComponent;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.cca.UnlockedIdentitysComponent;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;

public class Components implements EntityComponentInitializer {

    public static final ComponentKey<IdentityComponent> CURRENT_IDENTITY = ComponentRegistry.getOrCreate(
            Identity.id("current_identity"),
            IdentityComponent.class
    );

    public static final ComponentKey<UnlockedIdentitysComponent> UNLOCKED_IDENTITIES = ComponentRegistry.getOrCreate(
            Identity.id("unlocked_identities"),
            UnlockedIdentitysComponent.class
    );

    public static final ComponentKey<HostilityComponent> HOSTILITY = ComponentRegistry.getOrCreate(
            Identity.id("hostility"),
            HostilityComponent.class
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(Components.CURRENT_IDENTITY, IdentityComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(Components.UNLOCKED_IDENTITIES, UnlockedIdentitysComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(Components.HOSTILITY, p -> new HostilityComponent(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
