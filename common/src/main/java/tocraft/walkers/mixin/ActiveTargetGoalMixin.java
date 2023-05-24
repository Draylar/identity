package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.platform.WalkersConfig;
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
    private void ignoreShapedPlayers(CallbackInfo ci) {
        if (WalkersConfig.getInstance().hostilesIgnoreHostileShapedPlayer() && this.mob instanceof Monster && this.targetEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) this.targetEntity;
            LivingEntity walkers = PlayerWalkers.getCurrentShape(player);

            if(walkers != null) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // creepers should ignore cats
                    if (this.mob instanceof CreeperEntity && walkers.getType().equals(EntityType.OCELOT)) {
                        this.stop();
                        ci.cancel();
                    }

                    // withers should ignore undead
                    else if (this.mob instanceof WitherEntity && walkers.getGroup().equals(EntityGroup.UNDEAD)) {
                        this.stop();
                        ci.cancel();
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile mobs
                    else if (!(this.mob instanceof WitherEntity) && walkers instanceof Monster) {
                        this.stop();
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Override
    protected void walkers_shouldContinue(CallbackInfoReturnable<Boolean> cir) {
        // check cancelling for hostiles
        if(WalkersConfig.getInstance().hostilesIgnoreHostileShapedPlayer() && WalkersConfig.getInstance().hostilesForgetNewHostileShapedPlayer() && this.mob instanceof Monster && this.targetEntity instanceof PlayerEntity player) {
            LivingEntity walkers = PlayerWalkers.getCurrentShape(player);

            if (walkers != null) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // creepers should ignore cats
                    if (this.mob instanceof CreeperEntity && walkers.getType().equals(EntityType.OCELOT)) {
                        cir.setReturnValue(false);
                    }

                    // withers should ignore undead
                    else if (this.mob instanceof WitherEntity && walkers.getGroup().equals(EntityGroup.UNDEAD)) {
                        cir.setReturnValue(false);
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile mobs
                    else if (!(this.mob instanceof WitherEntity) && walkers instanceof Monster) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
