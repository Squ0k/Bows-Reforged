package com.dnii.bows_reforged;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ArcaneArrowEntity extends ArrowEntity {
    public ArcaneArrowEntity(EntityType<? extends ArcaneArrowEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        super.tick();

        if (this.getWorld().isClient) {
            for (int i=1;i<9;i++) {
                this.getWorld().addParticle(ParticleTypes.END_ROD,
                        (this.getX() * i + x * (8-i)) / 8,
                        (this.getY() * i + y * (8-i)) / 8,
                        (this.getZ() * i + z * (8-i)) / 8,
                        0.0, 0.0, 0.0);
            }
        }

        if (!this.getWorld().isClient && (this.inGround || this.age > 100)) {
            this.discard();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hitResult).getEntity();

            if (target instanceof LivingEntity living) {
                living.timeUntilRegen = 0;
                living.hurtTime = 0;
            }

            this.discard();
        }
    }
}
