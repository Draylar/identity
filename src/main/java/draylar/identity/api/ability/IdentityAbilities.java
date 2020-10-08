package draylar.identity.api.ability;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
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

            return TypedActionResult.success(stack);
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
            return TypedActionResult.success(stack);
        });

        IdentityAbilities.register(EntityType.ENDERMAN, Items.ENDER_PEARL, (player, identity, world, stack, hand) -> {
            HitResult lookingAt = player.raycast(32, 0, true);
            player.requestTeleport(lookingAt.getPos().x, lookingAt.getPos().y, lookingAt.getPos().z);
            stack.decrement(1);
            player.getItemCooldownManager().set(stack.getItem(), 100);
            return TypedActionResult.success(stack);
        });

        IdentityAbilities.register(EntityType.CREEPER, Items.GUNPOWDER, (player, identity, world, stack, hand) -> {
            world.createExplosion(player, player.getX(), player.getY(), player.getZ(), 3.0f, Explosion.DestructionType.NONE);
            stack.decrement(1);
            player.getItemCooldownManager().set(stack.getItem(), 100);
            return TypedActionResult.success(stack);
        });

        IdentityAbilities.register(EntityType.SNOW_GOLEM, Items.SNOWBALL, (player, identity, world, stack, hand) -> {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

            if (!world.isClient) {
                for(int i = 0; i < 10; i++) {
                    SnowballEntity snowballEntity = new SnowballEntity(world, player);
                    snowballEntity.setItem(new ItemStack(Items.SNOWBALL));
                    snowballEntity.setProperties(player, player.pitch + world.random.nextInt(10) - 5, player.yaw + world.random.nextInt(10) - 5, 0.0F, 1.5F, 1.0F);
                    world.spawnEntity(snowballEntity);
                }
            }

            return TypedActionResult.pass(stack);
        });

        IdentityAbilities.register(EntityType.WITHER, Items.WITHER_SKELETON_SKULL, (player, identity, world, stack, hand) -> {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

            if (!world.isClient) {
                Vec3d lookDirection = player.getRotationVector();
                WitherSkullEntity skull = new WitherSkullEntity(world, player, lookDirection.x, lookDirection.y, lookDirection.z);
                skull.setPos(player.getX(), player.getY() + 2, player.getZ());
                skull.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
                world.spawnEntity(skull);
            }

            player.getItemCooldownManager().set(stack.getItem(), 200);
            return TypedActionResult.success(stack);
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
