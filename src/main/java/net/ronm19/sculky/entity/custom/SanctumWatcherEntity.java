package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.api.interfaces.CheckAndPerform;
import org.jetbrains.annotations.NotNull;

public class SanctumWatcherEntity extends Monster {

    private static final EntityDataAccessor<Boolean> ALERTED =
            SynchedEntityData.defineId(SanctumWatcherEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(SanctumWatcherEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;
    private int attackTicks = 0;

    private static final double STALK_RANGE = 20.0D;
    private static final double AGGRO_RANGE = 6.0D;
    private static final double GIVE_UP_RANGE = 28.0D;
    private static final float STARE_DOT_THRESHOLD = 0.965F;

    private int aggressionTicks = 0;

    public SanctumWatcherEntity( EntityType<? extends Monster> entityType, Level level ) {
        super(entityType, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createSanctumWatcherAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.20D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SanctumWatcherMeleeGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.45D, 80));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData( SynchedEntityData.Builder builder ) {
        super.defineSynchedData(builder);
        builder.define(ALERTED, false);
        builder.define(ATTACKING, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            if (this.attackTicks > 0) {
                --this.attackTicks;

                if (this.attackTicks <= 0)
                    this.setAttacking(false);
            }
        }

        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && this.attackAnimationTimeout <= 0) {
            this.attackAnimationTimeout = 25; // matches 0.8 second animation
            this.attackAnimationState.start(this.tickCount);
        } else if (this.attackAnimationTimeout > 0) {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            this.attackAnimationState.stop();
        }
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking( boolean attacking ) {
        this.entityData.set(ATTACKING, attacking);
    }

    public void startAttackAnimation() {
        this.attackTicks = 12; // same as client animation timeout
        this.setAttacking(true);
    }


    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            if (this.random.nextFloat() < 0.06F) {
                this.level().addParticle(
                        ParticleTypes.SCULK_CHARGE_POP,
                        this.getX(),
                        this.getY(0.7D),
                        this.getZ(),
                        0.0D, 0.01D, 0.0D
                );
            }
            return;
        }

        this.tickWatcherLogic();
    }

    private void tickWatcherLogic() {
        Player nearestPlayer = this.level().getNearestPlayer(this, STALK_RANGE);

        if (this.isAlerted()) {
            if (nearestPlayer != null && (this.getTarget() == null || !this.getTarget().isAlive())) {
                this.setTarget(nearestPlayer);
            }

            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            if (this.aggressionTicks > 0) {
                this.aggressionTicks--;
            }

            if (target == null
                    || !target.isAlive()
                    || this.distanceToSqr(target) > GIVE_UP_RANGE * GIVE_UP_RANGE
                    || this.aggressionTicks <= 0) {
                this.calmDown();
            }

            return;
        }

        if (nearestPlayer == null || !nearestPlayer.isAlive()) {
            return;
        }

        this.getLookControl().setLookAt(nearestPlayer, 20.0F, 20.0F);

        double distanceSqr = this.distanceToSqr(nearestPlayer);

        if (distanceSqr <= AGGRO_RANGE * AGGRO_RANGE) {
            this.becomeAlerted(nearestPlayer, 200);
            return;
        }

        if (this.isPlayerLookingAtMe(nearestPlayer)) {
            this.getNavigation().stop();
        } else {
            this.getNavigation().moveTo(nearestPlayer, 0.80D);
        }
    }

    private boolean isPlayerLookingAtMe( Player player ) {
        Vec3 playerView = player.getViewVector(1.0F).normalize();
        Vec3 toWatcher = this.getEyePosition().subtract(player.getEyePosition()).normalize();
        double dot = playerView.dot(toWatcher);

        return dot > STARE_DOT_THRESHOLD && player.hasLineOfSight(this);
    }

    private void becomeAlerted( LivingEntity target, int ticks ) {
        this.setAlerted(true);
        this.aggressionTicks = ticks;
        this.setTarget(target);
    }

    private void calmDown() {
        this.setAlerted(false);
        this.aggressionTicks = 0;
        this.setTarget(null);
        this.getNavigation().stop();
    }

    public void angerFromThrone( Player player ) {
        this.becomeAlerted(player, 300);
    }

