package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SnowGolemAbility extends IdentityAbility<SnowGolemEntity> {

    @Override
    public void onUse(PlayerEntity player, SnowGolemEntity identity, World world) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClient) {
            for(int i = 0; i < 10; i++) {
                SnowballEntity snowballEntity = new SnowballEntity(world, player);
                snowballEntity.setItem(new ItemStack(Items.SNOWBALL));
                snowballEntity.setVelocity(player, player.getPitch() + world.random.nextInt(10) - 5, player.getYaw() + world.random.nextInt(10) - 5, 0.0F, 1.5F, 1.0F);
                world.spawnEntity(snowballEntity);
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.SNOWBALL;
    }
}
