package draylar.identity.screen.widget;

import draylar.identity.screen.IdentityHelpScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.Collections;

public class HelpWidget extends ButtonWidget {

    public HelpWidget(int x, int y, int width, int height) {
        super(x, y, width, height, new LiteralText("?"), (widget) -> {
            MinecraftClient.getInstance().setScreen(new IdentityHelpScreen());
        });
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;

        if(currentScreen != null) {
            currentScreen.renderTooltip(matrices, Collections.singletonList(new TranslatableText("identity.help")), mouseX, mouseY + 15);
        }
    }
}
