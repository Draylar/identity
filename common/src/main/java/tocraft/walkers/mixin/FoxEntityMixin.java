package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.registry.WalkersEntityTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity {

    @Shadow @Final @Mutable
    private static Predicate<Entity> NOTICEABLE_PLAYER_FILTER;

    private FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    // Change the default "flee from player," predicate to ignore players disguised as Foxes.
    // Hopefully nobody else needs to modify fox fleeing behavior.
    static {
        NOTICEABLE_PLAYER_FILTER = entity -> {
            boolean isWalkersPlayer = false;

            if(entity instanceof PlayerEntity player) {
                LivingEntity walkers = PlayerWalkers.getWalkers(player);
                if(walkers instanceof FoxEntity) {
                    isWalkersPlayer = true;
                }
            }

            return !entity.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity) && !isWalkersPlayer;
        };
    }

    @Inject(
            method = "initGoals",
            at = @At("RETURN")
    )
    private void addPlayerTarget(CallbackInfo ci) {
        this.targetSelector.add(7, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, false, false, player -> {
            // ensure foxes can attack players with an walkers similar to their normal prey
            if(!WalkersConfig.getInstance().foxesAttack2ndShapedPrey()) {
                return false;
            }

            // foxes can target players if their walkers is in the fox_prey tag, or if they are an entity that extends FishEntity
            // todo: add baby turtle targeting
            LivingEntity walkers = PlayerWalkers.getWalkers((PlayerEntity) player);
            return walkers != null && walkers.getType().isIn(WalkersEntityTags.FOX_PREY) || walkers instanceof FishEntity;
        }));
    }
}
