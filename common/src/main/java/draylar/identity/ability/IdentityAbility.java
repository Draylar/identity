package draylar.identity.ability;

import draylar.identity.Identity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public abstract class IdentityAbility<E extends LivingEntity> {
    /**
     * Defines the use action of this ability. Implementers can assume the ability checks, such as cool-downs, have successfully passed.
     *
     * @param player   player using the ability
     * @param identity current identity of the player
     * @param world    world the player is residing in
     */
    abstract public void onUse(PlayerEntity player, E identity, World world);

    /**
     * @return cooldown of this ability, in ticks, after it is used.
     */
    public int getCooldown(E entity) {
        return Identity.getCooldown(entity.getType());
    }

    abstract public Item getIcon();
}
