package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;

public class VillagerTypeProvider extends TypeProvider<VillagerEntity> {

    @Override
    public int getVariantData(VillagerEntity entity) {
        return Registries.VILLAGER_TYPE.getRawId(entity.getVariant());
    }

    @Override
    public VillagerEntity create(EntityType<VillagerEntity> type, World world, int data) {
        VillagerEntity villager = new VillagerEntity(type, world);
        villager.setVariant(Registries.VILLAGER_TYPE.get(data));
        return villager;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return Registries.VILLAGER_TYPE.size() - 1;
    }

    @Override
    public Text modifyText(VillagerEntity entity, MutableText text) {
        return Text.literal(formatTypePrefix(entity.getVariant().toString()) + " ").append(text);
    }
}
