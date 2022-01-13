package draylar.identity;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import draylar.identity.ability.AbilityOverlayRenderer;
import draylar.identity.api.model.EntityArms;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.impl.tick.AbilityKeyPressHandler;
import draylar.identity.impl.tick.MenuKeyPressHandler;
import draylar.identity.network.ClientNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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

        // Register event handlers
        ClientTickEvent.CLIENT_PRE.register(new MenuKeyPressHandler());
        ClientTickEvent.CLIENT_PRE.register(new AbilityKeyPressHandler());
        ClientNetworking.registerPacketHandlers();
    }
}
