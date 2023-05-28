package tocraft.walkers.api.variant;

import tocraft.walkers.impl.variant.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShapeType<T extends LivingEntity> {

    private static final List<EntityType<? extends LivingEntity>> LIVING_TYPE_CASH = new ArrayList<>();
    private static final Map<EntityType<? extends LivingEntity>, TypeProvider<?>> VARIANT_BY_TYPE = new LinkedHashMap<>();
    private final EntityType<T> type;
    private final int variantData;

    static {
        VARIANT_BY_TYPE.put(EntityType.SHEEP, new SheepTypeProvider());
        VARIANT_BY_TYPE.put(EntityType.AXOLOTL, new AxolotlTypeProvider());
        VARIANT_BY_TYPE.put(EntityType.PARROT, new ParrotTypeProvider());
        VARIANT_BY_TYPE.put(EntityType.FOX, new FoxTypeProvider());
        VARIANT_BY_TYPE.put(EntityType.CAT, new CatTypeProvider());
        VARIANT_BY_TYPE.put(EntityType.SLIME, new SlimeTypeProvider());
        VARIANT_BY_TYPE.put(EntityType.FROG, new FrogTypeProvider());
    }

    public ShapeType(EntityType<T> type) {
        this.type = type;
        variantData = getDefaultVariantData(type);
    }

    private int getDefaultVariantData(EntityType<T> type) {
        if(VARIANT_BY_TYPE.containsKey(type)) {
            return VARIANT_BY_TYPE.get(type).getFallbackData();
        } else {
            return -1;
        }
    }

    public ShapeType(EntityType<T> type, int variantData) {
        this.type = type;
        this.variantData = variantData;
    }

    public ShapeType(T entity) {
        this.type = (EntityType<T>) entity.getType();

        // Discover variant data based on entity NBT data.
        @Nullable TypeProvider<T> provider = (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
        if(provider != null) {
            variantData = provider.getVariantData(entity);
        } else {
            variantData = getDefaultVariantData(type);
        }
    }

    @Nullable
    public static <Z extends LivingEntity> ShapeType<Z> from(Z entity) {
        if(entity == null) {
            return null;
        }

        EntityType<Z> type = (EntityType<Z>) entity.getType();
        if(VARIANT_BY_TYPE.containsKey(type)) {
            TypeProvider<Z> typeProvider = (TypeProvider<Z>) VARIANT_BY_TYPE.get(type);
            return typeProvider.create(type, entity);
        }

        return new ShapeType<>((EntityType<Z>) entity.getType());
    }

    @Nullable
    public static ShapeType<?> from(NbtCompound compound) {
        Identifier id = new Identifier(compound.getString("EntityID"));
        if(!Registries.ENTITY_TYPE.containsId(id)) {
            return null;
        }

        return new ShapeType(Registries.ENTITY_TYPE.get(id), compound.contains("Variant") ? compound.getInt("Variant") : -1);
    }

    public static List<ShapeType<?>> getAllTypes(World world) {
        if(LIVING_TYPE_CASH.isEmpty()) {
            for (EntityType<?> type : Registries.ENTITY_TYPE) {
                Entity instance = type.create(world);
                if(instance instanceof LivingEntity) {
                    LIVING_TYPE_CASH.add((EntityType<? extends LivingEntity>) type);
                }
            }
        }

        List<ShapeType<?>> types = new ArrayList<>();
        for (EntityType<?> type : LIVING_TYPE_CASH) {
            if(VARIANT_BY_TYPE.containsKey(type)) {
                TypeProvider<?> variant = VARIANT_BY_TYPE.get(type);
                for (int i = 0; i <= variant.getRange(); i++) {
                    types.add(new ShapeType(type, i));
                }
            } else {
                types.add(new ShapeType(type));
            }
        }

        return types;
    }

    @Nullable
    public static <Z extends LivingEntity> ShapeType<Z> from(EntityType<?> entityType, int variant) {
        if(VARIANT_BY_TYPE.containsKey(entityType)) {
            TypeProvider<?> provider = VARIANT_BY_TYPE.get(entityType);
            if(variant < -1 || variant > provider.getRange()) {
                return null;
            }
        }

        return new ShapeType<>((EntityType<Z>) entityType, variant);
    }

    public NbtCompound writeCompound() {
        NbtCompound compound = new NbtCompound();
        compound.putString("EntityID", Registries.ENTITY_TYPE.getId(type).toString());
        compound.putInt("Variant", variantData);
        return compound;
    }

    public EntityType<? extends LivingEntity> getEntityType() {
        return type;
    }

    public T create(World world) {
        TypeProvider<T> typeProvider = (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
        if(typeProvider != null) {
            return typeProvider.create(type, world, variantData);
        }

        return type.create(world);
    }

    public int getVariantData() {
        return variantData;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ShapeType<?> that = (ShapeType<?>) o;
        return variantData == that.variantData && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, variantData);
    }

    public void writeEntityNbt(NbtCompound tag) {
        NbtCompound inner = writeCompound();
        tag.put("ShapeType", inner);
    }

    public static ShapeType<?> fromEntityNbt(NbtCompound tag) {
        return from(tag.getCompound("ShapeType"));
    }

    public Text createTooltipText(T entity) {
        TypeProvider<T> provider = (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
        if(provider != null) {
            return provider.modifyText(entity, Text.translatable(type.getTranslationKey()));
        }

        return Text.translatable(type.getTranslationKey());
    }
}