    public boolean isAlerted() {
        return this.entityData.get(ALERTED);
    }

    public void setAlerted( boolean alerted ) {
        this.entityData.set(ALERTED, alerted);
    }

    @Override
    public boolean hurt( DamageSource source, float amount ) {
        boolean hurt = super.hurt(source, amount);

        if (hurt && !this.level().isClientSide && source.getEntity() instanceof LivingEntity livingEntity) {
            this.becomeAlerted(livingEntity, 240);
        }

        return hurt;
    }

    @Override
    public boolean doHurtTarget( net.minecraft.world.entity.Entity target ) {
        this.aggressionTicks = 200;
        return super.doHurtTarget(target);
    }

    @Override
    public void addAdditionalSaveData( CompoundTag tag ) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Alerted", this.isAlerted());
        tag.putInt("AggressionTicks", this.aggressionTicks);
    }

    @Override
    public void readAdditionalSaveData( CompoundTag tag ) {
        super.readAdditionalSaveData(tag);
        this.setAlerted(tag.getBoolean("Alerted"));
        this.aggressionTicks = tag.getInt("AggressionTicks");
    }

    public static boolean canSpawn( EntityType<SanctumWatcherEntity> entityType,
                                    LevelAccessor level,
                                    MobSpawnType spawnType,
                                    net.minecraft.core.BlockPos pos,
                                    net.minecraft.util.RandomSource random ) {
        return Monster.checkMonsterSpawnRules(entityType, (ServerLevelAccessor) level, spawnType, pos, random);
    }

    private static class SanctumWatcherMeleeGoal extends MeleeAttackGoal implements CheckAndPerform {
        private final SanctumWatcherEntity entity;
        private final int attackDelay = 12;
        private int ticksUntilNextAttack = 25;
        private boolean shouldCountTillNextAttack = false;

        public SanctumWatcherMeleeGoal( PathfinderMob pMob, double speedModifier, boolean followingTargetEvenIfNotSeen ) {
            super(pMob, speedModifier, followingTargetEvenIfNotSeen);
            entity = ((SanctumWatcherEntity) pMob);
        }

        @Override
        public void checkAndPerformAttack( LivingEntity enemy, double distSqr ) {

            if (isEnemyWithinAttackDistance(enemy, distSqr)) {

                if (this.ticksUntilNextAttack <= 0) {

                    // START ANIMATION
                    entity.setAttacking(true);

                    // Deal damage immediately (or delay if you want wind-up)
                    performAttack(enemy);

                    // Cooldown
                    this.ticksUntilNextAttack = attackDelay;
                }

            } else {
                entity.setAttacking(false);
                this.ticksUntilNextAttack = attackDelay;
            }
        }

        private boolean isEnemyWithinAttackDistance( LivingEntity pEnemy, double pDistToEnemySqr ) {
            return pDistToEnemySqr <= this.getAttackReachSqr(pEnemy);
        }

        private double getAttackReachSqr( LivingEntity pEnemy ) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + pEnemy.getBbWidth();
        }

        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.adjustedTickDelay(attackDelay * 2);
        }

        protected boolean isTimeToAttack() {
            return this.ticksUntilNextAttack <= 0;
        }

        protected boolean isTimeToStartAttackAnimation() {
            return this.ticksUntilNextAttack <= attackDelay;
        }

        protected int getTicksUntilNextAttack() {
            return this.ticksUntilNextAttack;
        }


        protected void performAttack( LivingEntity pEnemy ) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(pEnemy);
        }

        @Override
        public void tick() {
            super.tick();

            if (this.ticksUntilNextAttack > 0) {
                this.ticksUntilNextAttack--;
            }

            // Stop attack animation shortly after hit
            if (this.ticksUntilNextAttack < attackDelay - 5) {
                entity.setAttacking(false);
            }
        }

        @Override
        public void stop() {
            entity.setAttacking(false);
            super.stop();
        }

        public boolean isShouldCountTillNextAttack() {
            return shouldCountTillNextAttack;
        }

        public void setShouldCountTillNextAttack( boolean shouldCountTillNextAttack ) {
            this.shouldCountTillNextAttack = shouldCountTillNextAttack;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_CLICKING;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_CATALYST_BLOOM;
    }
}