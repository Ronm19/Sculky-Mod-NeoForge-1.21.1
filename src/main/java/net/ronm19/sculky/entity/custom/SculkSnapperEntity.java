package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.item.ModItems;

public class SculkSnapperEntity extends WaterAnimal implements Enemy {

    public final AnimationState idleAnimationState = new AnimationState();

    public SculkSnapperEntity(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SnapperMoveControl(this);
        this.xpReward = 6;
    }

    public static AttributeSupplier.Builder createSculkSnapperAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.05D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.05D);
    }

    @Override
    protected void registerGoals() {
        // Do NOT use FloatGoal here. It makes aquatic mobs try to float upward.

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0D, 40));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Important: only attack players who are actually in water.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                true,
                player -> player.isInWaterOrBubble()
        ));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractFish.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Squid.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void dropCustomDeathLoot( ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        // Sculk Fin drop: common, but not guaranteed
        if (this.random.nextFloat() < 0.70F) {
            this.spawnAtLocation(new ItemStack(ModItems.SCULK_FIN.get()));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        // Since this extends WaterAnimal instead of Monster, this keeps it from existing in Peaceful.
        if (!this.level().isClientSide() && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.90D));
        } else {
            super.travel(travelVector);
        }
    }

    private void setupAnimationStates() {
        this.idleAnimationState.startIfStopped(this.tickCount);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hurt = super.doHurtTarget(entity);

        if (hurt) {
            this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 0.7F, 1.3F);
        }

        return hurt;
    }

    @Override
    protected int increaseAirSupply(int currentAir) {
        return this.getMaxAirSupply();
    }

    @Override
    protected int decreaseAirSupply(int currentAir) {
        return currentAir;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.COD_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.7F;
    }

    public static boolean canSpawn(
            EntityType<SculkSnapperEntity> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && level.getFluidState(pos).is(FluidTags.WATER)
                && level.getFluidState(pos.above()).is(FluidTags.WATER);
    }

    private static class SnapperMoveControl extends MoveControl {
        private final SculkSnapperEntity snapper;

        public SnapperMoveControl(SculkSnapperEntity snapper) {
            super(snapper);
            this.snapper = snapper;
        }

        @Override
        public void tick() {
            // Fish-style buoyancy, but very small.
            if (this.snapper.isEyeInFluid(FluidTags.WATER)) {
                this.snapper.setDeltaMovement(
                        this.snapper.getDeltaMovement().add(0.0D, 0.003D, 0.0D)
                );
            } else if (this.snapper.isInWater()) {
                // If body is in water but eye is above water, pull it back down.
                this.snapper.setDeltaMovement(
                        this.snapper.getDeltaMovement().add(0.0D, -0.04D, 0.0D)
                );
            }

            if (this.operation == Operation.MOVE_TO && !this.snapper.getNavigation().isDone()) {
                double xDistance = this.wantedX - this.snapper.getX();
                double yDistance = this.wantedY - this.snapper.getY();
                double zDistance = this.wantedZ - this.snapper.getZ();

                double distance = Math.sqrt(
                        xDistance * xDistance +
                                yDistance * yDistance +
                                zDistance * zDistance
                );

                if (distance < 0.0001D) {
                    this.snapper.setSpeed(0.0F);
                    return;
                }

                float targetSpeed = (float) (this.speedModifier * this.snapper.getAttributeValue(Attributes.MOVEMENT_SPEED));
                this.snapper.setSpeed(Mth.lerp(0.125F, this.snapper.getSpeed(), targetSpeed));

                // Only follow vertical movement if the target point is still underwater-ish.
                if (yDistance != 0.0D && this.snapper.isInWater()) {
                    double yMotion = this.snapper.getSpeed() * (yDistance / distance) * 0.08D;

                    // Prevent strong upward launching.
                    yMotion = Mth.clamp(yMotion, -0.06D, 0.035D);

                    this.snapper.setDeltaMovement(
                            this.snapper.getDeltaMovement().add(0.0D, yMotion, 0.0D)
                    );
                }

                if (xDistance != 0.0D || zDistance != 0.0D) {
                    float targetYRot = (float) (Mth.atan2(zDistance, xDistance) * 180.0F / (float) Math.PI) - 90.0F;
                    this.snapper.setYRot(this.rotlerp(this.snapper.getYRot(), targetYRot, 20.0F));
                    this.snapper.yBodyRot = this.snapper.getYRot();
                }
            } else {
                this.snapper.setSpeed(0.0F);
            }
        }
    }
}