package tocraft.walkers.impl.tick.walkers;

import tocraft.walkers.api.WalkersTickHandler;
import tocraft.walkers.api.platform.WalkersConfig;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class WardenTickHandler implements WalkersTickHandler<WardenEntity> {

    @Override
    public void tick(PlayerEntity player, WardenEntity entity) {
        if(!player.world.isClient) {
            if(player.age % 20 == 0) {

                // Blind the Warden Walkers player.
                if(WalkersConfig.getInstance().wardenIsBlinded()) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 20 * 3, 0, true, false));
                }

                // Blind other players near a player with the Warden Walkers.
                if(WalkersConfig.getInstance().wardenBlindsNearby()) {
                    for (PlayerEntity target : player.world.getPlayers(TargetPredicate.DEFAULT, player, new Box(player.getBlockPos()).expand(16))) {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 20 * 3, 0, true, false));
                    }
                }
            }
        }
    }
}
