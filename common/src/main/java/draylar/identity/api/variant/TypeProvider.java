package draylar.identity.api.variant;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Locale;

public abstract class TypeProvider<T extends LivingEntity> {

    public IdentityType<T> create(EntityType<T> type, T entity) {
        return new IdentityType<T>((EntityType<T>) entity.getType(), getVariantData(entity));
    }

    public abstract int getVariantData(T entity);

    public abstract T create(EntityType<T> type, World world, int data);

    public abstract int getFallbackData();

    public abstract int getRange();

    public abstract Text modifyText(T entity, MutableText text);

    public final String formatTypePrefix(String prefix) {
        return String.valueOf(prefix.charAt(0)).toUpperCase(Locale.ROOT) + prefix.substring(1);
    }
}
