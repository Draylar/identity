package draylar.identity.api.ability;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class IdentityAbilities {

    private static final Map<EntityType<? extends LivingEntity>, Map<Item, IdentityAbility>> abilities = new HashMap<>();

    private IdentityAbilities() {

    }

    public static void init() {
        IdentityAbilities.register(EntityType.GHAST, Items.FIRE_CHARGE, (player, world, stack, hand) -> {
            FireballEntity fireball = new FireballEntity(
                    world,
                    player.getX(),
                    player.getEyeY(),
                    player.getZ(),
                    player.getRotationVector().x,
                    player.getRotationVector().y,
                    player.getRotationVector().z
            );

            // todo: play ghast sound

            // take item from player
            stack.decrement(1);

            fireball.setOwner(player);
            world.spawnEntity(fireball);
            stack.setCooldown(60);
        });

        IdentityAbilities.register(EntityType.BLAZE, Items.BLAZE_POWDER, (player, world, stack, hand) -> {
            SmallFireballEntity smallFireball = new SmallFireballEntity(
                    world,
                    player.getX(),
                    player.getEyeY(),
                    player.getZ(),
                    player.getRotationVector().x,
                    player.getRotationVector().y,
                    player.getRotationVector().z
            );

            // todo: play blaze sound

            // take item from player
            stack.decrement(1);

            smallFireball.setOwner(player);
            world.spawnEntity(smallFireball);
            stack.setCooldown(20);
        });
    }

    public static IdentityAbility get(EntityType<? extends LivingEntity> entityType, Item heldItem) {
        if(abilities.containsKey(entityType)) {
            Map<Item, IdentityAbility> currentAbilities = abilities.get(entityType);
            return currentAbilities.getOrDefault(heldItem, null);
        }

        return null;
    }

    public static void register(EntityType<? extends LivingEntity> entityType, Item heldItem, IdentityAbility ability) {
        if(!abilities.containsKey(entityType)) {
            abilities.put(entityType, new HashMap<>());
        }

        abilities.get(entityType).put(heldItem, ability);
    }
}
