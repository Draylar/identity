package draylar.identity.screen.widget;

import draylar.identity.screen.IdentityHelpScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.function.Supplier;

public class HelpWidget extends ButtonWidget {

    public HelpWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.of("?"), (widget) -> {
            MinecraftClient.getInstance().setScreen(new IdentityHelpScreen());
        }, Supplier::get);

        setTooltip(Tooltip.of(Text.translatable("identity.help")));
    }
}
