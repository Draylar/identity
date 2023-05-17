package tocraft.walkers.api.model;

import tocraft.walkers.impl.NearbySongAccessor;
import tocraft.walkers.mixin.accessor.CreeperEntityAccessor;
import tocraft.walkers.mixin.accessor.ParrotEntityAccessor;
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
 * <p>{@link EntityUpdater}s are used to apply changes to walkers entity instances on the client using information from the player.
 * As an example, an {@link EntityUpdater} can be used to tell a walkers bat to "stop roosting," which triggers the flight animation.
 * {@link EntityUpdater}s are called once every render tick {@link net.minecraft.client.render.entity.EntityRenderer#render(Entity, float, float, MatrixStack, VertexConsumerProvider, int)}.
 */
@Environment(EnvType.CLIENT)
public class EntityUpdaters {

    private static final Map<EntityType<? extends LivingEntity>, EntityUpdater<? extends LivingEntity>> map = new HashMap<>();

    /**
     * Returns a {@link EntityUpdater} if one has been registered for the given {@link EntityType}, or null.
     *
     * @param entityType entity type key to retrieve a value registered in {@link EntityUpdaters#register(EntityType, EntityUpdater)}
     * @param <T>        passed in {@link EntityType} generic
     * @return registered {@link EntityUpdater} instance for the given {@link EntityType}, or null if one does not exist
     */
    public static <T extends LivingEntity> EntityUpdater<T> getUpdater(EntityType<T> entityType) {
        return (EntityUpdater<T>) map.getOrDefault(entityType, null);
    }

    /**
     * Registers an {@link EntityUpdater} for the given {@link EntityType}.
     *
     * <p>Note that a given {@link EntityType} can only have 1 {@link EntityUpdater} associated with it.
     *
     * @param type          entity type key associated with the given {@link EntityUpdater}
     * @param entityUpdater {@link EntityUpdater} associated with the given {@link EntityType}
     * @param <T>           passed in {@link EntityType} generic
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
            if (player.isOnGround()) {
                bat.setRoosting(true);
            } else {
                bat.setRoosting(false);
            }
        });

        EntityUpdaters.register(EntityType.PARROT, (player, parrot) -> {
            if (player.isOnGround() && ((NearbySongAccessor) player).walkers_isNearbySongPlaying()) {
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

            // setting yaw without +180 making tail faces front, for some reason
            if (dragon.latestSegment < 0) {
                for (int l = 0; l < dragon.segmentCircularBuffer.length; ++l) {
                    dragon.segmentCircularBuffer[l][0] = (double) player.getYaw() + 180;
                    dragon.segmentCircularBuffer[l][1] = player.getY();
                }
            }

            if (++(dragon).latestSegment == (dragon).segmentCircularBuffer.length) {
                (dragon).latestSegment = 0;
            }

            dragon.segmentCircularBuffer[dragon.latestSegment][0] = (double) player.getYaw() + 180;
            dragon.segmentCircularBuffer[dragon.latestSegment][1] = player.getY();
        });

        EntityUpdaters.register(EntityType.ENDERMAN, (player, enderman) -> {
            ItemStack heldStack = player.getMainHandStack();

            if (heldStack.getItem() instanceof BlockItem) {
                enderman.setCarriedBlock(((BlockItem) heldStack.getItem()).getBlock().getDefaultState());
            }
        });

        // To prevent Creeper Identities from flickering white, we reset currentFuseTime to 0.
        // Creepers normally tick their fuse timer in tick(), but:
        //    1. Identities do not tick
        //    2. The Creeper ability is instant, so we do not need to re-implement ticking
        EntityUpdaters.register(EntityType.CREEPER, (player, creeper) -> {
            ((CreeperEntityAccessor) creeper).setCurrentFuseTime(0);
        });
    }
}
