package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import draylar.identity.impl.SonicBoomUser;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class WardenAbility extends IdentityAbility<WardenEntity> {

    @Override
    public void onUse(PlayerEntity player, WardenEntity identity, World world) {
        ((SonicBoomUser) player).identity$ability_startSonicBoom();
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
