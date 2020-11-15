package draylar.identity.screen;

import draylar.identity.cca.FavoriteIdentitiesComponent;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.cca.UnlockedIdentitiesComponent;
import draylar.identity.registry.Components;
import draylar.identity.screen.widget.WEntityButton;
import draylar.identity.screen.widget.WPlayerButton;
import draylar.identity.screen.widget.WSearchBar;
import draylar.identity.screen.widget.WVerticalScrollableUnfadingContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import spinnery.client.screen.BaseScreen;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WInterface;
import spinnery.widget.WStaticText;
import spinnery.widget.WVerticalScrollableContainer;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IdentityScreen extends BaseScreen {

    private final List<LivingEntity> renderEntities = new ArrayList<>();
    private final List<WEntityButton> buttons = new ArrayList<>();

    public IdentityScreen() {
        super();

        // attempt to populate renderEntities list
        populateRenderEntities();

        // base interface, scrollbar, and searchbar setup
        WInterface wInterface = getInterface();
        WVerticalScrollableUnfadingContainer container = createScrollableContainer(wInterface);
        WSearchBar searchBar = createSearchBar(wInterface);

        // add player icon next to searchbar
        createPlayerButton(wInterface);

        // get identity components from player
        UnlockedIdentitiesComponent unlockedComponent = Components.UNLOCKED_IDENTITIES.get(MinecraftClient.getInstance().player);
        FavoriteIdentitiesComponent favoritesComponent = Components.FAVORITE_IDENTITIES.get(MinecraftClient.getInstance().player);
        IdentityComponent currentIdentityComponent = Components.CURRENT_IDENTITY.get(MinecraftClient.getInstance().player);

        // collect unlocked entities
        List<LivingEntity> unlocked = collectUnlockedEntities(unlockedComponent);

        // sort unlocked based on favorites
        Collections.sort(unlocked, (first, second) -> {
            if(favoritesComponent.has(first.getType())) {
                return -1;
            }

            return 1;
        });

        // add entity widgets
        populateEntityWidgets(container, unlocked, favoritesComponent, currentIdentityComponent);

        // add hint if the player has no unlocks on what to do
        if(unlocked.isEmpty()) {
            TranslatableText message = new TranslatableText("identity.menu_hint");
            float xPosition = (container.getWidth() / 2) - (MinecraftClient.getInstance().textRenderer.getWidth(message) / 2f);
            float yPosition = (container.getHeight() / 2);
            WStaticText hint = wInterface.createChild(WStaticText::new).setText(message).setPosition(Position.of(wInterface, xPosition, yPosition));
        }

        // implement search handler
        searchBar.setPostKeyPressListener(widget -> {
            container.getWidgets().clear();

            List<LivingEntity> filtered = unlocked
                    .stream()
                    .filter(livingEntity -> livingEntity.getType().getTranslationKey().contains(widget.getText()))
                    .collect(Collectors.toList());

            populateEntityWidgets(container, filtered, favoritesComponent, currentIdentityComponent);
        });
    }

    private void populateEntityWidgets(WVerticalScrollableContainer container, List<LivingEntity> unlocked, FavoriteIdentitiesComponent favorites, IdentityComponent current) {
        // add widget for each unlocked entity
        int x = 0;
        int y = 0;
        int rows = (int) Math.ceil(unlocked.size() / 7f);

        for(int yIndex = 0; yIndex <= rows; yIndex++) {
            List<WEntityButton> widgets = new ArrayList<>();

            for(int xIndex = 0; xIndex < 7; xIndex++) {
                int listIndex = yIndex * 7 + xIndex;

                if(listIndex < unlocked.size()) {
                    LivingEntity livingEntity = unlocked.get(listIndex);

                    // Determine whether this widget should start with the selection outline
                    boolean isCurrent = false;
                    if(current.getIdentity() != null && livingEntity.getType().equals(current.getIdentity().getType())) {
                        isCurrent = true;
                    }

                    // Add and create button
                    WEntityButton button = new WEntityButton(livingEntity, this, favorites.has(livingEntity.getType()), isCurrent)
                            .setSize(Size.of((getWindow().getScaledWidth() - 27) / 7f, getWindow().getScaledHeight() / 5f))
                            .setPosition(Position.of(container, (getWindow().getScaledWidth() - 27) / 7f * x, getWindow().getScaledHeight() / 5f * y));

                    buttons.add(button);

//                    button.setOnAlign(widget -> widget.setSize(Size.of((getWindow().getScaledWidth() - 27) / 7f, getWindow().getScaledHeight() / 5f))
//                            .setPosition(Position.of(container, (getWindow().getScaledWidth() - 27) / 7f * x, getWindow().getScaledHeight() / 5f * y)));

                    widgets.add(button);
                }
            }

            container.addRow(widgets.toArray(new WAbstractWidget[widgets.size()]));
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

    private WVerticalScrollableUnfadingContainer createScrollableContainer(WInterface wInterface) {
        WVerticalScrollableUnfadingContainer container = wInterface
                .createChild(WVerticalScrollableUnfadingContainer::new)
                .setSize(Size.of(getWindow().getScaledWidth(), getWindow().getScaledHeight() - 25))
                .setPosition(Position.of(wInterface, 0, 25));

        container.setOnAlign(widget -> widget.setSize(Size.of(getWindow().getScaledWidth(), getWindow().getScaledHeight() - 25))
                .setPosition(Position.of(wInterface, 0, 25)));

        return container;
    }

    private WSearchBar createSearchBar(WInterface wInterface) {
        WSearchBar searchBar = wInterface
                .createChild(WSearchBar::new)
                .setSize(Size.of(getWindow().getScaledWidth() / 4f, 20f))
                .setPosition(Position.of(getWindow().getScaledWidth() / 2f - (getWindow().getScaledWidth() / 4f / 2) - 5, 5, 0));

        searchBar.setOnAlign(widget -> widget
                .setSize(Size.of(getWindow().getScaledWidth() / 4f, 20f))
                .setPosition(Position.of(getWindow().getScaledWidth() / 2f - (getWindow().getScaledWidth() / 4f / 2) - 5, 5, 0)));

        return searchBar;
    }

    private WPlayerButton createPlayerButton(WInterface wInterface) {
        WPlayerButton playerButton = wInterface
                .createChild(() -> new WPlayerButton(this))
                .setSize(Size.of(15f, 15f))
                .setPosition(Position.of(getWindow().getScaledWidth() / 2f + (getWindow().getScaledWidth() / 8f) + 5, 7, 0));

        playerButton.setOnAlign(widget -> widget
                .setPosition(Position.of(getWindow().getScaledWidth() / 2f + (getWindow().getScaledWidth() / 8f) + 5, 7, 0)));

        return playerButton;
    }

    public Window getWindow() {
        return MinecraftClient.getInstance().getWindow();
    }

    public void disableAll() {
        buttons.forEach(button -> button.setActive(false));
    }
}
