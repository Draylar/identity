package draylar.identity.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import draylar.identity.registry.Components;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class AbilityComponent implements AutoSyncedComponent, ServerTickingComponent {

    private static final String ABILITY_COOLDOWN_KEY = "AbilityCooldown";
    private final PlayerEntity player;
    private int cooldown = 0;

    public AbilityComponent(PlayerEntity player) {

        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.cooldown = tag.getInt(ABILITY_COOLDOWN_KEY);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(ABILITY_COOLDOWN_KEY, cooldown);
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
        Components.ABILITY.sync(this.player);
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean canUseAbility() {
        return cooldown <= 0;
    }

    @Override
    public void serverTick() {
        setCooldown(Math.max(0, getCooldown() - 1));
    }
}
