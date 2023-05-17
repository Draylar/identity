package tocraft.walkers.ability.impl;

import tocraft.walkers.ability.WalkersAbility;
import tocraft.walkers.impl.SonicBoomUser;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class WardenAbility extends WalkersAbility<WardenEntity> {

    @Override
    public void onUse(PlayerEntity player, WardenEntity walkers, World world) {
        ((SonicBoomUser) player).walkers$ability_startSonicBoom();
    }

    @Override
    public Item getIcon() {
        return Items.ECHO_SHARD;
    }

    @Override
    public int getCooldown(WardenEntity entity) {
        return 20 * 10;
    }
}
