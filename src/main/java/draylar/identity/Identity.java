package draylar.identity;

import draylar.identity.api.ability.IdentityAbilities;
import draylar.identity.api.ability.IdentityAbility;
import draylar.identity.config.IdentityConfig;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;

public class Identity implements ModInitializer {

    public static final IdentityConfig CONFIG = AutoConfig.register(IdentityConfig.class, JanksonConfigSerializer::new).getConfig();

    public static final Identifier IDENTITY_REQUEST = id("request");

    @Override
    public void onInitialize() {
        EntityTags.init();
        Components.init();
        IdentityAbilities.init();

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
                    if(heldStack.getCooldown() <= 0) {
                        IdentityAbility ability = IdentityAbilities.get((EntityType<? extends LivingEntity>) identity.getType(), heldStack.getItem());

                        if(ability != null) {
                            ability.onUse(player, world, heldStack, hand);
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
}
