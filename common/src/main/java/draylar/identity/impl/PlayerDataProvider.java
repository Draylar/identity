package draylar.identity.impl;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerDataProvider {

    List<Identifier> getUnlocked();
    void setUnlocked(List<Identifier> unlocked);

    List<Identifier> getFavorites();
    void setFavorites(List<Identifier> favorites);

    int getRemainingHostilityTime();
    void setRemainingHostilityTime(int max);

    int getAbilityCooldown();
    void setAbilityCooldown(int cooldown);

    LivingEntity getIdentity();
    void setIdentity(@Nullable LivingEntity identity);
    boolean updateIdentity(@Nullable LivingEntity identity);
}
