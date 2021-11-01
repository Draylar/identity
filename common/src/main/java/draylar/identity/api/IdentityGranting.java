package draylar.identity.api;

import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.platform.PlayerIdentity;
import draylar.identity.api.platform.PlayerUnlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class IdentityGranting {

    public static void grantByAttack(PlayerEntity player, EntityType<?> granted) {
        if(player instanceof ServerPlayerEntity serverPlayerEntity) {
            int amountKilled = serverPlayerEntity.getStatHandler().getStat(Stats.KILLED, granted);

            // If the player has to kill a certain number of mobs before unlocking an Identity, check their statistic for the specific type.
            if(IdentityConfig.getInstance().requiresKillsForIdentity()) {
                String id = Registry.ENTITY_TYPE.getId(granted).toString();

                // Check against a specific count requirement or the default count.
                int required = IdentityConfig.getInstance().getRequiredKillsForIdentity();
                if(IdentityConfig.getInstance().getRequiredKillsByType() != null && IdentityConfig.getInstance().getRequiredKillsByType().containsKey(id)) {
                    required = IdentityConfig.getInstance().getRequiredKillsByType().get(id);
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
                if(IdentityConfig.getInstance().shouldOverlayIdentityUnlocks() && !player.isCreative()) {
                    player.sendMessage(
                            new TranslatableText(
                                    "identity.unlock_entity",
                                    new TranslatableText(granted.getTranslationKey())
                            ), true
                    );
                }

                isNew = true;
            }

            // force-morph player into new type
            Entity instanced = granted.create(player.world);
            if(instanced instanceof LivingEntity) {
                if(IdentityConfig.getInstance().forceChangeNew() && isNew) {
                    PlayerIdentity.updateIdentity(serverPlayerEntity, (LivingEntity) instanced);
                } else if(IdentityConfig.getInstance().forceChangeAlways()) {
                    PlayerIdentity.updateIdentity(serverPlayerEntity, (LivingEntity) instanced);
                }
            }
        }
    }
}
