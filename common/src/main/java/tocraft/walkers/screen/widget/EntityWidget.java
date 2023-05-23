package tocraft.walkers.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.screen.WalkersScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

import java.util.Collections;

public class EntityWidget<T extends LivingEntity> extends PressableWidget {

    private final WalkersType<T> type;
    private final T entity;
    private final int size;
    private boolean active;
    private final WalkersScreen parent;

    public EntityWidget(float x, float y, float width, float height, WalkersType<T> type, T entity, WalkersScreen parent, boolean current) {
        super((int) x, (int) y, (int) width, (int) height, Text.of("")); // int x, int y, int width, int height, message
        this.type = type;
        this.entity = entity;
        size = (int) (25 * (1 / (Math.max(entity.getHeight(), entity.getWidth()))));
        entity.setGlowing(true);
        this.parent = parent;
        this.active = current;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = mouseX >= (double) this.x && mouseX < (double) (this.x + this.width) && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);

        if(bl) {
            // Update current Walkers
            if(button == 0 || WalkersConfig.getInstance().autoUnlockShapes()) {
                SwapPackets.sendSwapRequest(type);
                parent.disableAll();
                active = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        // Some entities (namely Aether mobs) crash when rendered in a GUI.
        // Unsure as to the cause, but this try/catch should prevent the game from entirely dipping out.
        try {
            InventoryScreen.drawEntity(x + this.getWidth() / 2, (int) (y + this.getHeight() * .75f), size, -10, -10, entity);
        } catch (Exception ignored) {

        }

        // Render selected outline
        if(active) {
            RenderSystem.setShaderTexture(0, Walkers.id("textures/gui/selected.png"));
            DrawableHelper.drawTexture(matrices, x, y, getWidth(), getHeight(), 0, 0, 48, 32, 48, 32);
        }

        // Draw tooltip
//        float x = MouseUtilities.mouseX;
//        float y = MouseUtilities.mouseY;
//
//        if(getX() <= x && getX() + getWidth() >= x) {
//            if(getY() <= y && getY() + getHeight() >= y) {
//                drawTooltip(matrices, provider);
//                renderToolTip();
//            }
//        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onPress() {

    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;

        if(currentScreen != null) {
            currentScreen.renderTooltip(matrices, Collections.singletonList(type.createTooltipText(entity)), mouseX, mouseY);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
