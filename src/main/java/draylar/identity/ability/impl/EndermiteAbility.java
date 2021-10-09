package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EndermiteAbility extends IdentityAbility<EndermiteEntity> {
    
    @Override
    public void onUse(PlayerEntity player, EndermiteEntity identity, World world) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        for(int i = 0; i < 16; ++i) {
            // Pick a random location nearby to teleport to.
            double g = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
            double h = MathHelper.clamp(player.getY() + (double)(player.getRandom().nextInt(16) - 8), 0.0D, world.getHeight() - 1);
            double j = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;

            // Cancel vehicle/riding mechanics.
            if (player.hasVehicle()) {
                player.stopRiding();
            }

            // Teleport the player and play sound FX if it succeeds.
            if (player.teleport(g, h, j, true)) {
                SoundEvent soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                world.playSound(null, x, y, z, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.playSound(soundEvent, 1.0F, 1.0F);
                break;
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.CHORUS_FRUIT;
    }
}
