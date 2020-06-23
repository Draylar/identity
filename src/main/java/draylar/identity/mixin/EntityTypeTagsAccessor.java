package draylar.identity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityTypeTags.class)
public interface EntityTypeTagsAccessor {
    @Invoker
    static Tag.Identified<EntityType<?>> callRegister(String id) {
        throw new UnsupportedOperationException();
    }
}
