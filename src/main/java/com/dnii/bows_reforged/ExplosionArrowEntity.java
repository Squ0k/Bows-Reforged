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

public class ExplosionArrowEntity extends ArrowEntity {
    public ExplosionArrowEntity(EntityType<? extends ExplosionArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2.0F, false,
                        World.ExplosionSourceType.MOB);

                this.discard();
            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) hitResult).getEntity();

                this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 0.5F, false,
                        World.ExplosionSourceType.MOB);

                if (target instanceof MobEntity mob) {
                    if (mob.getWorld() instanceof ServerWorld) {
                        mob.setVelocity(this.getVelocity().x, 1.0, this.getVelocity().z);
                        BowsReforged.stunMob(mob, 0, ParticleTypes.CAMPFIRE_COSY_SMOKE); // force reset stun
                    }
                }

                this.discard();
            }
        }
    }
}
