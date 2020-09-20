package draylar.identity.api.model;

import draylar.identity.impl.NearbySongAccessor;
import draylar.identity.mixin.ParrotEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry class for {@link EntityUpdater} instances.
 *
 * <p>{@link EntityUpdater}s are used to apply changes to identity entity instances on the client using information from the player.
 * As an example, an {@link EntityUpdater} can be used to tell a identity bat to "stop roosting," which triggers the flight animation.
 * {@link EntityUpdater}s are called once every render tick {@link net.minecraft.client.render.entity.EntityRenderer#render(Entity, float, float, MatrixStack, VertexConsumerProvider, int)}.
 */
@Environment(EnvType.CLIENT)
public class EntityUpdaters {

    private static final Map<EntityType<? extends LivingEntity>, EntityUpdater<? extends LivingEntity>> map = new HashMap<>();

    /**
     * Returns a {@link EntityUpdater} if one has been registered for the given {@link EntityType}, or null.
     *
     * @param entityType  entity type key to retrieve a value registered in {@link EntityUpdaters#register(EntityType, EntityUpdater)}
     * @param <T>  passed in {@link EntityType} generic
     * @return  registered {@link EntityUpdater} instance for the given {@link EntityType}, or null if one does not exist
     */
    public static <T extends LivingEntity> EntityUpdater<T> getUpdater(EntityType<T> entityType) {
        return (EntityUpdater<T>) map.getOrDefault(entityType, null);
    }

    /**
     * Registers an {@link EntityUpdater} for the given {@link EntityType}.
     *
     * <p>Note that a given {@link EntityType} can only have 1 {@link EntityUpdater} associated with it.
     *
     * @param type  entity type key associated with the given {@link EntityUpdater}
     * @param entityUpdater  {@link EntityUpdater} associated with the given {@link EntityType}
     * @param <T>  passed in {@link EntityType} generic
     */
    public static <T extends LivingEntity> void register(EntityType<T> type, EntityUpdater<T> entityUpdater) {
        map.put(type, entityUpdater);
    }

    private EntityUpdaters() {
        // NO-OP
    }

    public static void init() {
        // register specific entity animation handling
        EntityUpdaters.register(EntityType.BAT, (player, bat) -> {
            if(player.isOnGround()) {
                bat.setRoosting(true);
            } else {
                bat.setRoosting(false);
            }
        });

        EntityUpdaters.register(EntityType.PARROT, (player, parrot) -> {
            if(player.isOnGround() && ((NearbySongAccessor) player).isNearbySongPlaying()) {
                parrot.setNearbySongPlaying(player.getBlockPos(), true);
                parrot.setSitting(true);
                parrot.setOnGround(true);
            } else if (player.isOnGround()) {
                parrot.setNearbySongPlaying(player.getBlockPos(), false);
                parrot.setSitting(true);
                parrot.setOnGround(true);
                parrot.prevFlapProgress = 0;
                parrot.flapProgress = 0;
                parrot.maxWingDeviation = 0;
                parrot.prevMaxWingDeviation = 0;
            } else {
                parrot.setNearbySongPlaying(player.getBlockPos(), false);
                parrot.setSitting(false);
                parrot.setOnGround(false);
                parrot.setInSittingPose(false);
                ((ParrotEntityAccessor) parrot).callFlapWings();
            }
        });

        EntityUpdaters.register(EntityType.ENDER_DRAGON, (player, dragon) -> {
            dragon.wingPosition += 0.01F;
            dragon.prevWingPosition = dragon.wingPosition;
        });

        EntityUpdaters.register(EntityType.ENDERMAN, (player, enderman) -> {
            ItemStack heldStack = player.getMainHandStack();

            if(heldStack.getItem() instanceof BlockItem) {
                enderman.setCarriedBlock(((BlockItem) heldStack.getItem()).getBlock().getDefaultState());
            }
        });
    }
}
