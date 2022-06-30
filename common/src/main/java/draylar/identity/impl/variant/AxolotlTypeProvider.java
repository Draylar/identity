package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;
import draylar.identity.mixin.accessor.AxolotlEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AxolotlTypeProvider extends TypeProvider<AxolotlEntity> {

    @Override
    public int getVariantData(AxolotlEntity entity) {
        return entity.getVariant().getId();
    }

    @Override
    public AxolotlEntity create(EntityType<AxolotlEntity> type, World world, int data) {
        AxolotlEntity axolotl = new AxolotlEntity(type, world);
        ((AxolotlEntityAccessor) axolotl).callSetVariant(AxolotlEntity.Variant.values()[data]);
        return axolotl;
    }

    @Override
    public int getFallbackData() {
        return AxolotlEntity.Variant.LUCY.getId();
    }

    @Override
    public int getRange() {
        return AxolotlEntity.Variant.values().length - 1;
    }

    @Override
    public Text modifyText(AxolotlEntity entity, MutableText text) {
        return Text.literal(formatTypePrefix(AxolotlEntity.Variant.values()[getVariantData(entity)].getName()) + " ").append(text);
    }
}
