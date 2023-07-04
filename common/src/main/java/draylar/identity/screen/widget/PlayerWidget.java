package draylar.identity.screen.widget;

import draylar.identity.Identity;
import draylar.identity.network.impl.SwapPackets;
import draylar.identity.screen.IdentityScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class PlayerWidget extends PressableWidget {

    private final IdentityScreen parent;

    public PlayerWidget(float x, float y, float width, float height, IdentityScreen parent) {
        super((int) x, (int) y, (int) width, (int) height, Text.of("")); // int x, int y, int width, int height, message
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        float x = MouseUtilities.mouseX;
//        float y = MouseUtilities.mouseY;
//
//        if(getX() <= x && getX() + getWidth() >= x) {
//            if(getY() <= y && getY() + getHeight() >= y) {
//                drawTooltip(matrices, provider);
//            }
//        }
//

        context.drawTexture(Identity.id("textures/gui/player.png"), getX(), getY(), 16, 16, 0, 0, 8, 8, 8, 8);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void onPress() {
        SwapPackets.sendSwapRequest(null);
        parent.disableAll();
    }
}
