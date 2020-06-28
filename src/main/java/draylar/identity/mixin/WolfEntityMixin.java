package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity {

    private WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "initGoals",
            at = @At("RETURN")
    )
    private void addPlayerTarget(CallbackInfo ci) {
        this.targetSelector.add(7, new FollowTargetGoal<>(this, PlayerEntity.class, 10, false, false, player -> {
            // ensure wolves can attack players with an identity similar to their normal prey
            if(!Identity.CONFIG.wolvesAttackIdentityPrey) {
                return false;
            }

            LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

            // wolves should ignore players that look like their prey if they have an owner,
            // unless the config option is turned to true
            LivingEntity owner = this.getOwner();
            if(owner != null || Identity.CONFIG.ownedWolvesAttackIdentityPrey) {
                return false;
            }

            return identity != null && EntityTags.WOLF_PREY.contains(identity.getType());
        }));
    }
}
