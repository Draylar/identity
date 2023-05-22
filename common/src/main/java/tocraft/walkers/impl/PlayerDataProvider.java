package tocraft.walkers.impl;

import tocraft.walkers.api.variant.WalkersType;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface PlayerDataProvider {

    Set<WalkersType<?>> getUnlocked();
    void setUnlocked(Set<WalkersType<?>> unlocked);

    Set<WalkersType<?>> getFavorites();
    void setFavorites(Set<WalkersType<?>> favorites);

    int getRemainingHostilityTime();
    void setRemainingHostilityTime(int max);

    int getAbilityCooldown();
    void setAbilityCooldown(int cooldown);

    LivingEntity getWalkers();
    void setWalkers(@Nullable LivingEntity walkers);
    boolean updateWalkers(@Nullable LivingEntity walkers);

    WalkersType<?> getWalkersType();
}
