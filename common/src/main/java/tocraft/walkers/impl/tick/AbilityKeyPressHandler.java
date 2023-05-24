package tocraft.walkers.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.network.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

public class AbilityKeyPressHandler implements ClientTickEvent.Client {

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.ABILITY_KEY.wasPressed()) {
            // TODO: maybe the check should be on the server to allow for ability extension mods?
            // Only send the ability packet if the walkers equipped by the player has one
            LivingEntity walkers = PlayerWalkers.getCurrentShape(client.player);

            if(walkers != null) {
                if(AbilityRegistry.has(walkers.getType())) {
                    ClientNetworking.sendAbilityRequest();
                }
            }
        }
    }
}
