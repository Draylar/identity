package draylar.identity.mixin;

import com.google.common.collect.ImmutableMap;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {

    @Shadow @Final private static ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER;

    @Inject(
            method = "isHostile",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkHostileIdentity(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof PlayerEntity player) {
            // check if we should be performing this from config
            if(IdentityConfig.getInstance().villagersRunFromIdentities()) {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                // check if identity is valid & if it is a type villagers run from
                if (identity != null && SQUARED_DISTANCES_FOR_DANGER.containsKey(identity.getType())) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(
            method = "isCloseEnoughForDanger",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkPlayerDanger(LivingEntity villager, LivingEntity potentialPlayer, CallbackInfoReturnable<Boolean> cir) {
        // should only be called if the above mixin passes, so we can assume the config option is true
        if(potentialPlayer instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            // check if identity is valid & if it is a type villagers run from
            if (identity != null && SQUARED_DISTANCES_FOR_DANGER.containsKey(identity.getType())) {
                float f = SQUARED_DISTANCES_FOR_DANGER.get(identity.getType());
                cir.setReturnValue(potentialPlayer.squaredDistanceTo(villager) <= (double) (f * f));
            } else {
                cir.setReturnValue(false);
            }
        }
    }
}
