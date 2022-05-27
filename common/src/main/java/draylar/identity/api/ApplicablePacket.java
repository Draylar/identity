package draylar.identity.api;

import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface ApplicablePacket {
    void apply(PlayerEntity player);
}
