package draylar.identity.screen;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import net.minecraft.util.Identifier;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.LiteralText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.util.TriState;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;

import draylar.identity.cca.IdentityComponent;
import draylar.identity.cca.UnlockedIdentitysComponent;
import draylar.identity.screen.widget.WEntityButton;
import draylar.identity.screen.widget.WPlayerButton;
import draylar.identity.screen.widget.WSearchBar;
import draylar.identity.registry.Components;

public class IdentityGui extends LightweightGuiDescription {
    private final List<LivingEntity> renderEntities = new ArrayList<>();
    private List<WEntityButton> renderedButtons = new ArrayList<>();
    public IdentityGui() {
        WPlainPanel container = new WPlainPanel();
        WScrollPanel scroller = new WScrollPanel(container);
        WPlainPanel root = new WPlainPanel();

        scroller.setScrollingHorizontally(TriState.FALSE);
        setRootPanel(root);
        setFullscreen(true);

        root.add(
            scroller, 0, 25,
            getWindow().getScaledWidth(),
            getWindow().getScaledHeight() - 25
        );

        // attempt to populate renderEntities list
        populateRenderEntities();

        // get identity components from player
        UnlockedIdentitysComponent unlockedIdentitys = Components.UNLOCKED_IDENTITIES.get(MinecraftClient.getInstance().player);
        IdentityComponent currentIdentity = Components.CURRENT_IDENTITY.get(MinecraftClient.getInstance().player);

        // collect unlocked entities
        List<LivingEntity> unlocked = collectUnlockedEntities(unlockedIdentitys);

        populateEntityWidgets(container, unlocked);

        WPlayerButton playerButton = new WPlayerButton();

        root.add(
            playerButton,
            getWindow().getScaledWidth() * 5 / 8 + 5,
            7,
            15,
            15
        );


        WSearchBar searchBar = new WSearchBar();
        requestFocus(searchBar);
        root.add(
            searchBar,
            getWindow().getScaledWidth() * 3 / 8 - 5,
            5,
            getWindow().getScaledWidth() / 4,
            20
        );

        // implement search handler
        searchBar.setPostKeyPressListener(widget -> {
            for (WEntityButton button : renderedButtons) {
                container.remove(button);
            }
            renderedButtons.clear();

            List<LivingEntity> filtered = unlocked
                    .stream()
                    .filter(livingEntity -> livingEntity.getType().getTranslationKey().contains(widget.getText()))
                    .collect(Collectors.toList());

            populateEntityWidgets(container, filtered);
        });

        root.validate(this);
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
    private void populateEntityWidgets(WPlainPanel container, List<LivingEntity> unlocked) {

        for (int listIndex = 0; listIndex < unlocked.size(); listIndex++) {
            int y = listIndex / 7;
            int x = listIndex % 7;
            LivingEntity livingEntity = unlocked.get(listIndex);

            WEntityButton button = new WEntityButton(livingEntity);
            renderedButtons.add(button);
            container.add(
                button,
                (getWindow().getScaledWidth() - 27) / 7 * x + 13,
                (int) Math.ceil(getWindow().getScaledHeight() / 5 * (y + 0.5)),
                (getWindow().getScaledWidth() - 27) / 7,
                getWindow().getScaledHeight() / 5
            );
        }
    }
    private List<LivingEntity> collectUnlockedEntities(UnlockedIdentitysComponent unlockedIdentitys) {
        List<LivingEntity> unlocked = new ArrayList<>();

        // collect current unlocked identities (or allow all for creative users)
        renderEntities.forEach(entity -> {
            if(unlockedIdentitys.has(entity.getType()) || MinecraftClient.getInstance().player.isCreative()) {
                unlocked.add(entity);
            }
        });

        return unlocked;
    }
    public Window getWindow() {
        return MinecraftClient.getInstance().getWindow();
    }
}
