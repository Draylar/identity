package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WitherEntityAbility extends IdentityAbility<WitherEntity> {

    @Override
    public void onUse(PlayerEntity player, WitherEntity identity, World world) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClient) {
            Vec3d lookDirection = player.getRotationVector();
            WitherSkullEntity skull = new WitherSkullEntity(world, player, lookDirection.x, lookDirection.y, lookDirection.z);
            skull.setPos(player.getX(), player.getY() + 2, player.getZ());
            skull.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(skull);
        }
    }

    @Override
    public Item getIcon() {
        return Items.WITHER_SKELETON_SKULL;
    }
}
