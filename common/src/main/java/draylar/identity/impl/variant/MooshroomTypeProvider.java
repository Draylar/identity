package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class MooshroomTypeProvider extends TypeProvider<MooshroomEntity> {

    @Override
    public int getVariantData(MooshroomEntity entity) {
        return entity.getVariant().ordinal();
    }

    @Override
    public MooshroomEntity create(EntityType<MooshroomEntity> type, World world, int data) {
        MooshroomEntity mooshroom = new MooshroomEntity(type, world);
        mooshroom.setVariant(MooshroomEntity.Type.values()[data]);
        return mooshroom;
    }

    @Override
    public int getFallbackData() {
        return MooshroomEntity.Type.RED.ordinal();
    }

    @Override
    public int getRange() {
        return MooshroomEntity.Type.values().length - 1;
    }

    @Override
    public Text modifyText(MooshroomEntity entity, MutableText text) {
        return Text.literal(formatTypePrefix(entity.getVariant().asString()) + " ").append(text);
    }
}
