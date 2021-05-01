package draylar.identity.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EvokerEntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class EntityArms {

    private static final Map<EntityType<? extends LivingEntity>, Pair<EntityArmProvider<? extends LivingEntity>, ArmRenderingManipulator<?>>> DIRECT_PROVIDERS = new HashMap<>();
    private static final Map<Class<?>, Pair<ClassArmProvider<?>, ArmRenderingManipulator<?>>> CLASS_PROVIDERS = new HashMap<>();

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
        register(QuadrupedEntityModel.class, (quad, model) -> model.frontRightLeg, (stack, model) -> {});
        register(LlamaEntityModel.class, (llama, model) -> model.rightFrontLeg, (stack, model) -> {});
        register(PandaEntityModel.class, (llama, model) -> model.frontRightLeg, (stack, model) -> stack.translate(0, -0.5, 0));
        register(BlazeEntityModel.class, (llama, model) -> model.rods[10], (stack, model) -> {
            stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(45));
            stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-15));
            stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-25));
            stack.translate(0, 0, -.25);
        });
        register(OcelotEntityModel.class, (ocelot, model) -> model.rightFrontLeg, (stack, model) -> {});
        register(SpiderEntityModel.class, (spider, model) -> model.rightFrontLeg, (stack, model) -> {
            stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-15));
            stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(15));
            stack.translate(0, 0, 0);
        });
        register(IronGolemEntityModel.class, (golem, model) -> model.getRightArm(), (stack, model) -> {
            stack.translate(0, 0, -.5);
        });
        register(PigEntityModel.class, (pig, model) -> model.frontRightLeg, (stack, model) -> {
            stack.translate(0, 0, .6);
        });
        register(EntityType.PILLAGER, (pillager, model) -> ((IllagerEntityModel) model).rightAttackingArm, (stack, model) -> {
            stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-10));
            stack.translate(0, .5, -.3);
        });
        register(PolarBearEntityModel.class, (bear, model) -> model.frontRightLeg, (stack, model) -> {
            stack.translate(0, 0, .3);
        });
        register(RavagerEntityModel.class, (bear, model) -> model.rightFrontLeg, (stack, model) -> { });
        register(SquidEntityModel.class, (squid, model) -> model.tentacles[0], (stack, model) -> {

        });
    }

    private EntityArms() {
        // NO-OP
    }
}
