package draylar.identity.impl.variant;

import draylar.identity.api.variant.TypeProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class PandaTypeProvider extends TypeProvider<PandaEntity> {

    @Override
    public int getVariantData(PandaEntity entity) {
        return entity.getMainGene().getId();
    }

    @Override
    public PandaEntity create(EntityType<PandaEntity> type, World world, int data) {
        PandaEntity panda = new PandaEntity(type, world);
        panda.setMainGene(PandaEntity.Gene.byId(data));
        return panda;
    }

    @Override
    public int getFallbackData() {
        return PandaEntity.Gene.NORMAL.getId();
    }

    @Override
    public int getRange() {
        return PandaEntity.Gene.values().length - 1;
    }

    @Override
    public Text modifyText(PandaEntity entity, MutableText text) {
        PandaEntity.Gene gene = entity.getMainGene();
        if(gene.equals(PandaEntity.Gene.NORMAL)) return text;
        else return Text.literal(formatTypePrefix(gene.asString()) + " ").append(text);
    }
}
