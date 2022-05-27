package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;
import draylar.identity.mixin.accessor.SlimeEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

public class SlimeTypeProvider extends TypeProvider<SlimeEntity> {

    @Override
    public int getVariantData(SlimeEntity entity) {
        return entity.getSize();
    }

    @Override
    public SlimeEntity create(EntityType<SlimeEntity> type, World world, int data) {
        SlimeEntity slime = new SlimeEntity(type, world);
        ((SlimeEntityAccessor) slime).callSetSize(data + 1, true);
        return slime;
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
    public Text modifyText(SlimeEntity entity, TranslatableText text) {
        return new LiteralText(String.format("Size %d ", entity.getSize())).append(text);
    }
}
