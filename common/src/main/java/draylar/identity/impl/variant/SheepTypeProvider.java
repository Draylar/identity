package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class SheepTypeProvider extends TypeProvider<SheepEntity> {

    @Override
    public int getVariantData(SheepEntity entity) {
        return entity.getColor().ordinal();
    }

    @Override
    public SheepEntity create(EntityType<SheepEntity> type, World world, int data) {
        SheepEntity sheep = new SheepEntity(type, world);
        sheep.setColor(DyeColor.byId(data));
        return sheep;
    }

    @Override
    public int getFallbackData() {
        return DyeColor.WHITE.getId();
    }

    @Override
    public int getRange() {
        return DyeColor.BLACK.getId();
    }

    @Override
    public Text modifyText(SheepEntity sheep, MutableText text) {
        return Text.literal(formatTypePrefix(DyeColor.byId(getVariantData(sheep)).getName()) + " ").append(text);
    }
}
