package tocraft.walkers.forge;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;

@Mod.EventBusSubscriber(modid = "identity")
public class WalkersForgeEventHandler {

	@SubscribeEvent
	public static void livingBreath(LivingBreatheEvent event) {
		if (event.getEntity() instanceof PlayerEntity) {
			LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) event.getEntity());

			if (identity != null) {
				if (Identity.isAquatic(identity)) {
					event.setCanBreathe(false);

				}
			}
		}
	}
}
