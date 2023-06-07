package draylar.identity.impl.variant;

import com.google.common.collect.ImmutableMap;
import draylar.identity.api.variant.TypeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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
        return Registries.CAT_VARIANT.getRawId(entity.getVariant());
    }

    @Override
    public CatEntity create(EntityType<CatEntity> type, World world, int data) {
        CatEntity cat = new CatEntity(type, world);
        cat.setVariant(Registries.CAT_VARIANT.get(data));
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
    public Text modifyText(CatEntity cat, MutableText text) {
        int variant = getVariantData(cat);
        return Text.literal(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
