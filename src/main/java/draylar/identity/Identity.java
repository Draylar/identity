package draylar.identity;

import draylar.identity.ability.AbilityRegistry;
import draylar.identity.ability.IdentityAbility;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.config.IdentityConfig;
import draylar.identity.network.ServerNetworking;
import draylar.identity.registry.Commands;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import draylar.identity.registry.EventHandlers;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class Identity implements ModInitializer {

    public static final IdentityConfig CONFIG = AutoConfig.register(IdentityConfig.class, JanksonConfigSerializer::new).getConfig();
    public static final AbilitySource ABILITY_SOURCE = Pal.getAbilitySource(id("equipped_identity"));

    @Override
    public void onInitialize() {
        EntityTags.init();
        AbilityRegistry.init();
        EventHandlers.init();
        Commands.init();
        ServerNetworking.init();

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if(world.getTime() % (60 * 20) == 0) {
                if(Identity.CONFIG.switchEveryMinute) {
                    world.getPlayers().forEach(player -> {
                        if (player.isAlive()) {
                            IdentityComponent identityComponent = Components.CURRENT_IDENTITY.get(player);

                            // find a living, non-player identity to swap to
                            Entity entity;
                            do {
                                entity = Registry.ENTITY_TYPE.get(world.getRandom().nextInt(Registry.ENTITY_TYPE.getEntries().size())).create(world);
                            } while (!(entity instanceof LivingEntity) || entity instanceof PlayerEntity);
                            identityComponent.setIdentity((LivingEntity) entity);
                        }
                    });
                }
            }
        });
    }

    public static Identifier id(String name) {
        return new Identifier("identity", name);
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
