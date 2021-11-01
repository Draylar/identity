package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class WitchAbility extends IdentityAbility<WitchEntity> {

    public static final List<Potion> VALID_POTIONS = Arrays.asList(Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS);

    @Override
    public void onUse(PlayerEntity player, WitchEntity identity, World world) {
        PotionEntity potionEntity = new PotionEntity(world, player);
        potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), VALID_POTIONS.get(world.random.nextInt(VALID_POTIONS.size()))));
        potionEntity.setPitch(-20.0F);
        Vec3d rotation = player.getRotationVector();
        potionEntity.setVelocity(rotation.getX(), rotation.getY(), rotation.getZ(), 0.75F, 8.0F);

        world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WITCH_THROW, player.getSoundCategory(), 1.0F, 0.8F + world.random.nextFloat() * 0.4F);

        world.spawnEntity(potionEntity);
    }

    @Override
    public Item getIcon() {
        return Items.POTION;
    }
}
