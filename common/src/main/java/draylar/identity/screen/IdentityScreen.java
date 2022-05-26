package draylar.identity.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import draylar.identity.api.PlayerFavorites;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import draylar.identity.mixin.accessor.ScreenAccessor;
import draylar.identity.screen.widget.EntityWidget;
import draylar.identity.screen.widget.HelpWidget;
import draylar.identity.screen.widget.PlayerWidget;
import draylar.identity.screen.widget.SearchWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IdentityScreen extends Screen {

    private final List<LivingEntity> unlocked = new ArrayList<>();
    private final List<LivingEntity> renderEntities = new ArrayList<>();
    private final List<EntityWidget> entityWidgets = new ArrayList<>();
    private final SearchWidget searchBar = createSearchBar();
    private final PlayerWidget playerButton = createPlayerButton();
    private final ButtonWidget helpButton = createHelpButton();
    private String lastSearchContents = "";

    public IdentityScreen() {
        super(new LiteralText(""));
        super.init(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());

        // don't initialize if the player is null
        if(client.player == null) {
            client.setScreen(null);
            return;
        }

        populateRenderEntities();
        addDrawableChild(searchBar);
        addDrawableChild(playerButton);
        addDrawableChild(helpButton);

        // collect unlocked entities
        unlocked.addAll(collectUnlockedEntities(client.player));

        // Some users were experiencing a crash with this sort method, so we catch potential errors here
        // https://github.com/Draylar/identity/issues/87
        try {
            // sort unlocked based on favorites
            unlocked.sort((first, second) -> {
                if(PlayerFavorites.has(client.player, first.getType())) {
                    return -1;
                }

                return 1;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add entity widgets
        populateEntityWidgets(client.player, unlocked);

        // implement search handler
        searchBar.setChangedListener(text -> {
            focusOn(searchBar);

            // Only re-filter if the text contents changed
            if(!lastSearchContents.equals(text)) {
                ((ScreenAccessor) this).getSelectables().removeIf(button -> button instanceof EntityWidget);
                children().removeIf(button -> button instanceof EntityWidget);
                entityWidgets.clear();

                List<LivingEntity> filtered = unlocked
                        .stream()
                        .filter(livingEntity -> text.isEmpty() || livingEntity.getType().getTranslationKey().contains(text))
                        .collect(Collectors.toList());

                populateEntityWidgets(client.player, filtered);
            }

            lastSearchContents = text;
        });
    }

    @Override
    public void clearChildren() {

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        // Render background hint when no identities have been collected
        if(unlocked.isEmpty()) {
            TranslatableText message = new TranslatableText("identity.menu_hint");
            float xPosition = (getWindow().getWidth() / 2f) - (MinecraftClient.getInstance().textRenderer.getWidth(message) / 2f);
            float yPosition = (getWindow().getHeight() / 2f);
            MinecraftClient.getInstance().textRenderer.draw(matrices, message, xPosition, yPosition, 0xFFFFFF);
        }

        // tooltips
        for (Selectable selectable : ((ScreenAccessor) this).getSelectables()) {
            if(selectable instanceof PressableWidget button) {
                if(button.isHovered()) {
                    button.renderTooltip(matrices, mouseX, mouseY);
                    break;
                }
            }
        }

        searchBar.render(matrices, mouseX, mouseY, delta);
        playerButton.render(matrices, mouseX, mouseY, delta);
        helpButton.render(matrices, mouseX, mouseY, delta);
        renderEntityWidgets(matrices, mouseX, mouseY, delta);
    }

    public void renderEntityWidgets(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double scaledFactor = this.client.getWindow().getScaleFactor();
        int top = 35;

        matrices.push();
        RenderSystem.enableScissor(
                (int) ((double) 0 * scaledFactor),
                (int) ((double) 0 * scaledFactor),
                (int) ((double) width * scaledFactor),
                (int) ((double) (this.height - top) * scaledFactor));

        entityWidgets.forEach(widget -> {
            widget.render(matrices, mouseX, mouseY, delta);
        });

        RenderSystem.disableScissor();

        matrices.pop();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(entityWidgets.size() > 0) {
            float firstPos = entityWidgets.get(0).y;

            // Top section should always have mobs, prevent scrolling the entire list down the screen
            if(amount == 1 && firstPos >= 35) {
                return false;
            }

            ((ScreenAccessor) this).getSelectables().forEach(button -> {
                if(button instanceof EntityWidget widget) {
                    widget.y = (int) (widget.y + amount * 10);
                }
            });
        }

        return false;
    }

    private void populateEntityWidgets(ClientPlayerEntity player, List<LivingEntity> unlocked) {
        // add widget for each unlocked entity
        int x = 15;
        int y = 35;
        int rows = (int) Math.ceil(unlocked.size() / 7f);

        for (int yIndex = 0; yIndex <= rows; yIndex++) {
            for (int xIndex = 0; xIndex < 7; xIndex++) {
                int listIndex = yIndex * 7 + xIndex;

                if(listIndex < unlocked.size()) {
                    LivingEntity livingEntity = unlocked.get(listIndex);

                    // Determine whether this widget should start with the selection outline
                    boolean isCurrent = false;
                    if(PlayerIdentity.getIdentity(player) != null && livingEntity.getType().equals(PlayerIdentity.getIdentity(player).getType())) {
                        isCurrent = true;
                    }

                    EntityWidget entityWidget = new EntityWidget(
                            (getWindow().getScaledWidth() - 27) / 7f * xIndex + x,
                            getWindow().getScaledHeight() / 5f * yIndex + y,
                            (getWindow().getScaledWidth() - 27) / 7f,
                            getWindow().getScaledHeight() / 5f,
                            livingEntity,
                            this,
                            PlayerFavorites.has(player, livingEntity.getType()),
                            isCurrent
                    );

                    addDrawableChild(entityWidget);
                    entityWidgets.add(entityWidget);
                }
            }
        }
    }

    private void populateRenderEntities() {
        if(renderEntities.isEmpty()) {
            Registry.ENTITY_TYPE.forEach(type -> {
                Entity entity = type.create(MinecraftClient.getInstance().world);

                // only add living entities to cache
                if(entity instanceof LivingEntity) {
                    renderEntities.add((LivingEntity) entity);
                }
            });
        }
    }

    private List<LivingEntity> collectUnlockedEntities(ClientPlayerEntity player) {
        List<LivingEntity> unlocked = new ArrayList<>();

        // collect current unlocked identities (or allow all for creative users)
        renderEntities.forEach(entity -> {
            if(PlayerUnlocks.has(player, entity.getType()) || player.isCreative()) {
                unlocked.add(entity);
            }
        });

        return unlocked;
    }

    private SearchWidget createSearchBar() {
        return new SearchWidget(
                getWindow().getScaledWidth() / 2f - (getWindow().getScaledWidth() / 4f / 2) - 5,
                5,
                getWindow().getScaledWidth() / 4f,
                20f);
    }

    private PlayerWidget createPlayerButton() {
        return new PlayerWidget(
                getWindow().getScaledWidth() / 2f + (getWindow().getScaledWidth() / 8f) + 5,
                7,
                15,
                15,
                this);
    }

    private ButtonWidget createHelpButton() {
        return new HelpWidget(
                (int) (getWindow().getScaledWidth() / 2f - (getWindow().getScaledWidth() / 4f / 2) - 5) - 30,
                5,
                20,
                20);
    }

    public Window getWindow() {
        return MinecraftClient.getInstance().getWindow();
    }

    public void disableAll() {
        entityWidgets.forEach(button -> button.setActive(false));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseY < 35) {
            return searchBar.mouseClicked(mouseX, mouseY, button) || playerButton.mouseClicked(mouseX, mouseY, button) || helpButton.mouseClicked(mouseX, mouseY, button);
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
