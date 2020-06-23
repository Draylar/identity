package draylar.identity.registry;

import draylar.identity.Identity;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.cca.UnlockedIdentitysComponent;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.util.EntityComponents;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;

public class Components {

    public static final ComponentType<IdentityComponent> CURRENT_IDENTITY = ComponentRegistry.INSTANCE.registerIfAbsent(
            Identity.id("current_identity"),
            IdentityComponent.class
    );
    public static final ComponentType<UnlockedIdentitysComponent> UNLOCKED_IDENTITIES = ComponentRegistry.INSTANCE.registerIfAbsent(
            Identity.id("unlocked_identities"),
            UnlockedIdentitysComponent.class
    );

    private Components() {

    }

    public static void init() {
        EntityComponents.setRespawnCopyStrategy(Components.CURRENT_IDENTITY, RespawnCopyStrategy.ALWAYS_COPY);
        EntityComponents.setRespawnCopyStrategy(Components.UNLOCKED_IDENTITIES, RespawnCopyStrategy.ALWAYS_COPY);

        EntityComponentCallback.event(PlayerEntity.class).register((player, components) -> {
            components.put(Components.CURRENT_IDENTITY, new IdentityComponent(player));
        });

        EntityComponentCallback.event(PlayerEntity.class).register((player, components) -> {
            components.put(Components.UNLOCKED_IDENTITIES, new UnlockedIdentitysComponent(player));
        });
    }
}
