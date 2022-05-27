package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.registry.IdentityEntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
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
        this.targetSelector.add(7, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, false, false, player -> {
            // ensure wolves can attack players with an identity similar to their normal prey
            if(!IdentityConfig.getInstance().wolvesAttackIdentityPrey()) {
                return false;
            }

            LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) player);

            // wolves should ignore players that look like their prey if they have an owner,
            // unless the config option is turned to true
            LivingEntity owner = this.getOwner();
            if(owner != null || IdentityConfig.getInstance().ownedWolvesAttackIdentityPrey()) {
                return false;
            }

            return identity != null && identity.getType().isIn(IdentityEntityTags.WOLF_PREY);
        }));
    }
}
