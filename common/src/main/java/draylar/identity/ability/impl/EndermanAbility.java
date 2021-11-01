package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class EndermanAbility extends IdentityAbility<EndermanEntity> {

    @Override
    public void onUse(PlayerEntity player, EndermanEntity identity, World world) {
        HitResult lookingAt = player.raycast(IdentityConfig.getInstance().endermanAbilityTeleportDistance(), 0, true);
        player.requestTeleport(lookingAt.getPos().x, lookingAt.getPos().y, lookingAt.getPos().z);
        player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
    }

    @Override
    public Item getIcon() {
        return Items.ENDER_PEARL;
    }
}
