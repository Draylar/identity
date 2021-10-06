package draylar.identity.api;

import draylar.identity.Identity;
import draylar.identity.cca.UnlockedIdentitiesComponent;
import draylar.identity.registry.Components;
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
            if(Identity.CONFIG.killForIdentity) {
                String id = Registry.ENTITY_TYPE.getId(granted).toString();

                // Check against a specific count requirement or the default count.
                int required = Identity.CONFIG.requiredKillsForIdentity;
                if(Identity.CONFIG.requiredKillsByType != null && Identity.CONFIG.requiredKillsByType.containsKey(id)) {
                    required = Identity.CONFIG.requiredKillsByType.get(id);
                }

                // If the amount currently killed is less than the required amount, do not allow the player to unlock.
                if(amountKilled < required) {
                    return;
                }
            }

            boolean isNew = false;
            UnlockedIdentitiesComponent unlocked = Components.UNLOCKED_IDENTITIES.get(player);
            boolean hadPreviously = unlocked.has(granted);
            boolean result = unlocked.unlock(granted);

            // ensure type has not already been unlocked
            if(result && !hadPreviously) {

                // send unlock message to player if they aren't in creative and the config option is on
                if(Identity.CONFIG.overlayIdentityUnlocks && !player.isCreative()) {
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
                if(Identity.CONFIG.forceChangeNew && isNew) {
                    Components.CURRENT_IDENTITY.get(player).setIdentity((LivingEntity) instanced);
                } else if(Identity.CONFIG.forceChangeAlways) {
                    Components.CURRENT_IDENTITY.get(player).setIdentity((LivingEntity) instanced);
                }
            }
        }
    }
}
