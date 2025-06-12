package com.dnii.bows_reforged;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ElectricArrowEntity extends ArrowEntity {
    public ElectricArrowEntity(EntityType<? extends ElectricArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) hitResult).getEntity();

                if (target instanceof MobEntity mob) {
                    if (mob.getWorld() instanceof ServerWorld) {
                        BowsReforged.stunMob(mob, 10, ParticleTypes.ELECTRIC_SPARK);
                    }
                }

                this.discard();
            }
        }
    }
}
