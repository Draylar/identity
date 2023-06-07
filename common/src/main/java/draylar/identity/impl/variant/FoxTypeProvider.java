package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class FoxTypeProvider extends TypeProvider<FoxEntity> {

    @Override
    public int getVariantData(FoxEntity entity) {
        return entity.getVariant().getId();
    }

    @Override
    public FoxEntity create(EntityType<FoxEntity> type, World world, int data) {
        FoxEntity fox = new FoxEntity(type, world);
        fox.setVariant(FoxEntity.Type.fromId(data));
        return fox;
    }

    @Override
    public int getFallbackData() {
        return FoxEntity.Type.RED.getId();
    }

    @Override
    public int getRange() {
        return FoxEntity.Type.values().length - 1;
    }

    @Override
    public Text modifyText(FoxEntity entity, MutableText text) {
        return Text.literal(formatTypePrefix(FoxEntity.Type.fromId(getVariantData(entity)).asString()) + " ").append(text);
    }
}
