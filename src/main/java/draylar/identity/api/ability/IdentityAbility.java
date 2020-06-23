package draylar.identity.api.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@FunctionalInterface
public interface IdentityAbility {
    void onUse(PlayerEntity player, World world, ItemStack stack, Hand hand);
}
