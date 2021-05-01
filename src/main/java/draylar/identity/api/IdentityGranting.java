package draylar.identity.api;

import draylar.identity.Identity;
import draylar.identity.cca.UnlockedIdentitiesComponent;
import draylar.identity.registry.Components;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;

public class IdentityGranting {

    public static void grantByAttack(PlayerEntity player, EntityType<?> granted) {
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
            } else if (Identity.CONFIG.forceChangeAlways) {
                Components.CURRENT_IDENTITY.get(player).setIdentity((LivingEntity) instanced);
            }
        }
    }
}
