package draylar.identity.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SearchWidget extends TextFieldWidget {

    public SearchWidget(float x, float y, float width, float height) {
        super(MinecraftClient.getInstance().textRenderer, (int) x, (int) y, (int) width, (int) height, Text.of(""));
    }
}
