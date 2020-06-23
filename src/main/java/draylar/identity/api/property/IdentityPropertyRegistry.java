package draylar.identity.api.property;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class IdentityPropertyRegistry {

    private static final Map<EntityType<LivingEntity>, IdentityProperties> PROPERTIES = new HashMap<>();
    private static final Map<EntityVerifier, IdentityProperties> FALLBACKS = new HashMap<>();

    private IdentityPropertyRegistry() {

    }

    public static void register(EntityType<LivingEntity> type, IdentityProperties properties) {
        if(PROPERTIES.containsKey(type)) {
            throw new UnsupportedOperationException(String.format("MorphPropertyRegistry already contains data for %s!", type.getTranslationKey()));
        } else {
            PROPERTIES.put(type, properties);
        }
    }

    public static IdentityProperties get(EntityType<LivingEntity> type) {
        return PROPERTIES.getOrDefault(type, null);
    }

    public interface EntityVerifier {
        boolean isValid(LivingEntity entity);
    }
}
