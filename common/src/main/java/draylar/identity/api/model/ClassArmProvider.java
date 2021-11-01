package draylar.identity.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public interface ClassArmProvider<T> {
    ModelPart getArm(LivingEntity entity, T model);
}
