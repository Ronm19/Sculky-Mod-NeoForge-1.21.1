package net.ronm19.sculky.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.custom.SculkSkeletonEntity;

public class ShadowBoltEntity extends Projectile {

    private static final int LIFETIME_TICKS = 60;
    private static final double DRAG = 0.99D;

    public ShadowBoltEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = this.getDeltaMovement();

        // Check what we would hit along this tick's movement
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
            if (!this.isAlive()) {
                return;
            }
        }

        // Move
        this.setPos(
                this.getX() + motion.x,
                this.getY() + motion.y,
                this.getZ() + motion.z
        );

        // Face travel direction
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);

        // Particles
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SCULK_SOUL, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            this.level().addParticle(ParticleTypes.SCULK_SOUL, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }

        // Slight drag so it does not fly forever
        this.setDeltaMovement(motion.scale(DRAG));

        // Safety despawn
        if (this.tickCount > LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = this.getOwner();

        // Don't hit yourself
        if (target == owner) return false;

        // Don't hit same-type minions (your sculk skeletons)
        if (target instanceof SculkSkeletonEntity) return false;

        return super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (result.getEntity() instanceof LivingEntity target) {
            Entity owner = this.getOwner();
            LivingEntity livingOwner = owner instanceof LivingEntity living ? living : null;

            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));

            target.hurt(
                    this.damageSources().mobProjectile(this, livingOwner),
                    4.0F
            );
        }

        this.discard();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (result.getType() == HitResult.Type.BLOCK) {
            this.discard();
        }
    }
}