package draylar.identity.api.ability;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.explosion.Explosion;

import java.util.HashMap;
import java.util.Map;

public class IdentityAbilities {

    private static final Map<EntityType<? extends LivingEntity>, Map<Item, IdentityAbility>> abilities = new HashMap<>();

    private IdentityAbilities() {

    }

    public static void init() {
        IdentityAbilities.register(EntityType.GHAST, Items.FIRE_CHARGE, (player, identity, world, stack, hand) -> {
            FireballEntity fireball = new FireballEntity(
                    world,
                    player.getX(),
                    player.getEyeY(),
                    player.getZ(),
                    player.getRotationVector().x,
                    player.getRotationVector().y,
                    player.getRotationVector().z
            );

            stack.decrement(1);
            fireball.setOwner(player);
            world.spawnEntity(fireball);
            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
            player.getItemCooldownManager().set(stack.getItem(), 60);
        });

        IdentityAbilities.register(EntityType.BLAZE, Items.BLAZE_POWDER, (player, identity, world, stack, hand) -> {
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

            stack.decrement(1);
            smallFireball.setOwner(player);
            world.spawnEntity(smallFireball);
            player.getItemCooldownManager().set(stack.getItem(), 20);
        });

        IdentityAbilities.register(EntityType.ENDERMAN, Items.ENDER_PEARL, (player, identity, world, stack, hand) -> {
            HitResult lookingAt = player.rayTrace(32, 0, true);
            player.requestTeleport(lookingAt.getPos().x, lookingAt.getPos().y, lookingAt.getPos().z);
            stack.decrement(1);
            player.getItemCooldownManager().set(stack.getItem(), 100);
        });

        IdentityAbilities.register(EntityType.CREEPER, Items.GUNPOWDER, (player, identity, world, stack, hand) -> {
            world.createExplosion(player, player.getX(), player.getY(), player.getZ(), 3.0f, Explosion.DestructionType.NONE);
            stack.decrement(1);
            player.getItemCooldownManager().set(stack.getItem(), 100);
        });
    }

    public static IdentityAbility get(EntityType<? extends LivingEntity> entityType, Item heldItem) {
        if(abilities.containsKey(entityType)) {
            Map<Item, IdentityAbility> currentAbilities = abilities.get(entityType);
            return currentAbilities.getOrDefault(heldItem, null);
        }

        return null;
    }

    public static <A extends LivingEntity, T extends EntityType<A>> void register(T entityType, Item heldItem, IdentityAbility<A> ability) {
        if(!abilities.containsKey(entityType)) {
            abilities.put(entityType, new HashMap<>());
        }

        abilities.get(entityType).put(heldItem, ability);
    }
}
