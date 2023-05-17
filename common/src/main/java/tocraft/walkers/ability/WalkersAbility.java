package tocraft.walkers.ability;

import tocraft.walkers.Walkers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public abstract class WalkersAbility<E extends LivingEntity> {
    /**
     * Defines the use action of this ability. Implementers can assume the ability checks, such as cool-downs, have successfully passed.
     *
     * @param player   player using the ability
     * @param walkers current walkers of the player
     * @param world    world the player is residing in
     */
    abstract public void onUse(PlayerEntity player, E walkers, World world);

    /**
     * @return cooldown of this ability, in ticks, after it is used.
     */
    public int getCooldown(E entity) {
        return Walkers.getCooldown(entity.getType());
    }

    abstract public Item getIcon();
}
