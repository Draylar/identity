package draylar.identity.impl.variant;

import com.google.common.collect.ImmutableMap;
import draylar.identity.api.variant.TypeProvider;
import draylar.identity.mixin.accessor.AxolotlEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

import java.util.Map;

public class CatTypeProvider extends TypeProvider<CatEntity> {

    private static final Map<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Tabby")
            .put(1, "Black")
            .put(2, "Red")
            .put(3, "Siamese")
            .put(4, "British Shorthair")
            .put(5, "Calico")
            .put(6, "Persian")
            .put(7, "Ragdoll")
            .put(8, "White")
            .put(9, "Jellie")
            .put(10, "Black")
            .build();

    @Override
    public int getVariantData(CatEntity entity) {
        return entity.getCatType();
    }

    @Override
    public CatEntity create(EntityType<CatEntity> type, World world, int data) {
        CatEntity cat = new CatEntity(type, world);
        cat.setCatType(data);
        return cat;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 10;
    }

    @Override
    public Text modifyText(CatEntity cat, TranslatableText text) {
        int variant = getVariantData(cat);
        return new LiteralText(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
