package tocraft.walkers.api;

import tocraft.walkers.impl.tick.walkers.FrogTickHandler;
import tocraft.walkers.impl.tick.walkers.JumpBoostTickHandler;
import tocraft.walkers.impl.tick.walkers.WardenTickHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class WalkersTickHandlers {

    private static final Map<EntityType<?>, WalkersTickHandler<?>> HANDLERS = new HashMap<>();

    public static void initialize() {
        register(EntityType.WARDEN, new WardenTickHandler());
        register(EntityType.FROG, new FrogTickHandler());
        register(EntityType.RABBIT, new JumpBoostTickHandler<>(1));
        register(EntityType.GOAT, new JumpBoostTickHandler<>(2));
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, WalkersTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    public static Map<EntityType<?>, WalkersTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
