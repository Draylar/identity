package draylar.identity.registry;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import draylar.identity.Identity;
import draylar.identity.cca.*;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;

public class Components implements EntityComponentInitializer {

    public static final ComponentKey<IdentityComponent> CURRENT_IDENTITY = ComponentRegistry.getOrCreate(
            Identity.id("current_identity"),
            IdentityComponent.class);

    public static final ComponentKey<UnlockedIdentitiesComponent> UNLOCKED_IDENTITIES = ComponentRegistry.getOrCreate(
            Identity.id("unlocked_identities"),
            UnlockedIdentitiesComponent.class);

    public static final ComponentKey<FavoriteIdentitiesComponent> FAVORITE_IDENTITIES = ComponentRegistry.getOrCreate(
            Identity.id("favorite_identities"),
            FavoriteIdentitiesComponent.class);

    public static final ComponentKey<HostilityComponent> HOSTILITY = ComponentRegistry.getOrCreate(
            Identity.id("hostility"),
            HostilityComponent.class);

    public static final ComponentKey<AbilityComponent> ABILITY = ComponentRegistry.getOrCreate(
            Identity.id("ability"),
            AbilityComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(Components.CURRENT_IDENTITY, IdentityComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(Components.UNLOCKED_IDENTITIES, UnlockedIdentitiesComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(Components.FAVORITE_IDENTITIES, FavoriteIdentitiesComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(Components.HOSTILITY, player -> new HostilityComponent(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(Components.ABILITY, AbilityComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
