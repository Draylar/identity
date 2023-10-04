package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ZombieVillagerTypeProvider extends TypeProvider<ZombieVillagerEntity> {

    @Override
    public int getVariantData(ZombieVillagerEntity entity) {
        return Registries.VILLAGER_TYPE.getRawId(entity.getVariant());
    }

    @Override
    public ZombieVillagerEntity create(EntityType<ZombieVillagerEntity> type, World world, int data) {
        ZombieVillagerEntity villager = new ZombieVillagerEntity(type, world);
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
    public Text modifyText(ZombieVillagerEntity entity, MutableText text) {
        return Text.literal(formatTypePrefix(entity.getVariant().toString()) + " ").append(text);
    }
}
