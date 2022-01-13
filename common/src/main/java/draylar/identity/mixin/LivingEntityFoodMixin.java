package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Lazy;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFoodMixin extends Entity {

    @Unique
    private static final Lazy<List<FoodComponent>> WOLVES_IGNORE = new Lazy<>(() -> Arrays.asList(FoodComponents.CHICKEN, FoodComponents.PUFFERFISH, FoodComponents.ROTTEN_FLESH));

    public LivingEntityFoodMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "applyFoodEffects",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void removeFleshHungerForWolves(ItemStack stack, World world, LivingEntity targetEntity, CallbackInfo ci, Item item) {
        if((LivingEntity) (Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            // If this player is a Wolf and the item they are eating is an item wolves are immune to, cancel the method.
            if(identity instanceof WolfEntity) {
                if(WOLVES_IGNORE.get().contains(item.getFoodComponent())) {
                    ci.cancel();
                }
            }
        }
    }
}
