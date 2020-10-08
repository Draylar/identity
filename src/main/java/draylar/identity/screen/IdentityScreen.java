package draylar.identity.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

import draylar.identity.screen.IdentityGui;

public class IdentityScreen extends CottonClientScreen {
    public IdentityScreen() {
        super(new IdentityGui());
    }
    public Window getWindow() {
        return MinecraftClient.getInstance().getWindow();
    }
}
