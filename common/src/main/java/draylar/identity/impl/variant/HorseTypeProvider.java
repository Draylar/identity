package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class HorseTypeProvider extends TypeProvider<HorseEntity> {

    @Override
    public int getVariantData(HorseEntity entity) {
        return entity.getVariant().getId();
    }

    @Override
    public HorseEntity create(EntityType<HorseEntity> type, World world, int data) {
        HorseEntity horse = new HorseEntity(type, world);
        horse.setVariant(HorseColor.byId(data));
        return horse;
    }

    @Override
    public int getFallbackData() {
        return HorseColor.WHITE.getId();
    }

    @Override
    public int getRange() {
        return HorseColor.values().length - 1;
    }

    @Override
    public Text modifyText(HorseEntity entity, MutableText text) {
        return Text.literal(formatTypePrefix(entity.getVariant().asString()) + " ").append(text);
    }
}
