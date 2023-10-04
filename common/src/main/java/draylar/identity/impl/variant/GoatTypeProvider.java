package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class GoatTypeProvider extends TypeProvider<GoatEntity> {

    @Override
    public int getVariantData(GoatEntity entity) {
        return entity.isScreaming() ? 1 : 0;
    }

    @Override
    public GoatEntity create(EntityType<GoatEntity> type, World world, int data) {
        GoatEntity goat = new GoatEntity(type, world);
        goat.setScreaming(data > 0);
        return goat;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 1;
    }

    @Override
    public Text modifyText(GoatEntity entity, MutableText text) {
        if(entity.isScreaming()) return Text.literal("Screaming ").append(text);
        else return text;
    }
}
