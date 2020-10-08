package draylar.identity.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.cca.UnlockedIdentitysComponent;
import draylar.identity.registry.Components;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IdentityCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, b) -> {
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
                            .then(CommandManager.argument("identity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        grant(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "identity")
                                        );
                                        return 1;
                                    })
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> revokeNode = CommandManager
                    .literal("revoke")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.argument("identity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        revoke(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntitySummonArgumentType.getEntitySummon(context, "identity")
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
        IdentityComponent current = Components.CURRENT_IDENTITY.get(player);
        EntityType<?> type = Registry.ENTITY_TYPE.get(identity);

        if(current.getIdentity() != null && current.getIdentity().getType().equals(type)) {
            source.sendMessage(new TranslatableText("identity.test_positive", player.getDisplayName(), new TranslatableText(type.getTranslationKey())), false);
            return 1;
        }

        source.sendMessage(new TranslatableText("identity.test_failed", player.getDisplayName(), new TranslatableText(type.getTranslationKey())), false);
        return 0;
    }

    private static int testNot(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity) {
        IdentityComponent current = Components.CURRENT_IDENTITY.get(player);
        EntityType<?> type = Registry.ENTITY_TYPE.get(identity);

        if(current.getIdentity() != null && !current.getIdentity().getType().equals(type)) {
            source.sendMessage(new TranslatableText("identity.test_failed", player.getDisplayName(), new TranslatableText(type.getTranslationKey())), false);
            return 1;
        }

        source.sendMessage(new TranslatableText("identity.test_positive", player.getDisplayName(), new TranslatableText(type.getTranslationKey())), false);
        return 0;
    }

    private static void grant(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity) {
        UnlockedIdentitysComponent unlocked = Components.UNLOCKED_IDENTITIES.get(player);
        EntityType<?> entity = Registry.ENTITY_TYPE.get(identity);

        if(!unlocked.has(entity)) {
            unlocked.unlock(entity);
            player.sendMessage(new TranslatableText("identity.unlock_entity", new TranslatableText(entity.getTranslationKey())), true);
            source.sendMessage(new TranslatableText("identity.grant_success", new TranslatableText(entity.getTranslationKey()), player.getDisplayName()), false);
        } else {
            source.sendMessage(new TranslatableText("identity.already_has", player.getDisplayName(), new TranslatableText(entity.getTranslationKey())), false);
        }
    }

    private static void revoke(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity)  {
        UnlockedIdentitysComponent unlocked = Components.UNLOCKED_IDENTITIES.get(player);
        EntityType<?> entity = Registry.ENTITY_TYPE.get(identity);

        if(unlocked.has(entity)) {
            unlocked.revoke(entity);
            player.sendMessage(new TranslatableText("identity.revoke_entity", new TranslatableText(entity.getTranslationKey())), true);
            source.sendMessage(new TranslatableText("identity.revoke_success", new TranslatableText(entity.getTranslationKey()), player.getDisplayName()), false);
        } else {
            source.sendMessage(new TranslatableText("identity.does_not_have", player.getDisplayName(), new TranslatableText(entity.getTranslationKey())), false);
        }
    }

    private static void equip(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity)  {
        IdentityComponent current = Components.CURRENT_IDENTITY.get(player);
        EntityType<?> entity = Registry.ENTITY_TYPE.get(identity);

        Entity createdEntity = entity.create(player.world);

        if(createdEntity instanceof LivingEntity) {
            current.setIdentity((LivingEntity) createdEntity);
            source.sendMessage(new TranslatableText("identity.equip_success", new TranslatableText(entity.getTranslationKey()), player.getDisplayName()), false);
        }
    }

    private static void unequip(ServerPlayerEntity source, ServerPlayerEntity player) {
        IdentityComponent current = Components.CURRENT_IDENTITY.get(player);
        current.setIdentity(null);
        source.sendMessage(new TranslatableText("identity.unequip_success", player.getDisplayName()), false);
    }
}
