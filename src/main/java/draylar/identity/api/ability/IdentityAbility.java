package draylar.identity.api.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@FunctionalInterface
public interface IdentityAbility<E extends LivingEntity> {
    TypedActionResult<ItemStack> onUse(PlayerEntity player, E identity, World world, ItemStack stack, Hand hand);
}
