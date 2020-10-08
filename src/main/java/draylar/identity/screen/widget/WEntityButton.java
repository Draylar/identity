package draylar.identity.screen.widget;

import draylar.identity.Identity;
import draylar.identity.screen.ScreenUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import io.github.cottonmc.cotton.gui.widget.WButton;

public class WEntityButton extends WButton {

    private final LivingEntity entity;
    private final int size;
    private static final int NO_LIGHT = LightmapTextureManager.pack(0, 0);

    public WEntityButton(LivingEntity entity) {
        super();
        this.entity = entity;
        size = (int) (25 * (1 / (Math.max(entity.getHeight(), entity.getWidth()))));
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(Registry.ENTITY_TYPE.getId(entity.getType()));
        ClientSidePacketRegistry.INSTANCE.sendToServer(Identity.IDENTITY_REQUEST, packet);
        super.onClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        ScreenUtils.drawEntity(
                (int) (x + this.getWidth() / 2),
                (int) (y + this.getHeight() * .75f),
                size,
                -10,
                -10,
                entity,
                isFocused() ? 15728880 : NO_LIGHT
        );
    }
}
