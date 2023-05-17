package tocraft.walkers.ability.impl;

import tocraft.walkers.ability.WalkersAbility;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class CreeperAbility extends WalkersAbility<CreeperEntity> {

    @Override
    public void onUse(PlayerEntity player, CreeperEntity walkers, World world) {
        world.createExplosion(player, player.getX(), player.getY(), player.getZ(), 3.0f, Explosion.DestructionType.NONE);
    }

    @Override
    public Item getIcon() {
        return Items.TNT;
    }
}
