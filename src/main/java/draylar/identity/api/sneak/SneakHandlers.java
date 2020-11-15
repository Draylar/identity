package draylar.identity.api.sneak;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SneakHandlers {

    private static final Map<EntityType<? extends LivingEntity>, SneakHandler<? extends LivingEntity>> HANDLERS = new HashMap<>();

    public static <E extends LivingEntity, T extends EntityType<E>> void register(T type, SneakHandler<E> handler) {
        HANDLERS.put(type, handler);
    }

    @Nullable
    public static <E extends LivingEntity> SneakHandler<E> get(EntityType<E> type) {
        return (SneakHandler<E>) HANDLERS.getOrDefault(type, null);
    }

    public static void init() {
        register(EntityType.CAT, (player, identity) -> {
            identity.setSitting(true);
            identity.setInSittingPose(true);
            identity.setSneaking(true);
        });

        register(EntityType.OCELOT, (player, identity) -> {
            identity.setPose(EntityPose.CROUCHING);
            identity.setSneaking(true);
        });
    }

    private SneakHandlers() {

    }
}
