package tocraft.walkers.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import tocraft.walkers.Walkers;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.screen.WalkersScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PlayerWidget extends PressableWidget {

    private final WalkersScreen parent;

    public PlayerWidget(float x, float y, float width, float height, WalkersScreen parent) {
        super((int) x, (int) y, (int) width, (int) height, Text.of("")); // int x, int y, int width, int height, message
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        float x = MouseUtilities.mouseX;
//        float y = MouseUtilities.mouseY;
//
//        if(getX() <= x && getX() + getWidth() >= x) {
//            if(getY() <= y && getY() + getHeight() >= y) {
//                drawTooltip(matrices, provider);
//            }
//        }
//

        RenderSystem.setShaderTexture(0, Walkers.id("textures/gui/player.png"));
        DrawableHelper.drawTexture(matrices, x, y, 16, 16, 0, 0, 8, 8, 8, 8);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void onPress() {
        SwapPackets.sendSwapRequest(null);
        parent.disableAll();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
