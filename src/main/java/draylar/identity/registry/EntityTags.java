package draylar.identity.registry;

import draylar.identity.Identity;
import draylar.identity.mixin.EntityTypeTagsAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class EntityTags {

    public static final Tag<EntityType<?>> BURNS_IN_DAYLIGHT = register("burns_in_daylight");
    public static final Tag<EntityType<?>> FLYING = register("flying");
    public static final Tag<EntityType<?>> SLOW_FALLING = register("slow_falling");
    public static final Tag<EntityType<?>> WOLF_PREY = register("wolf_prey");
    public static final Tag<EntityType<?>> FOX_PREY = register("fox_prey");
    public static final Tag<EntityType<?>> HURT_BY_HIGH_TEMPERATURE = register("hurt_by_high_temperature");

    private EntityTags() { }

    public static void init() {
        // NO-OP
    }

    private static Tag<EntityType<?>> register(String id) {
        return EntityTypeTagsAccessor.callRegister(Identity.id(id).toString());
    }
}
