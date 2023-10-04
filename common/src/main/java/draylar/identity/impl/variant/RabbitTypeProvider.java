package draylar.identity.impl.variant;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class RabbitTypeProvider extends TypeProvider<RabbitEntity> {

    private static final Map<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Brown")
            .put(1, "White")
            .put(2, "Black")
            .put(3, "White Splotched")
            .put(4, "Gold")
            .put(5, "Salt and Pepper")
            .build();

    @Override
    public int getVariantData(RabbitEntity entity) {
        int id = entity.getVariant().getId();
        if(id == 99) return 6;
        else return id;
    }

    @Override
    public RabbitEntity create(EntityType<RabbitEntity> type, World world, int data) {
        RabbitEntity rabbit = new RabbitEntity(type, world);
        if (data == 6) data = 99;
        rabbit.setVariant(RabbitEntity.RabbitType.byId(data));
        return rabbit;
    }

    @Override
    public int getFallbackData() {
        return RabbitEntity.RabbitType.BROWN.getId();
    }

    @Override
    public int getRange() {
        return RabbitEntity.RabbitType.values().length - 1;
    }

    @Override
    public Text modifyText(RabbitEntity entity, MutableText text) {
        RabbitEntity.RabbitType type = entity.getVariant();
        if(type.equals(RabbitEntity.RabbitType.EVIL)) return Text.translatable("entity.minecraft.killer_bunny");
        else return Text.literal(PREFIX_BY_ID.get(type.getId()) + " ").append(text);
    }
}
