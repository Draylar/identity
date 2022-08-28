package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LlamaAbility <T extends LlamaEntity> extends IdentityAbility<T> {

    @Override
    public void onUse(PlayerEntity player, LlamaEntity identity, World world) {
        LlamaSpitEntity spit = new LlamaSpitEntity(EntityType.LLAMA_SPIT, world);
        spit.setOwner(player);
        Vec3d rotation = player.getRotationVector();
        spit.setVelocity(rotation.x, rotation.y, rotation.z, 1.5F, 10.0F);
        spit.updateTrackedPosition(player.getX(), player.getEyeY(), player.getZ());
        spit.updatePosition(player.getX(), player.getEyeY(), player.getZ());

        // Play SFX
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_LLAMA_SPIT, player.getSoundCategory(), 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F);

        world.spawnEntity(spit);
    }

    @Override
    public Item getIcon() {
        return Items.LEAD;
    }
}
