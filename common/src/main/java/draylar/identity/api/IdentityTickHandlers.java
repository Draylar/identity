package draylar.identity.api;

import draylar.identity.impl.tick.identity.FrogTickHandler;
import draylar.identity.impl.tick.identity.JumpBoostTickHandler;
import draylar.identity.impl.tick.identity.WardenTickHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class IdentityTickHandlers {

    private static final Map<EntityType<?>, IdentityTickHandler<?>> HANDLERS = new HashMap<>();

    public static void initialize() {
        register(EntityType.WARDEN, new WardenTickHandler());
        register(EntityType.FROG, new FrogTickHandler());
        register(EntityType.RABBIT, new JumpBoostTickHandler<>(1));
        register(EntityType.GOAT, new JumpBoostTickHandler<>(2));
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, IdentityTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    public static Map<EntityType<?>, IdentityTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
