package draylar.identity.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class IdentityCommand {

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, ctx, b) -> {
            LiteralCommandNode<ServerCommandSource> rootNode = CommandManager
                    .literal("identity")
                    .requires(source -> source.hasPermissionLevel(2))
                    .build();

            /*
            Used to give the specified Identity to the specified Player.
             */
            LiteralCommandNode<ServerCommandSource> grantNode = CommandManager
                    .literal("grant")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.literal("everything")
                                    .executes(context -> {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        for (IdentityType<?> type : IdentityType.getAllTypes(player.world)) {
                                            if(!PlayerUnlocks.has(player, type)) {
                                                PlayerUnlocks.unlock(player, type);
                                            }
                                        }

                                        return 1;
                                    })
                            )
                            .then(CommandManager.argument("identity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        grant(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                new IdentityType(Registry.ENTITY_TYPE.get(EntitySummonArgumentType.getEntitySummon(context, "identity")))
                                        );
                                        return 1;
                                    })
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> revokeNode = CommandManager
                    .literal("revoke")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.literal("everything")
                                    .executes(context -> {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        for (IdentityType<?> type : IdentityType.getAllTypes(player.world)) {
                                            if(!PlayerUnlocks.has(player, type)) {
                                                PlayerUnlocks.revoke(player, type);
                                            }
                                        }

                                        return 1;
                                    })
                            )
                            .then(CommandManager.argument("identity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        revoke(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                new IdentityType(Registry.ENTITY_TYPE.get(EntitySummonArgumentType.getEntitySummon(context, "identity")))
                                        );
                                        return 1;
                                    })
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> equip = CommandManager
                    .literal("equip")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.argument("identity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        equip(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "identity")
                                        );
                                        return 1;
                                    })
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> unequip = CommandManager
                    .literal("unequip")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .executes(context -> {
                                unequip(
                                        context.getSource().getPlayer(),
                                        EntityArgumentType.getPlayer(context, "player")
                                );
                                return 1;
                            })
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> test = CommandManager
                    .literal("test")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.literal("not")
                                    .then(CommandManager.argument("identity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                            .executes(context -> {
                                                return testNot(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        EntitySummonArgumentType.getEntitySummon(context, "identity")
                                                );
                                            })
                                    )
                            )
                            .then(CommandManager.argument("identity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        return test(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "identity")
                                        );
                                    })
                            )
                    )
                    .build();

            rootNode.addChild(grantNode);
            rootNode.addChild(revokeNode);
            rootNode.addChild(equip);
            rootNode.addChild(unequip);
            rootNode.addChild(test);

            dispatcher.getRoot().addChild(rootNode);
        });
    }

    private static int test(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity) {
        EntityType<?> type = Registry.ENTITY_TYPE.get(identity);

        if(PlayerIdentity.getIdentity(player) != null && PlayerIdentity.getIdentity(player).getType().equals(type)) {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.test_positive", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
            }

            return 1;
        }

        if(IdentityConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("identity.test_failed", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
        }

        return 0;
    }

    private static int testNot(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity) {
        EntityType<?> type = Registry.ENTITY_TYPE.get(identity);

        if(PlayerIdentity.getIdentity(player) != null && !PlayerIdentity.getIdentity(player).getType().equals(type)) {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.test_failed", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
            }

            return 1;
        }

        if(IdentityConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("identity.test_positive", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
        }

        return 0;
    }

    private static void grant(ServerPlayerEntity source, ServerPlayerEntity player, IdentityType<?> type) {
        if(!PlayerUnlocks.has(player, type)) {
            boolean result = PlayerUnlocks.unlock(player, type);

            if(result && IdentityConfig.getInstance().logCommands()) {
                player.sendMessage(Text.translatable("identity.unlock_entity", Text.translatable(type.getEntityType().getTranslationKey())), true);
                source.sendMessage(Text.translatable("identity.grant_success", Text.translatable(type.getEntityType().getTranslationKey()), player.getDisplayName()), true);
            }
        } else {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.already_has", player.getDisplayName(), Text.translatable(type.getEntityType().getTranslationKey())), true);
            }
        }
    }

    private static void revoke(ServerPlayerEntity source, ServerPlayerEntity player, IdentityType<?> type) {
        if(PlayerUnlocks.has(player, type)) {
            PlayerUnlocks.revoke(player, type);

            if(IdentityConfig.getInstance().logCommands()) {
                player.sendMessage(Text.translatable("identity.revoke_entity", Text.translatable(type.getEntityType().getTranslationKey())), true);
                source.sendMessage(Text.translatable("identity.revoke_success", Text.translatable(type.getEntityType().getTranslationKey()), player.getDisplayName()), true);
            }
        } else {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.does_not_have", player.getDisplayName(), Text.translatable(type.getEntityType().getTranslationKey())), true);
            }
        }
    }

    private static void equip(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity) {
        EntityType<?> entity = Registry.ENTITY_TYPE.get(identity);
        Entity createdEntity = entity.create(player.world);

        if(createdEntity instanceof LivingEntity living) {
            @Nullable IdentityType<?> defaultType = IdentityType.from(living);

            if(defaultType != null) {
                boolean result = PlayerIdentity.updateIdentity(player, defaultType, (LivingEntity) createdEntity);
                if(result && IdentityConfig.getInstance().logCommands()) {
                    source.sendMessage(Text.translatable("identity.equip_success", Text.translatable(entity.getTranslationKey()), player.getDisplayName()), true);
                }
            }
        }
    }

    private static void unequip(ServerPlayerEntity source, ServerPlayerEntity player) {
        boolean result = PlayerIdentity.updateIdentity(player, null, null);

        if(result && IdentityConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("identity.unequip_success", player.getDisplayName()), false);
        }
    }
}
