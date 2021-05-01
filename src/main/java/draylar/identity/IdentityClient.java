package draylar.identity;

import draylar.identity.ability.AbilityOverlayRenderer;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.model.EntityArms;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.network.ClientNetworking;
import draylar.identity.registry.Components;
import draylar.identity.screen.IdentityScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class IdentityClient implements ClientModInitializer {

    public static final KeyBinding MENU_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.identity",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_GRAVE_ACCENT,
                    "key.categories.identity"));

    public static final KeyBinding ABILITY_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.identity_ability",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.identity"));

    @Override
    public void onInitializeClient() {
        EntityUpdaters.init();
        ClientNetworking.init();
        AbilityOverlayRenderer.register();
        EntityArms.init();

        // add screen opening key-bind
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            assert client.player != null;

            if(MENU_KEY.wasPressed()) {
                if(Identity.CONFIG.enableClientSwapMenu || client.player.hasPermissionLevel(3)) {
                    MinecraftClient.getInstance().openScreen(new IdentityScreen());
                }
            }
        });

        // when the use-ability key is pressed, trigger ability
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            assert client.player != null;

            if(ABILITY_KEY.wasPressed()) {
                // TODO: maybe the check should be on the server to allow for ability extension mods?
                // Only send the ability packet if the identity equipped by the player has one
                LivingEntity identity = Components.CURRENT_IDENTITY.get(client.player).getIdentity();

                if(identity != null) {
                    if(AbilityRegistry.has(identity.getType())) {
                        ClientNetworking.sendAbilityRequest();
                    }
                }
            }
        });
    }
}
