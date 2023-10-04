package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;
import draylar.identity.mixin.accessor.SlimeEntityAccessor;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class MagmaCubeTypeProvider extends TypeProvider<MagmaCubeEntity> {

    @Override
    public int getVariantData(MagmaCubeEntity entity) {
        return entity.getSize();
    }

    @Override
    public MagmaCubeEntity create(EntityType<MagmaCubeEntity> type, World world, int data) {
        MagmaCubeEntity magmaCube = new MagmaCubeEntity(type, world);
        ((SlimeEntityAccessor) magmaCube).callSetSize(data + 1, true);
        return magmaCube;
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
    public Text modifyText(MagmaCubeEntity entity, MutableText text) {
        return Text.literal(String.format("Size %d ", entity.getSize())).append(text);
    }
}
