package draylar.identity.screen.widget;

import draylar.identity.Identity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import spinnery.widget.WStaticImage;

public class WPlayerButton extends WStaticImage {

    public WPlayerButton() {
        setTexture(Identity.id("textures/gui/player.png"));
    }

    @Override
    public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(Registry.ENTITY_TYPE.getId(EntityType.PLAYER));
        ClientSidePacketRegistry.INSTANCE.sendToServer(Identity.IDENTITY_REQUEST, packet);
        super.onMouseClicked(mouseX, mouseY, mouseButton);
    }
}
