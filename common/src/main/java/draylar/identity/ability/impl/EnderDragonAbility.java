package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class EnderDragonAbility extends IdentityAbility<EnderDragonEntity> {

    @Override
    public void onUse(PlayerEntity player, EnderDragonEntity identity, World world) {
        DragonFireballEntity dragonFireball = new DragonFireballEntity(
                world,
                player,
                player.getRotationVector().x,
                player.getRotationVector().y,
                player.getRotationVector().z
        );

        dragonFireball.setOwner(player);
        world.spawnEntity(dragonFireball);
    }

    @Override
    public Item getIcon() {
        return Items.DRAGON_BREATH;
    }
}
