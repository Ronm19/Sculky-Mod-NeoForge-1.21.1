package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SculkSpiritEntity extends Vex implements Enemy {
    private static final int FLAG_IS_CHARGING = 1;
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID =
            SynchedEntityData.defineId(SculkSpiritEntity.class, EntityDataSerializers.BYTE);

    @Nullable
    private BlockPos boundOrigin;

    // -1 = permanent. Useful later when the Sculk Evoker summons temporary spirits.
    private int limitedLifeTicks = -1;

    public SculkSpiritEntity(EntityType<? extends Vex> type, Level level) {
        super(type, level);
        this.xpReward = 10;
        this.moveControl = new SculkSpiritMoveControl(this);
        this.navigation = new FlyingPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25D, true) {
            @Override
            public void start() {
                super.start();
                setIsCharging(true);
            }

            @Override
            public void stop() {
                super.stop();
                setIsCharging(false);
            }
        });

        this.goalSelector.addGoal(5, new SculkSpiritRandomMoveGoal());
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.limitedLifeTicks > 0) {
                this.limitedLifeTicks--;

                if (this.limitedLifeTicks <= 0) {
                    this.discard();
                    return;
                }
            }

            if (this.level() instanceof ServerLevel serverLevel && this.tickCount % 8 == 0) {
                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        this.getX(),
                        this.getY() + 0.35D,
                        this.getZ(),
                        2,
                        0.15D,
                        0.18D,
                        0.15D,
                        0.01D
                );
            }
        }

        if (this.isCharging()) {
            Vec3 look = this.getLookAngle();
            this.setDeltaMovement(this.getDeltaMovement().add(
                    look.x * 0.018D,
                    look.y * 0.008D,
                    look.z * 0.018D
            ));
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit && target instanceof LivingEntity living) {
            if (this.random.nextFloat() < 0.25F) {
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), this);
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        living.getX(),
                        living.getY() + 0.5D,
                        living.getZ(),
                        6,
                        0.2D,
                        0.25D,
                        0.2D,
                        0.02D
                );
            }
        }

        return hit;
    }

    public static AttributeSupplier.Builder createSculkSpiritAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 22.0D);
    }

    public void setLimitedLife(int ticks) {
        this.limitedLifeTicks = ticks;
    }

    public boolean hasLimitedLife() {
        return this.limitedLifeTicks > 0;
    }

    public boolean isCharging() {
        return getSpiritFlag(FLAG_IS_CHARGING);
    }

    public void setIsCharging(boolean charging) {
        setSpiritFlag(FLAG_IS_CHARGING, charging);
    }

    private boolean getSpiritFlag(int mask) {
        int flags = this.entityData.get(DATA_FLAGS_ID);
        return (flags & mask) != 0;
    }

    private void setSpiritFlag(int mask, boolean value) {
        int flags = this.entityData.get(DATA_FLAGS_ID);

        if (value) {
            flags |= mask;
        } else {
            flags &= ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (flags & 255));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos boundOrigin) {
        this.boundOrigin = boundOrigin;
    }

    @Override
    protected @NotNull MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SENSOR_HIT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    class SculkSpiritMoveControl extends MoveControl {
        public SculkSpiritMoveControl(SculkSpiritEntity spirit) {
            super(spirit);
        }

        @Override
        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                Vec3 movement = new Vec3(
                        this.wantedX - SculkSpiritEntity.this.getX(),
                        this.wantedY - SculkSpiritEntity.this.getY(),
                        this.wantedZ - SculkSpiritEntity.this.getZ()
                );

                double distance = movement.length();

                if (distance < SculkSpiritEntity.this.getBoundingBox().getSize()) {
                    this.operation = Operation.WAIT;
                    SculkSpiritEntity.this.setDeltaMovement(SculkSpiritEntity.this.getDeltaMovement().scale(0.5D));
                } else {
                    SculkSpiritEntity.this.setDeltaMovement(
                            SculkSpiritEntity.this.getDeltaMovement().add(movement.scale(this.speedModifier * 0.05D / distance))
                    );

                    if (SculkSpiritEntity.this.getTarget() == null) {
                        Vec3 delta = SculkSpiritEntity.this.getDeltaMovement();
                        SculkSpiritEntity.this.setYRot(-((float) Mth.atan2(delta.x, delta.z)) * (180F / (float) Math.PI));
                        SculkSpiritEntity.this.yBodyRot = SculkSpiritEntity.this.getYRot();
                    } else {
                        double x = SculkSpiritEntity.this.getTarget().getX() - SculkSpiritEntity.this.getX();
                        double z = SculkSpiritEntity.this.getTarget().getZ() - SculkSpiritEntity.this.getZ();
                        SculkSpiritEntity.this.setYRot(-((float) Mth.atan2(x, z)) * (180F / (float) Math.PI));
                        SculkSpiritEntity.this.yBodyRot = SculkSpiritEntity.this.getYRot();
                    }
                }
            }
        }
    }

    class SculkSpiritRandomMoveGoal extends Goal {
        public SculkSpiritRandomMoveGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !SculkSpiritEntity.this.getMoveControl().hasWanted()
                    && SculkSpiritEntity.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos origin = SculkSpiritEntity.this.getBoundOrigin();

            if (origin == null) {
                origin = SculkSpiritEntity.this.blockPosition();
            }

            for (int i = 0; i < 3; ++i) {
                BlockPos targetPos = origin.offset(
                        SculkSpiritEntity.this.random.nextInt(15) - 7,
                        SculkSpiritEntity.this.random.nextInt(11) - 5,
                        SculkSpiritEntity.this.random.nextInt(15) - 7
                );

                if (SculkSpiritEntity.this.level().isEmptyBlock(targetPos)) {
                    SculkSpiritEntity.this.moveControl.setWantedPosition(
                            targetPos.getX() + 0.5D,
                            targetPos.getY() + 0.5D,
                            targetPos.getZ() + 0.5D,
                            0.25D
                    );

                    if (SculkSpiritEntity.this.getTarget() == null) {
                        SculkSpiritEntity.this.getLookControl().setLookAt(
                                targetPos.getX() + 0.5D,
                                targetPos.getY() + 0.5D,
                                targetPos.getZ() + 0.5D,
                                180.0F,
                                20.0F
                        );
                    }

                    break;
                }
            }
        }
    }
}