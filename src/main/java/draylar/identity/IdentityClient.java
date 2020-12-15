package draylar.identity;

import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.network.ClientNetworking;
import draylar.identity.screen.IdentityScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class IdentityClient implements ClientModInitializer {

    public static boolean enableMenu = Identity.CONFIG.enableClientSwapMenu;
    public static boolean showNametags = Identity.CONFIG.showPlayerNametag;
    // todo: won't these change from the client config when the client leaves a server with different options?

    public static final KeyBinding key = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.identity",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_GRAVE_ACCENT,
                    "key.categories.gameplay"
            )
    );

    @Override
    public void onInitializeClient() {
        EntityUpdaters.init();
        ClientNetworking.init();

        // add screen opening key-bind
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            assert client.player != null;

            if(key.wasPressed()) {
                if(enableMenu || client.player.hasPermissionLevel(3)) {
                    MinecraftClient.getInstance().openScreen(new IdentityScreen());
                }
            }
        });
    }
}
