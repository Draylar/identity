package tocraft.walkers.api;

import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.WalkersType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class WalkersGranting {

    public static void grantByAttack(PlayerEntity player, WalkersType<?> granted) {
        if(player instanceof ServerPlayerEntity serverPlayerEntity) {
            int amountKilled = serverPlayerEntity.getStatHandler().getStat(Stats.KILLED, granted.getEntityType());

            // If the player has to kill a certain number of mobs before unlocking an Walkers, check their statistic for the specific type.
            if(WalkersConfig.getInstance().requiresKillsForWalkers()) {
                String id = Registry.ENTITY_TYPE.getId(granted.getEntityType()).toString();

                // Check against a specific count requirement or the default count.
                int required = WalkersConfig.getInstance().getRequiredKillsForWalkers();
                if(WalkersConfig.getInstance().getRequiredKillsByType() != null && WalkersConfig.getInstance().getRequiredKillsByType().containsKey(id)) {
                    required = WalkersConfig.getInstance().getRequiredKillsByType().get(id);
                }

                // If the amount currently killed is less than the required amount, do not allow the player to unlock.
                if(amountKilled < required) {
                    return;
                }
            }

            boolean isNew = false;
            boolean hadPreviously = PlayerUnlocks.has(serverPlayerEntity, granted);
            boolean result = PlayerUnlocks.unlock(serverPlayerEntity, granted);

            // ensure type has not already been unlocked
            if(result && !hadPreviously) {

                // send unlock message to player if they aren't in creative and the config option is on
                if(WalkersConfig.getInstance().shouldOverlayWalkersUnlocks() && !player.isCreative()) {
                    player.sendMessage(
                            Text.translatable(
                                    "walkers.unlock_entity",
                                    Text.translatable(granted.getEntityType().getTranslationKey())
                            ), true
                    );
                }

                isNew = true;
            }

            // force-morph player into new type
            Entity instanced = granted.create(player.world);
            if(instanced instanceof LivingEntity) {
                if(WalkersConfig.getInstance().forceChangeNew() && isNew) {
                    PlayerWalkers.updateWalkers(serverPlayerEntity, granted, (LivingEntity) instanced);
                } else if(WalkersConfig.getInstance().forceChangeAlways()) {
                    PlayerWalkers.updateWalkers(serverPlayerEntity, granted, (LivingEntity) instanced);
                }
            }
        }
    }
}
