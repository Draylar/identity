package draylar.identity.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public interface EntityArmProvider<T extends LivingEntity> {
    ModelPart getArm(T entity, EntityModel<T> model);
}
