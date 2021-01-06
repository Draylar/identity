package draylar.identity.ability;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class AbilityOverlayRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register((matrices, delta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;

            if(player != null) {

            }
        });
    }

    private AbilityOverlayRenderer() {
        // NO-OP
    }
}
