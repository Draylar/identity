package draylar.identity.api.model;

import net.minecraft.client.util.math.MatrixStack;

public interface ArmRenderingManipulator<T> {
    void run(MatrixStack stack, T model);
}
