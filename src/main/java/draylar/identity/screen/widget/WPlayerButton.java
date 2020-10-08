package draylar.identity.screen.widget;

import draylar.identity.Identity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import io.github.cottonmc.cotton.gui.widget.WSprite;

public class WPlayerButton extends WSprite {

    public WPlayerButton() {
        super(Identity.id("textures/gui/player.png"));
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(Registry.ENTITY_TYPE.getId(EntityType.PLAYER));
        ClientSidePacketRegistry.INSTANCE.sendToServer(Identity.IDENTITY_REQUEST, packet);
        super.onClick(mouseX, mouseY, mouseButton);
    }
}
