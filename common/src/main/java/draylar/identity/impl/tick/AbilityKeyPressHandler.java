package draylar.identity.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import draylar.identity.IdentityClient;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.network.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

public class AbilityKeyPressHandler implements ClientTickEvent.Client {

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(IdentityClient.ABILITY_KEY.wasPressed()) {
            // TODO: maybe the check should be on the server to allow for ability extension mods?
            // Only send the ability packet if the identity equipped by the player has one
            LivingEntity identity = PlayerIdentity.getIdentity(client.player);

            if(identity != null) {
                if(AbilityRegistry.has(identity.getType())) {
                    ClientNetworking.sendAbilityRequest();
                }
            }
        }
    }
}
