package draylar.identity;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import draylar.identity.ability.AbilityOverlayRenderer;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.model.EntityArms;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.platform.PlayerIdentity;
import draylar.identity.network.ClientNetworking;
import draylar.identity.screen.IdentityScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

public class IdentityClient {

    public static final KeyBinding MENU_KEY =
            new KeyBinding(
                    "key.identity",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_GRAVE_ACCENT,
                    "key.categories.identity");

    public static final KeyBinding ABILITY_KEY =
            new KeyBinding(
                    "key.identity_ability",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.identity");

    public void initialize() {
        KeyMappingRegistry.register(MENU_KEY);
        KeyMappingRegistry.register(ABILITY_KEY);

        // Register client-side event handlers
        EntityUpdaters.init();
        AbilityOverlayRenderer.register();
        EntityArms.init();

        // add screen opening key-bind
        ClientTickEvent.CLIENT_PRE.register(client -> {
            assert client.player != null;

            if(MENU_KEY.wasPressed()) {
                if(IdentityConfig.getInstance().enableClientSwapMenu() || client.player.hasPermissionLevel(3)) {
                    MinecraftClient.getInstance().setScreen(new IdentityScreen());
                }
            }
        });

        // when the use-ability key is pressed, trigger ability
        ClientTickEvent.CLIENT_PRE.register(client -> {
            assert client.player != null;

            if(ABILITY_KEY.wasPressed()) {
                // TODO: maybe the check should be on the server to allow for ability extension mods?
                // Only send the ability packet if the identity equipped by the player has one
                LivingEntity identity = PlayerIdentity.getIdentity(client.player);

                if(identity != null) {
                    if(AbilityRegistry.has(identity.getType())) {
                        ClientNetworking.sendAbilityRequest();
                    }
                }
            }
        });
    }
}
