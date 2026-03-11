package net.ronm19.sculky.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class SonicBoomEntity extends AbstractHurtingProjectile {

    public SonicBoomEntity(EntityType<? extends SonicBoomEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        Entity hit = result.getEntity();
        Entity owner = this.getOwner();

        if (!this.level().isClientSide && hit instanceof LivingEntity living) {
            DamageSource source = this.damageSources().mobProjectile(this, owner instanceof LivingEntity shooter ? shooter : null);
            living.hurt(source, 6.0F);

            double dx = hit.getX() - this.getX();
            double dz = hit.getZ() - this.getZ();
            double length = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));

            // knockback
            hit.push((dx / length) * 0.8D, 0.18D, (dz / length) * 0.8D);
        }

        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
}