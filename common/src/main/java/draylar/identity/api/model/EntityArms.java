package draylar.identity.api.model;

import draylar.identity.mixin.accessor.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class EntityArms {

    private static final Map<EntityType<? extends LivingEntity>, Pair<EntityArmProvider<? extends LivingEntity>, ArmRenderingManipulator<?>>> DIRECT_PROVIDERS = new LinkedHashMap<>();
    private static final Map<Class<?>, Pair<ClassArmProvider<?>, ArmRenderingManipulator<?>>> CLASS_PROVIDERS = new LinkedHashMap<>();

    public static <T extends LivingEntity> void register(EntityType<T> type, EntityArmProvider<T> provider, ArmRenderingManipulator<EntityModel<T>> manipulator) {
        DIRECT_PROVIDERS.put(type, new Pair<>(provider, manipulator));
    }

    public static <T> void register(Class<T> modelClass, ClassArmProvider<T> provider, ArmRenderingManipulator<T> manipulator) {
        CLASS_PROVIDERS.put(modelClass, new Pair<>(provider, manipulator));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> Pair<ModelPart, ArmRenderingManipulator<?>> get(T entity, EntityModel<T> model) {
        // done to bypass type issues
        Pair<EntityArmProvider<? extends LivingEntity>, ArmRenderingManipulator<?>> before = DIRECT_PROVIDERS.get(entity.getType());

        // Direct entity type provider was found, return it now
        if(before != null) {
            Pair<EntityArmProvider<T>, ArmRenderingManipulator<?>> provider = new Pair<>((EntityArmProvider<T>) before.getLeft(), before.getRight());
            return new Pair<>(provider.getLeft().getArm(entity, model), provider.getRight());
        } else {
            Optional<Pair<ClassArmProvider<?>, ArmRenderingManipulator<?>>> beforeClassProvider = CLASS_PROVIDERS.entrySet().stream().filter(pair -> {
                return pair.getKey().isInstance(model);
            }).findFirst().map(entry -> new Pair<>(entry.getValue().getLeft(), entry.getValue().getRight()));

            // fall back to class providers
            if(beforeClassProvider.isPresent()) {
                Pair<ClassArmProvider<EntityModel>, ArmRenderingManipulator<EntityModel<LivingEntity>>> classProvider = new Pair<>((ClassArmProvider<EntityModel>) beforeClassProvider.get().getLeft(), (ArmRenderingManipulator<EntityModel<LivingEntity>>) beforeClassProvider.get().getRight());
                return new Pair<>(classProvider.getLeft().getArm(entity, model), classProvider.getRight());
            } else {
                return null;
            }
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> EntityArmProvider<T> get(EntityType<LivingEntity> type) {
        return (EntityArmProvider<T>) DIRECT_PROVIDERS.get(type);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> EntityArmProvider<T> get(Class<EntityModel<? extends LivingEntity>> modelClass) {
        return (EntityArmProvider<T>) CLASS_PROVIDERS.get(modelClass);
    }

    public static void init() {
        // specific
        register(LlamaEntityModel.class, (llama, model) -> ((LlamaEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> {});
        register(PandaEntityModel.class, (llama, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> stack.translate(0, -0.5, 0));
        register(BlazeEntityModel.class, (llama, model) -> ((BlazeEntityModelAccessor) model).getRods()[10], (stack, model) -> {
            stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-15));
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-25));
            stack.translate(0, 0, -.25);
        });
        register(OcelotEntityModel.class, (ocelot, model) -> ((OcelotEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> {});
        register(SpiderEntityModel.class, (spider, model) -> ((SpiderEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> {
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-15));
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15));
            stack.translate(0, 0, 0);
        });
        register(IronGolemEntityModel.class, (golem, model) -> model.getRightArm(), (stack, model) -> {
            stack.translate(0, 0, -.5);
        });
        register(PigEntityModel.class, (pig, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> {
            stack.translate(0, 0, .6);
        });
        register(PolarBearEntityModel.class, (bear, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> {
            stack.translate(0, 0, .3);
        });
        register(RavagerEntityModel.class, (bear, model) -> ((RavagerEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> { });
        register(SquidEntityModel.class, (squid, model) -> ((SquidEntityModelAccessor) model).getTentacles()[0], (stack, model) -> {

        });

        // generic
        register(QuadrupedEntityModel.class, (quad, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(), (stack, model) -> {});

        // types
        register(EntityType.PILLAGER, (pillager, model) -> ((IllagerEntityModelAccessor) model).getRightArm(), (stack, model) -> {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10));
            stack.translate(0, .5, -.3);
        });
    }

    private EntityArms() {
        // NO-OP
    }
}
