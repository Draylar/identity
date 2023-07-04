package draylar.identity.impl.tick.identity;

import draylar.identity.api.IdentityTickHandler;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class WardenTickHandler implements IdentityTickHandler<WardenEntity> {

    @Override
    public void tick(PlayerEntity player, WardenEntity entity) {
        if(!player.getWorld().isClient) {
            if(player.age % 20 == 0) {

                // Blind the Warden Identity player.
                if(IdentityConfig.getInstance().wardenIsBlinded()) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 20 * 3, 0, true, false));
                }

                // Blind other players near a player with the Warden Identity.
                if(IdentityConfig.getInstance().wardenBlindsNearby()) {
                    for (PlayerEntity target : player.getWorld().getPlayers(TargetPredicate.DEFAULT, player, new Box(player.getBlockPos()).expand(16))) {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 20 * 3, 0, true, false));
                    }
                }
            }
        }
    }
}
