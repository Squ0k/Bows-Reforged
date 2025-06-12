package com.dnii.bows_reforged;

import com.dnii.bows_reforged.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;

public class BowsReforged implements ModInitializer {
	public static final String MOD_ID = "bows-reforged";

	private static final HashMap<MobEntity, Integer> stunTimers = new HashMap<>();
	private static final HashMap<MobEntity, ParticleEffect> stunEffects = new HashMap<>();

	@Override
	public void onInitialize() {
		BrItems.initialize();
		BrEntities.initialize();
		BowComponents.initialize();
		ModScreenHandlers.register();

		ServerTickEvents.END_SERVER_TICK.register(this::stunTick);
	}

	private void stunTick(MinecraftServer minecraftServer) {
		HashMap<MobEntity, Integer> stunCopy = new HashMap<>(stunTimers);
		stunCopy.forEach((mob, timer) -> {
			if (!mob.isAlive()) {
				stunTimers.remove(mob);
				stunEffects.remove(mob);
			} else if (timer > 0) {
				stunTimers.put(mob, timer - 1);
				if (mob.getWorld() instanceof ServerWorld world) {
					world.spawnParticles(stunEffects.get(mob), mob.getX(), mob.getY() + mob.getHeight() / 2, mob.getZ(),
							2, 0.2, 0.3, 0.2, 0.5);
				}
			} else {
				mob.setAiDisabled(false);
				stunTimers.remove(mob);
				stunEffects.remove(mob);
			}
		});
	}

	public static void stunMob(MobEntity mob, int secs, ParticleEffect effect) {
		if (mob.getWorld() instanceof ServerWorld) {
			mob.setAiDisabled(true);
			stunTimers.put(mob, secs * 20); // 20 ticks = 1 second
			stunEffects.put(mob, effect);
		}
	}
}