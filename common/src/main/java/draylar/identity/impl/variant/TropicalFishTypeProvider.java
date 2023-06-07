package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

// TODO: do we want to add this? There will be a boat-load of fish...
public class TropicalFishTypeProvider extends TypeProvider<TropicalFishEntity> {

    @Override
    public int getVariantData(TropicalFishEntity entity) {
        return entity.getVariant().getId();
    }

    @Override
    public TropicalFishEntity create(EntityType<TropicalFishEntity> type, World world, int data) {
        TropicalFishEntity fish = new TropicalFishEntity(type, world);
        fish.setVariant(TropicalFishEntity.Variety.fromId(data));
        return fish;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 0;
    }

    @Override
    public Text modifyText(TropicalFishEntity entity, MutableText text) {
        return null;
    }
}
