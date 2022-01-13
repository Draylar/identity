package draylar.identity.mixin;

import draylar.identity.api.PlayerHostility;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoalMixin {

    @Shadow protected LivingEntity targetEntity;

    @Inject(
            method = "start",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ignoreMorphedPlayers(CallbackInfo ci) {
        if (IdentityConfig.getInstance().hostilesIgnoreHostileIdentityPlayer() && this.mob instanceof Monster && this.targetEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) this.targetEntity;
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if(identity != null) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // creepers should ignore cats
                    if (this.mob instanceof CreeperEntity && identity.getType().equals(EntityType.OCELOT)) {
                        this.stop();
                        ci.cancel();
                    }

                    // withers should ignore undead
                    else if (this.mob instanceof WitherEntity && identity.getGroup().equals(EntityGroup.UNDEAD)) {
                        this.stop();
                        ci.cancel();
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile mobs
                    else if (!(this.mob instanceof WitherEntity) && identity instanceof Monster) {
                        this.stop();
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Override
    protected void identity_shouldContinue(CallbackInfoReturnable<Boolean> cir) {
        // check cancelling for hostiles
        if(IdentityConfig.getInstance().hostilesIgnoreHostileIdentityPlayer() && IdentityConfig.getInstance().hostilesForgetNewHostileIdentityPlayer() && this.mob instanceof Monster && this.targetEntity instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // creepers should ignore cats
                    if (this.mob instanceof CreeperEntity && identity.getType().equals(EntityType.OCELOT)) {
                        cir.setReturnValue(false);
                    }

                    // withers should ignore undead
                    else if (this.mob instanceof WitherEntity && identity.getGroup().equals(EntityGroup.UNDEAD)) {
                        cir.setReturnValue(false);
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile mobs
                    else if (!(this.mob instanceof WitherEntity) && identity instanceof Monster) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
