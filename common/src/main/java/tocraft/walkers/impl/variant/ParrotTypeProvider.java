package tocraft.walkers.impl.variant;

import com.google.common.collect.ImmutableMap;
import tocraft.walkers.api.variant.TypeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ParrotTypeProvider extends TypeProvider<ParrotEntity> {

    private static final ImmutableMap<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Red Blue")
            .put(1, "Blue")
            .put(2, "Green")
            .put(3, "Yellow Blue")
            .put(4, "Gray")
            .build();

    @Override
    public int getVariantData(ParrotEntity entity) {
        return entity.getVariant();
    }

    @Override
    public ParrotEntity create(EntityType<ParrotEntity> type, World world, int data) {
        ParrotEntity parrot = new ParrotEntity(type, world);
        parrot.setVariant(data);
        return parrot;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 4;
    }

    @Override
    public Text modifyText(ParrotEntity parrot, MutableText text) {
        int variant = getVariantData(parrot);
        return Text.literal(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
