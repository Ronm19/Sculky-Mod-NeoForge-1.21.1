package net.ronm19.sculky.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.util.ModTags;

import java.util.List;

public class SculkBombProjectileEntity extends ThrowableItemProjectile {

    private static final int CLOUD_DURATION_TICKS = 20 * 30;
    private static final int DARKNESS_DURATION_TICKS = 20 * 10;
    private static final int DAMAGE_INTERVAL_TICKS = 10;
    private static final int PARTICLE_INTERVAL_TICKS = 2;
    private static final int AMBIENT_SOUND_INTERVAL_TICKS = 40;

    private static final double CLOUD_RADIUS = 4.0D;
    private static final float IMPACT_DAMAGE = 4.0F;
    private static final float CLOUD_DAMAGE = 2.0F;

    private boolean cloudActive = false;
    private int cloudAge = 0;

    public SculkBombProjectileEntity(EntityType<? extends SculkBombProjectileEntity> type, Level level) {
        super(type, level);
    }

    public SculkBombProjectileEntity(Level level, LivingEntity shooter) {
        this(ModEntities.SCULK_BOMB_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
    }

    public SculkBombProjectileEntity(Level level, double x, double y, double z) {
        this(ModEntities.SCULK_BOMB_PROJECTILE.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SCULK_BOMB.get();
    }

    @Override
    public void tick() {
        if (!this.cloudActive) {
            super.tick();
            return;
        }

        super.baseTick();
        this.setDeltaMovement(Vec3.ZERO);

        if (!this.level().isClientSide) {
            if (this.cloudAge % PARTICLE_INTERVAL_TICKS == 0 && this.level() instanceof ServerLevel serverLevel) {
                spawnCloudParticles(serverLevel);
            }

            if (this.cloudAge % DAMAGE_INTERVAL_TICKS == 0) {
                applyCloudEffects();
            }

            if (this.cloudAge % AMBIENT_SOUND_INTERVAL_TICKS == 0) {
                this.level().playSound(null, this.blockPosition(),
                        SoundEvents.SCULK_SHRIEKER_SHRIEK,
                        SoundSource.HOSTILE,
                        0.9F,
                        0.9F + this.random.nextFloat() * 0.2F);
            }
        }

        this.cloudAge++;
        if (this.cloudAge >= CLOUD_DURATION_TICKS) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.cloudActive
                && !this.level().isClientSide
                && result.getEntity() instanceof LivingEntity living
                && !isImmuneTarget(living)) {

            living.hurt(this.damageSources().generic(), IMPACT_DAMAGE);
        }

        super.onHitEntity(result);
    }

    @Override
    protected void onHit(HitResult result) {
        if (this.cloudActive) {
            return;
        }

        super.onHit(result);

        if (!this.level().isClientSide) {
            Vec3 hit = result.getLocation();
            activateCloud(hit.x, hit.y + 0.05D, hit.z);
        }
    }

    private void activateCloud(double x, double y, double z) {
        this.cloudActive = true;
        this.cloudAge = 0;

        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setInvisible(true);

        if (this.level() instanceof ServerLevel serverLevel) {
            spawnCloudParticles(serverLevel);
        }
    }

    private void spawnCloudParticles(ServerLevel level) {
        for (int i = 0; i < 18; i++) {
            double angle = this.random.nextDouble() * (Math.PI * 2.0D);
            double radius = this.random.nextDouble() * CLOUD_RADIUS;

            double px = this.getX() + Math.cos(angle) * radius;
            double pz = this.getZ() + Math.sin(angle) * radius;
            double py = this.getY() + 0.1D + this.random.nextDouble() * 1.3D;

            level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, px, py, pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            level.sendParticles(ParticleTypes.SMOKE, px, py, pz, 1, 0.03D, 0.02D, 0.03D, 0.0D);
        }
    }

    private void applyCloudEffects() {
        AABB area = new AABB(
                this.getX() - CLOUD_RADIUS, this.getY() - 1.0D, this.getZ() - CLOUD_RADIUS,
                this.getX() + CLOUD_RADIUS, this.getY() + 2.0D, this.getZ() + CLOUD_RADIUS
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, living ->
                living != null
                        && living.isAlive()
                        && !living.isSpectator()
                        && !isImmuneTarget(living)
        );

        for (LivingEntity living : targets) {
            if (living.distanceToSqr(this.getX(), this.getY(), this.getZ()) > CLOUD_RADIUS * CLOUD_RADIUS) {
                continue;
            }

            living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, DARKNESS_DURATION_TICKS, 0));
            living.hurt(this.damageSources().generic(), CLOUD_DAMAGE);
        }
    }

    private boolean isImmuneTarget(LivingEntity living) {
        return living.getType().is(ModTags.Entities.SCULK_MOBS);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("CloudActive", this.cloudActive);
        tag.putInt("CloudAge", this.cloudAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.cloudActive = tag.getBoolean("CloudActive");
        this.cloudAge = tag.getInt("CloudAge");

        if (this.cloudActive) {
            this.setNoGravity(true);
            this.noPhysics = true;
            this.setInvisible(true);
        }
    }
}