package tocraft.walkers;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import tocraft.walkers.ability.AbilityOverlayRenderer;
import tocraft.walkers.api.ApplicablePacket;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.impl.join.ClientPlayerJoinHandler;
import tocraft.walkers.impl.tick.AbilityKeyPressHandler;
import tocraft.walkers.impl.tick.MenuOnJoinHandler;
import tocraft.walkers.impl.tick.TransformKeyPressHandler;
import tocraft.walkers.network.ClientNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class WalkersClient {
    public static final KeyBinding TRANSFORM_KEY =
    new KeyBinding(
            "key.walkers",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.walkers");
    public static final KeyBinding ABILITY_KEY =
            new KeyBinding(
                    "key.walkers_ability",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.walkers");

    private static final Set<ApplicablePacket> SYNC_PACKET_QUEUE = new HashSet<>();

    public void initialize() {
        KeyMappingRegistry.register(ABILITY_KEY);
        KeyMappingRegistry.register(TRANSFORM_KEY);

        // Register client-side event handlers
        EntityUpdaters.init();
        AbilityOverlayRenderer.register();
        EntityArms.init();

        // Register event handlers
        ClientTickEvent.CLIENT_PRE.register(new AbilityKeyPressHandler());
        ClientTickEvent.CLIENT_PRE.register(new TransformKeyPressHandler());
        ClientTickEvent.CLIENT_PRE.register(new MenuOnJoinHandler());
        ClientNetworking.registerPacketHandlers();
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(new ClientPlayerJoinHandler());
    }

    // We do this because the Architectury "player log in" network event runs before MinecraftClient#player exists.
    public static Set<ApplicablePacket> getSyncPacketQueue() {
        return SYNC_PACKET_QUEUE;
    }
}
