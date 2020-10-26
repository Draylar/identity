package draylar.identity;

import draylar.identity.api.ability.IdentityAbilities;
import draylar.identity.api.ability.IdentityAbility;
import draylar.identity.config.IdentityConfig;
import draylar.identity.registry.Commands;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import draylar.identity.registry.EventHandlers;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class Identity implements ModInitializer {

    public static final IdentityConfig CONFIG = AutoConfig.register(IdentityConfig.class, JanksonConfigSerializer::new).getConfig();

    public static final Identifier IDENTITY_REQUEST = id("request");
    public static final AbilitySource ABILITY_SOURCE = Pal.getAbilitySource(id("equipped_identity"));

    @Override
    public void onInitialize() {
        EntityTags.init();
        IdentityAbilities.init();
        EventHandlers.init();
        Commands.init();

        registerIdentityRequestPacketHandler();
        registerAbilityItemUseHandler();
    }

    public static Identifier id(String name) {
        return new Identifier("identity", name);
    }

    private void registerAbilityItemUseHandler() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if(!world.isClient) {
                LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

                if (identity != null) {
                    ItemStack heldStack = player.getStackInHand(hand);

                    // ensure cooldown is valid (0) for custom use action
                    if(player.getItemCooldownManager().getCooldownProgress(heldStack.getItem(), 0) <= 0) {
                        IdentityAbility ability = IdentityAbilities.get((EntityType<? extends LivingEntity>) identity.getType(), heldStack.getItem());

                        if(ability != null) {
                            return ability.onUse(player, identity, world, heldStack, hand);
                        }
                    }
                }
            }

            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }

    private void registerIdentityRequestPacketHandler() {
        ServerSidePacketRegistry.INSTANCE.register(IDENTITY_REQUEST, (context, packet) -> {
            EntityType<?> type = Registry.ENTITY_TYPE.get(packet.readIdentifier());

            if (type.equals(EntityType.PLAYER)) {
                Components.CURRENT_IDENTITY.get(context.getPlayer()).setIdentity(null);
            } else {
                Components.CURRENT_IDENTITY.get(context.getPlayer()).setIdentity((LivingEntity) type.create(context.getPlayer().world));
            }

            context.getPlayer().calculateDimensions();
        });
    }

    public static boolean hasFlyingPermissions(ServerPlayerEntity player) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

        if(identity != null && Identity.CONFIG.enableFlight && EntityTags.FLYING.contains(identity.getType())) {
            List<String> requiredAdvancements = CONFIG.advancementsRequiredForFlight;

            // requires at least 1 advancement, check if player has them
            if (!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    Advancement advancement = player.server.getAdvancementLoader().get(new Identifier(requiredAdvancement));
                    AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);

                    if (!progress.isDone()) {
                        hasPermission = false;
                    }
                }

                return hasPermission;
            }


            return true;
        }

        return false;
    }

    public static boolean isAquatic(LivingEntity entity) {
        return entity instanceof WaterCreatureEntity || entity instanceof GuardianEntity;
    }
}
