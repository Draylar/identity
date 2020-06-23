package draylar.identity;

import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.screen.IdentityScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class IdentityClient implements ClientModInitializer {

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

        // add screen opening key-bind
        ClientTickCallback.EVENT.register(client -> {
            if(key.wasPressed()) {
                MinecraftClient.getInstance().openScreen(new IdentityScreen());
            }
        });
    }
}
