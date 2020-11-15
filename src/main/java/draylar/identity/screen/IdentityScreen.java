package draylar.identity.screen;

import draylar.identity.cca.FavoriteIdentitiesComponent;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.cca.UnlockedIdentitiesComponent;
import draylar.identity.registry.Components;
import draylar.identity.screen.widget.EntityWidget;
import draylar.identity.screen.widget.PlayerWidget;
import draylar.identity.screen.widget.SearchWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class IdentityScreen extends Screen {

    private final List<LivingEntity> unlocked;
    private final List<LivingEntity> renderEntities = new ArrayList<>();
    private final List<EntityWidget> entityWidgets = new ArrayList<>();

    public IdentityScreen() {
        super(new LiteralText(""));

        populateRenderEntities();
        SearchWidget searchBar = addButton(createSearchBar());
        addButton(createPlayerButton());

        // get identity components from player
        UnlockedIdentitiesComponent unlockedComponent = Components.UNLOCKED_IDENTITIES.get(MinecraftClient.getInstance().player);
        FavoriteIdentitiesComponent favoritesComponent = Components.FAVORITE_IDENTITIES.get(MinecraftClient.getInstance().player);
        IdentityComponent currentIdentityComponent = Components.CURRENT_IDENTITY.get(MinecraftClient.getInstance().player);

        // collect unlocked entities
        unlocked = collectUnlockedEntities(unlockedComponent);

        // sort unlocked based on favorites
        Collections.sort(unlocked, (first, second) -> {
            if(favoritesComponent.has(first.getType())) {
                return -1;
            }

            return 1;
        });

        // add entity widgets
        populateEntityWidgets(unlocked, favoritesComponent, currentIdentityComponent);

        // implement search handler
        searchBar.setChangedListener(widget -> {
            buttons.removeIf(button -> {
                return button instanceof EntityWidget;
            });

            List<LivingEntity> filtered = unlocked
                    .stream()
                    .filter(livingEntity -> livingEntity.getType().getTranslationKey().contains(widget))
                    .collect(Collectors.toList());

            populateEntityWidgets(filtered, favoritesComponent, currentIdentityComponent);
        });
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        this.client = client;
        this.itemRenderer = client.getItemRenderer();
        this.textRenderer = client.textRenderer;
        this.width = width;
        this.height = height;
        this.setFocused(null);
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
        for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
            if (abstractButtonWidget.isHovered()) {
                abstractButtonWidget.renderToolTip(matrices, mouseX, mouseY);
                break;
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        float firstPos = entityWidgets.get(0).y;

        // Top section should always have mobs, prevent scrolling the entire list down the screen
        if(amount == 1 && firstPos >= 35) {
            return false;
        }

        buttons.forEach(button -> {
            if(button instanceof EntityWidget) {
                button.y = (int) (button.y + amount * 10);
            }
        });


        return false;
    }

    private void populateEntityWidgets(List<LivingEntity> unlocked, FavoriteIdentitiesComponent favorites, IdentityComponent current) {
        // add widget for each unlocked entity
        int x = 15;
        int y = 35;
        int rows = (int) Math.ceil(unlocked.size() / 7f);

        for(int yIndex = 0; yIndex <= rows; yIndex++) {
            for(int xIndex = 0; xIndex < 7; xIndex++) {
                int listIndex = yIndex * 7 + xIndex;

                if(listIndex < unlocked.size()) {
                    LivingEntity livingEntity = unlocked.get(listIndex);

                    // Determine whether this widget should start with the selection outline
                    boolean isCurrent = false;
                    if(current.getIdentity() != null && livingEntity.getType().equals(current.getIdentity().getType())) {
                        isCurrent = true;
                    }

                    EntityWidget entityWidget = new EntityWidget(
                            (getWindow().getScaledWidth() - 27) / 7f * xIndex + x,
                            getWindow().getScaledHeight() / 5f * yIndex + y,
                            (getWindow().getScaledWidth() - 27) / 7f,
                            getWindow().getScaledHeight() / 5f,
                            livingEntity,
                    this,
                            favorites.has(livingEntity.getType()),
                            isCurrent
                    );

                    addButton(entityWidget);
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

    private List<LivingEntity> collectUnlockedEntities(UnlockedIdentitiesComponent unlockedIdentitys) {
        List<LivingEntity> unlocked = new ArrayList<>();

        // collect current unlocked identities (or allow all for creative users)
        renderEntities.forEach(entity -> {
            if(unlockedIdentitys.has(entity.getType()) || MinecraftClient.getInstance().player.isCreative()) {
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

    public Window getWindow() {
        return MinecraftClient.getInstance().getWindow();
    }

    public void disableAll() {
        entityWidgets.forEach(button -> button.setActive(false));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
