package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class CowAbility extends IdentityAbility<CowEntity> {

    @Override
    public void onUse(PlayerEntity player, CowEntity identity, World world) {
        player.clearStatusEffects();
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.MILK_BUCKET;
    }
}
