package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(PiglinBruteBrain.class)
public class PiglinBruteBrainMixin {

    /**
     * @author Draylar
     *
     * @reason method_30249 searches for a nearby player to aggro on.
     * This mixin modifies the search logic to exclude players disguised as anything besides a Wither Skeleton or Wither.
     */
    @Overwrite
    private static Optional<? extends LivingEntity> method_30249(AbstractPiglinEntity abstractPiglinEntity, MemoryModuleType<? extends LivingEntity> memoryModuleType) {
        return abstractPiglinEntity.getBrain().getOptionalMemory(memoryModuleType).filter((livingEntity) -> {
            if(livingEntity instanceof PlayerEntity player) {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                if(identity != null && !(identity instanceof WitherSkeletonEntity) && !(identity instanceof WitherEntity)) {
                    return false;
                }
            }

            return livingEntity.isInRange(abstractPiglinEntity, 12.0D);
        });
    }
}
