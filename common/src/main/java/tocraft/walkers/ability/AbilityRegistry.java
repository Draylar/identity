package tocraft.walkers.ability;

import tocraft.walkers.ability.impl.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {

    private static final Map<EntityType<? extends LivingEntity>, WalkersAbility<?>> abilities = new HashMap<>();

    private AbilityRegistry() {

    }

    public static void init() {
        register(EntityType.BLAZE, new BlazeAbility());
        register(EntityType.CREEPER, new CreeperAbility());
        register(EntityType.ENDER_DRAGON, new EnderDragonAbility());
        register(EntityType.ENDERMAN, new EndermanAbility());
        register(EntityType.GHAST, new GhastAbility());
        register(EntityType.SNOW_GOLEM, new SnowGolemAbility());
        register(EntityType.WITHER, new WitherEntityAbility());
        register(EntityType.COW, new CowAbility());
        register(EntityType.ENDERMITE, new EndermiteAbility());
        register(EntityType.LLAMA, new LlamaAbility<>());
        register(EntityType.TRADER_LLAMA, new LlamaAbility<>());
        register(EntityType.WITCH, new WitchAbility());
        register(EntityType.EVOKER, new EvokerAbility());
        register(EntityType.WARDEN, new WardenAbility());
    }

    public static WalkersAbility get(EntityType<?> type) {
        return abilities.get(type);
    }

    public static <A extends LivingEntity, T extends EntityType<A>> void register(T type, WalkersAbility<A> ability) {
        abilities.put(type, ability);
    }

    public static boolean has(EntityType<?> type) {
        return abilities.containsKey(type);
    }
}
