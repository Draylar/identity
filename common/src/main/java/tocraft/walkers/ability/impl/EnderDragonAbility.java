package tocraft.walkers.ability.impl;

import tocraft.walkers.ability.WalkersAbility;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class EnderDragonAbility extends WalkersAbility<EnderDragonEntity> {

    @Override
    public void onUse(PlayerEntity player, EnderDragonEntity walkers, World world) {
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
