package draylar.identity.screen.widget;

import draylar.identity.Identity;
import draylar.identity.network.ClientNetworking;
import draylar.identity.screen.IdentityScreen;
import draylar.identity.screen.ScreenUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import spinnery.client.render.BaseRenderer;
import spinnery.common.utility.MouseUtilities;
import spinnery.widget.WButton;

import java.util.Arrays;
import java.util.List;

public class WEntityButton extends WButton {

    private final LivingEntity entity;
    private final int size;
    private boolean active;
    private boolean starred;
    private final IdentityScreen parent;

    public WEntityButton(LivingEntity entity, IdentityScreen parent, boolean starred, boolean current) {
        super();
        this.entity = entity;
        size = (int) (25 * (1 / (Math.max(entity.getHeight(), entity.getWidth()))));
        entity.setGlowing(true);
        this.parent = parent;
        this.starred = starred;
        this.active = current;
    }

    @Override
    public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
        // Update current Identity
        if(mouseButton == 0) {
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeIdentifier(Registry.ENTITY_TYPE.getId(entity.getType()));
            ClientSidePacketRegistry.INSTANCE.sendToServer(ClientNetworking.IDENTITY_REQUEST, packet);
            parent.disableAll();
            active = true;
        }

        // Add to favorites
        else if (mouseButton == 1) {
            boolean favorite = false;

            if(starred) {
                starred = false;
            } else {
                starred = true;
                favorite = true;
            }

            // Update server with information on favorite
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeIdentifier(Registry.ENTITY_TYPE.getId(entity.getType()));
            packet.writeBoolean(favorite);
            ClientSidePacketRegistry.INSTANCE.sendToServer(ClientNetworking.FAVORITE_UPDATE, packet);

            // TODO: re-sort screen?
        }

        super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
        BaseRenderer.getAdvancedItemRenderer().renderGuiItemIcon(matrices, provider, new ItemStack(Items.DIAMOND), -25, -25, 0);
        // DO NOT REMOVE THE ABOVE LINE OR THE ENTIRE SCREEN WILL BREAK

        ScreenUtils.drawEntity(
                (int) (getX() + this.getWidth() / 2),
                (int) (getY() + this.getHeight() * .75f),
                size,
                -10,
                -10,
                entity,
                15728880
        );

        // Render selected outline
        if(active) {
            BaseRenderer.drawTexturedQuad(matrices, provider, getX(), getY(), -15, getWidth(), getHeight(), Identity.id("textures/gui/selected.png"));
        }

        // Render favorite star
        if(starred) {
            BaseRenderer.drawTexturedQuad(matrices, provider, getX(), getY(), -15, 15, 15, Identity.id("textures/gui/star.png"));
        }

        // Draw tooltip
        float x = MouseUtilities.mouseX;
        float y = MouseUtilities.mouseY;

        if(getX() <= x && getX() + getWidth() >= x) {
            if(getY() <= y && getY() + getHeight() >= y) {
                drawTooltip(matrices, provider);
            }
        }
    }

    @Override
    public List<Text> getTooltip() {
        return Arrays.asList(entity.getDisplayName());
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }
}
