package draylar.identity.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import draylar.identity.Identity;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.network.impl.FavoritePackets;
import draylar.identity.network.impl.SwapPackets;
import draylar.identity.screen.IdentityScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

import java.util.Collections;

public class EntityWidget<T extends LivingEntity> extends PressableWidget {

    private final IdentityType<T> type;
    private final T entity;
    private final int size;
    private boolean active;
    private boolean starred;
    private final IdentityScreen parent;

    public EntityWidget(float x, float y, float width, float height, IdentityType<T> type, T entity, IdentityScreen parent, boolean starred, boolean current) {
        super((int) x, (int) y, (int) width, (int) height, Text.of("")); // int x, int y, int width, int height, message
        this.type = type;
        this.entity = entity;
        size = (int) (25 * (1 / (Math.max(entity.getHeight(), entity.getWidth()))));
        entity.setGlowing(true);
        this.parent = parent;
        this.starred = starred;
        this.active = current;
        setTooltip(Tooltip.of(type.createTooltipText(entity)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);

        if(bl) {
            // Update current Identity
            if(button == 0) {
                SwapPackets.sendSwapRequest(type);
                parent.disableAll();
                active = true;
            }

            // Add to favorites
            else if(button == 1) {
                boolean favorite = false;

                if(starred) {
                    starred = false;
                } else {
                    starred = true;
                    favorite = true;
                }

                // Update server with information on favorite
                FavoritePackets.sendFavoriteRequest(type, favorite);

                // TODO: re-sort screen?
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        // Some entities (namely Aether mobs) crash when rendered in a GUI.
        // Unsure as to the cause, but this try/catch should prevent the game from entirely dipping out.
        try {
            InventoryScreen.drawEntity(getX() + this.getWidth() / 2, (int) (getY() + this.getHeight() * .75f), size, -10, -10, entity);
        } catch (Exception ignored) {

        }

        // Render selected outline
        if(active) {
            RenderSystem.setShaderTexture(0, Identity.id("textures/gui/selected.png"));
            DrawableHelper.drawTexture(matrices, getX(), getY(), getWidth(), getHeight(), 0, 0, 48, 32, 48, 32);
        }

        // Render favorite star
        if(starred) {
            RenderSystem.setShaderTexture(0, Identity.id("textures/gui/star.png"));
            DrawableHelper.drawTexture(matrices, getX(), getY(), 0, 0, 15, 15, 15, 15);
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
}
