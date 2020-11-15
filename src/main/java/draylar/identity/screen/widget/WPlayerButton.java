package draylar.identity.screen.widget;

import com.google.common.collect.Lists;
import draylar.identity.Identity;
import draylar.identity.network.ClientNetworking;
import draylar.identity.screen.IdentityScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import spinnery.common.utility.MouseUtilities;
import spinnery.widget.WStaticImage;

import java.util.Arrays;
import java.util.List;

public class WPlayerButton extends WStaticImage {

    private final IdentityScreen parent;

    public WPlayerButton(IdentityScreen parent) {
        this.parent = parent;
        setTexture(Identity.id("textures/gui/player.png"));
    }

    @Override
    public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
        float x = MouseUtilities.mouseX;
        float y = MouseUtilities.mouseY;

        if(getX() <= x && getX() + getWidth() >= x) {
            if(getY() <= y && getY() + getHeight() >= y) {
                drawTooltip(matrices, provider);
            }
        }

        super.draw(matrices, provider);
    }

    @Override
    public List<Text> getTooltip() {
        return Arrays.asList(new LiteralText("Back to Player"));
    }

    @Override
    public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(Registry.ENTITY_TYPE.getId(EntityType.PLAYER));
        ClientSidePacketRegistry.INSTANCE.sendToServer(ClientNetworking.IDENTITY_REQUEST, packet);
        parent.disableAll();
        super.onMouseClicked(mouseX, mouseY, mouseButton);
    }
}
