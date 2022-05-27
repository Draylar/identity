package draylar.identity.impl;

import draylar.identity.api.variant.IdentityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface PlayerDataProvider {

    Set<IdentityType<?>> getUnlocked();
    void setUnlocked(Set<IdentityType<?>> unlocked);

    Set<IdentityType<?>> getFavorites();
    void setFavorites(Set<IdentityType<?>> favorites);

    int getRemainingHostilityTime();
    void setRemainingHostilityTime(int max);

    int getAbilityCooldown();
    void setAbilityCooldown(int cooldown);

    LivingEntity getIdentity();
    void setIdentity(@Nullable LivingEntity identity);
    boolean updateIdentity(@Nullable LivingEntity identity);

    IdentityType<?> getIdentityType();
}
