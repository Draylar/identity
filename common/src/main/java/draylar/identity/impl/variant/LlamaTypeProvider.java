package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class LlamaTypeProvider extends TypeProvider<LlamaEntity> {

    @Override
    public int getVariantData(LlamaEntity entity) {
        return entity.getVariant().getIndex();
    }

    @Override
    public LlamaEntity create(EntityType<LlamaEntity> type, World world, int data) {
        LlamaEntity llama = new LlamaEntity(type, world);
        llama.setVariant(LlamaEntity.Variant.byId(data));
        return llama;
    }

    @Override
    public int getFallbackData() {
        return LlamaEntity.Variant.CREAMY.getIndex();
    }

    @Override
    public int getRange() {
        return LlamaEntity.Variant.values().length - 1;
    }

    @Override
    public Text modifyText(LlamaEntity entity, MutableText text) {
        return Text.literal(formatTypePrefix(entity.getVariant().asString()) + " ").append(text);
    }
}
