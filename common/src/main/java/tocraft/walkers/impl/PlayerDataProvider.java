package tocraft.walkers.impl;

import tocraft.walkers.api.variant.ShapeType;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface PlayerDataProvider {

    ShapeType<?> get2ndShape();
    void set2ndShape(ShapeType<?> unlocked);

    int getRemainingHostilityTime();
    void setRemainingHostilityTime(int max);

    int getAbilityCooldown();
    void setAbilityCooldown(int cooldown);

    LivingEntity getCurrentShape();
    void setCurrentShape(@Nullable LivingEntity walkers);
    boolean updateShapes(@Nullable LivingEntity walkers);

    ShapeType<?> getCurrentShapeType();
}
