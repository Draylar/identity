package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity {

    private FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "initGoals",
            at = @At("RETURN")
    )
    private void addPlayerTarget(CallbackInfo ci) {
        this.targetSelector.add(7, new FollowTargetGoal<>(this, PlayerEntity.class, 10, false, false, player -> {
            // ensure foxes can attack players with an identity similar to their normal prey
            if(!Identity.CONFIG.foxesAttackIdentityPrey) {
                return false;
            }

            // foxes can target players if their identity is in the fox_prey tag, or if they are an entity that extends FishEntity
            // todo: add baby turtle targeting
            LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();
            return identity != null && EntityTags.FOX_PREY.contains(identity.getType()) || identity instanceof FishEntity;
        }));
    }
}
