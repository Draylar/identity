package draylar.identity.ability;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientGuiEvent;
import draylar.identity.api.PlayerAbilities;
import draylar.identity.api.PlayerIdentity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class AbilityOverlayRenderer {

    private static final int fadingTickRequirement = 0;
    private static int lastCooldown = 0;
    private static int ticksSinceUpdate = 0;
    private static boolean isFading = false;
    private static int fadingProgress = 0;

    public static void register() {
        ClientGuiEvent.RENDER_HUD.register((matrices, delta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            Window window = client.getWindow();
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if(identity == null) {
                return;
            }

            IdentityAbility<? extends LivingEntity> identityAbility = AbilityRegistry.get(identity.getType());

            if(identityAbility == null) {
                return;
            }

            if(client.currentScreen instanceof ChatScreen) {
                return;
            }

            double d = client.getWindow().getScaleFactor();
            int cd = PlayerAbilities.getCooldown(player);
            float lerpedCooldown = MathHelper.lerp(delta, cd - 1, cd);
            int max = AbilityRegistry.get(identity.getType()).getCooldown(identity);
            float cooldownScale = 1 - cd / (float) max;

            // CD has NOT updated since last tick. It is most likely full.
            if(cd == lastCooldown) {
                ticksSinceUpdate++;

                // If the CD has not updated, we are above the requirement, and we are not fading, start fading.
                if(ticksSinceUpdate > fadingTickRequirement && !isFading) {
                    isFading = true;
                    fadingProgress = 0;
                }
            }

            // CD updated in the last tick, and we are fading. Stop fading.
            else if(ticksSinceUpdate > fadingProgress) {
                ticksSinceUpdate = 0;
                isFading = false;
            }

            // Tick fading
            if(isFading) {
                fadingProgress = Math.min(50, fadingProgress + 1);
            } else {
                fadingProgress = Math.max(0, fadingProgress - 1);
            }

            if(player != null) {
                int start = (int) (window.getWidth() / d * .804);
                int end = (int) (window.getWidth() / d * .948);
                int diff = end - start;

//                DrawableHelper.fill(
//                        matrices,
//                        (int) (window.getWidth() / d * .8),
//                        (int) (window.getHeight() / d * .93),
//                        (int) (window.getWidth() / d * .95),
//                        (int) (window.getHeight() / d * .97),
//                        -1);

                int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
                int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
                int top = 245;

                matrices.push();
                if(cooldownScale != 1) {
                    RenderSystem.enableScissor(
                            (int) ((double) 0 * d),
                            (int) ((double) 0 * d),
                            (int) ((double) width * d),
                            (int) ((double) height * (.02 + .055 * cooldownScale) * d)); // min is 0.21, max is 0.76. dif = .55
                }

                // ending pop
                if(isFading) {
                    float fadeScalar = fadingProgress / 50f; // 0f -> 1f, 0 is start, 1 is end
                    float scale = 1f + (float) Math.sin(fadeScalar * 1.5 * Math.PI) - .25f;
                    scale = Math.max(scale, 0);
                    matrices.scale(scale, scale, scale);
                }

                // TODO: cache ability stack?
//                MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(new ItemStack(identityAbility.getIcon()), (int) (width * .95f), (int) (height * .92f));
                ItemStack stack = new ItemStack(identityAbility.getIcon());
//                BakedModel heldItemModel = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(stack, client.world, player);
//                renderGuiItemModel(matrices, stack, (int) (width * .95f), (int) (height * .92f), heldItemModel);
                MinecraftClient.getInstance().getItemRenderer()
                        .renderGuiItemIcon(stack, (int) (width * .95f), (int) (height * .92f));

                RenderSystem.disableScissor();
                matrices.pop();
            }
        });
    }

    private AbilityOverlayRenderer() {
        // NO-OP
    }
}
