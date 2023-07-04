package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EvokerAbility extends IdentityAbility<EvokerEntity> {

    @Override
    public void onUse(PlayerEntity player, EvokerEntity identity, World world) {
        // Spawn 8 Evoker Fangs out from the player.
        Vec3d origin = player.getPos();
        Vec3d facing = player.getRotationVector().multiply(1, 0, 1); // fangs should not go up/down based on pitch

        // Iterate out 5 blocks
        for(int blockOut = 0; blockOut < 8; blockOut++) {
            origin = origin.add(facing); // we add at the start -- no need to put a fang directly underneath the player!

            // Spawn an Evoker Fang at the given position.
            // For each position, we go up or down at most -+1 block per iteration.
            // If we cannot go up or down 1 block (or stay at the same level), the chain ends.

            // If the block underneath is solid, we are good to go.
            EvokerFangsEntity fangs = new EvokerFangsEntity(world, origin.getX(), origin.getY(), origin.getZ(), player.getYaw(), blockOut * 2, player);
            BlockPos underneathPosition = new BlockPos((int) origin.getX(), (int) origin.getY(), (int) origin.getZ()).down();
            BlockState underneath = world.getBlockState(underneathPosition);
            if(underneath.isSideSolidFullSquare(world, underneathPosition, Direction.UP) && world.isAir(underneathPosition.up())) {
                world.spawnEntity(fangs);
                continue;
            }

            // Check underneath (2x down) again...
            BlockPos underneath2Position = new BlockPos((int) origin.getX(), (int) origin.getY(), (int) origin.getZ()).down(2);
            BlockState underneath2 = world.getBlockState(underneath2Position);
            if(underneath2.isSideSolidFullSquare(world, underneath2Position, Direction.UP) && world.isAir(underneath2Position.up())) {
                fangs.setPos(fangs.getX(), fangs.getY() - 1, fangs.getZ());
                world.spawnEntity(fangs);
                origin = origin.add(0, -1, 0);
                continue;
            }

            // Check above (1x up)
            BlockPos upPosition = new BlockPos((int) origin.getX(), (int) origin.getY(), (int) origin.getZ()).up();
            BlockState up = world.getBlockState(underneath2Position);
            if(up.isSideSolidFullSquare(world, upPosition, Direction.UP) && world.isAir(upPosition)) {
                fangs.setPos(fangs.getX(), fangs.getY() + 1, fangs.getZ());
                world.spawnEntity(fangs);
                origin = origin.add(0, 1, 0);
                continue;
            }

            break;
        }
    }

    @Override
    public Item getIcon() {
        return Items.EMERALD;
    }
}
