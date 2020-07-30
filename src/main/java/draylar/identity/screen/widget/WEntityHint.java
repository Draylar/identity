package draylar.identity.screen.widget;

import draylar.identity.screen.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import spinnery.widget.WButton;

public class WEntityHint extends WButton {

    private final EntityType type;
    private LivingEntity entity;
    private final int size;

    public WEntityHint(EntityType type) {
        super();
        this.type = type;
        size = (int) (25 * (1 / (Math.max(type.getHeight(), type.getWidth()))));
    }

    @Override
    public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
        ClientWorld world = MinecraftClient.getInstance().world;

        if(world != null) {
            if (entity == null) {
                entity = (LivingEntity) type.create(world);
            }

            ScreenUtils.drawEntity(
                    (int) (getX() + this.getWidth() / 2),
                    (int) (getY() + this.getHeight() * .75f),
                    size,
                    -10,
                    -10,
                    entity,
                    15728880
            );
        }
    }
}
