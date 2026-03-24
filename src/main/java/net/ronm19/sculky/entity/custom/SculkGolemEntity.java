package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class SculkGolemEntity extends AbstractGolem {

    // --------------------
    // Synced Data
    // --------------------
    private static final EntityDataAccessor<Boolean> BERSERK =
            SynchedEntityData.defineId(SculkGolemEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ROARING =
            SynchedEntityData.defineId(SculkGolemEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> SLAMMING =
            SynchedEntityData.defineId(SculkGolemEntity.class, EntityDataSerializers.BOOLEAN);

    // --------------------
    // Animation / State
    // --------------------
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState slamAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;

    private int attackAnimationTick;
    private int roarAnimationTick;
    private int slamAnimationTick;

    private int roarCooldown;
    private int slamCooldown;
    private int rushCooldown;

    private boolean berserkTriggered;

    public SculkGolemEntity(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 22;
    }

    // --------------------
    // Synced Data Setup
    // --------------------
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BERSERK, false);
        builder.define(ROARING, false);
        builder.define(SLAMMING, false);
    }

    // --------------------
    // Goals / AI
    // --------------------
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new SculkGolemRoarGoal(this));
        this.goalSelector.addGoal(2, new SculkGolemSlamGoal(this));
        this.goalSelector.addGoal(3, new SculkGolemRushGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                SculkSentinelEntity.class,
                true
        ));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this,
                Monster.class,
                10,
                true,
                false,
                monster -> !(monster instanceof SculkSentinelEntity)
        ));
    }

    // --------------------
    // Attributes
    // --------------------
    public static AttributeSupplier.Builder createSculkGolemAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 95.0D)
                .add(Attributes.ATTACK_DAMAGE, 11.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 8.0D);
    }

    // --------------------
    // Sounds
    // --------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBerserk() ? SoundEvents.WARDEN_HEARTBEAT : SoundEvents.SCULK_BLOCK_CHARGE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SHRIEKER_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 0.55F + this.random.nextFloat() * 0.08F);
    }

    protected SoundEvent getRoarSound() {
        return SoundEvents.WARDEN_ROAR;
    }

    // --------------------
    // State Getters / Setters
    // --------------------
    public boolean isBerserk() {
        return this.entityData.get(BERSERK);
    }

    public void setBerserk(boolean value) {
        this.entityData.set(BERSERK, value);
    }

    public boolean isRoaring() {
        return this.entityData.get(ROARING);
    }

    public void setRoaring(boolean value) {
        this.entityData.set(ROARING, value);
    }

    public boolean isSlamming() {
        return this.entityData.get(SLAMMING);
    }

    public void setSlamming(boolean value) {
        this.entityData.set(SLAMMING, value);
    }

    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }

    public int getRoarAnimationTick() {
        return this.roarAnimationTick;
    }

    public int getSlamAnimationTick() {
        return this.slamAnimationTick;
    }


    // --------------------
    // Animation Helpers
    // --------------------
    private void setupAnimationStates() {
        boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
        boolean busy = this.attackAnimationTick > 0 || this.roarAnimationTick > 0 || this.slamAnimationTick > 0;

        if (moving || busy) {
            this.idleAnimationState.stop();
            this.idleAnimationTimeout = 0;
            return;
        }

        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private void updateClientAnimationTimers() {
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        } else {
            this.attackAnimationState.stop();
        }

        if (this.roarAnimationTick > 0) {
            --this.roarAnimationTick;
        } else {
            this.roarAnimationState.stop();
        }

        if (this.slamAnimationTick > 0) {
            --this.slamAnimationTick;
        } else {
            this.slamAnimationState.stop();
        }
    }

    // --------------------
    // Core Tick Logic
    // --------------------
    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
            this.updateClientAnimationTimers();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            return;
        }

        if (this.roarCooldown > 0) this.roarCooldown--;
        if (this.slamCooldown > 0) this.slamCooldown--;
        if (this.rushCooldown > 0) this.rushCooldown--;

        if (!this.berserkTriggered && this.getHealth() <= this.getMaxHealth() * 0.45F) {
            this.enterBerserk();
        }

        if (this.level() instanceof ServerLevel server) {
            if (this.tickCount % 120 == 0) {
                server.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY() + 1.1D, this.getZ(),
                        this.isBerserk() ? 18 : 10,
                        0.45D, 0.45D, 0.45D,
                        0.02D
                );

                server.playSound(
                        null,
                        this.blockPosition(),
                        this.isBerserk() ? SoundEvents.WARDEN_HEARTBEAT : SoundEvents.SCULK_BLOCK_CHARGE,
                        SoundSource.HOSTILE,
                        this.isBerserk() ? 1.0F : 0.7F,
                        this.isBerserk() ? 0.8F : 0.95F
                );
            }

            if (this.isRoaring()) {
                server.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY() + 1.8D, this.getZ(),
                        3,
                        0.3D, 0.25D, 0.3D,
                        0.01D
                );
            }

            if (this.isSlamming()) {
                server.sendParticles(
                        ParticleTypes.SCULK_CHARGE_POP,
                        this.getX(), this.getY() + 0.2D, this.getZ(),
                        4,
                        0.5D, 0.1D, 0.5D,
                        0.01D
                );
            }
        }
    }

    private void enterBerserk() {
        this.berserkTriggered = true;
        this.setBerserk(true);

        this.level().broadcastEntityEvent(this, (byte) 61);

        if (this.level() instanceof ServerLevel server) {
            server.playSound(
                    null,
                    this.blockPosition(),
                    this.getRoarSound(),
                    SoundSource.HOSTILE,
                    1.7F,
                    0.65F
            );

            server.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 1.5D, this.getZ(),
                    28,
                    0.7D, 0.6D, 0.7D,
                    0.03D
            );

            server.sendParticles(
                    ParticleTypes.SCULK_CHARGE_POP,
                    this.getX(), this.getY() + 1.0D, this.getZ(),
                    20,
                    0.8D, 0.4D, 0.8D,
                    0.02D
            );
        }
    }

    // --------------------
    // Alliance Logic
    // --------------------
    public boolean isGolemAlly(Entity entity) {
        return entity instanceof SculkGolemEntity;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return super.isAlliedTo(entity) || this.isGolemAlly(entity);
    }

    // --------------------
    // Combat
    // --------------------
    @Override
    public boolean doHurtTarget(Entity entity) {
        this.level().broadcastEntityEvent(this, (byte) 4);

        float baseDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (this.isBerserk()) {
            baseDamage += 3.0F;
        }

        float finalDamage = baseDamage / 2.0F + this.random.nextFloat() * (baseDamage * 0.8F);
        DamageSource source = this.damageSources().mobAttack(this);
        boolean flag = entity.hurt(source, finalDamage);

        if (flag) {
            double resist = entity instanceof LivingEntity living
                    ? living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
                    : 0.0D;

            double knockScale = Math.max(0.0D, 1.0D - resist);
            Vec3 dir = entity.position().subtract(this.position());

            if (dir.lengthSqr() < 1.0E-4D) {
                dir = new Vec3(
                        (this.random.nextDouble() - 0.5D),
                        0.0D,
                        (this.random.nextDouble() - 0.5D)
                );
            }

            dir = dir.normalize();

            double horizontalKnockback = (this.isBerserk() ? 2.0D : 1.6D) * knockScale;
            double verticalKnockback = (this.isBerserk() ? 0.55D : 0.4D) * knockScale;

            entity.push(dir.x * horizontalKnockback, verticalKnockback, dir.z * horizontalKnockback);
            entity.hurtMarked = true;

            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        this.isBerserk() ? 50 : 35,
                        0
                ));
            }

            if (this.level() instanceof ServerLevel server) {
                EnchantmentHelper.doPostAttackEffects(server, entity, source);
            }
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.2F, this.isBerserk() ? 0.65F : 0.8F);
        return flag;
    }

    // --------------------
    // Roar Ability
    // --------------------
    protected void performRoar() {
        double radius = this.isBerserk() ? 10.5D : 9.0D;
        double horizontalStrength = this.isBerserk() ? 3.1D : 2.5D;
        double verticalStrength = this.isBerserk() ? 1.0D : 0.8D;

        if (this.level() instanceof ServerLevel server) {
            server.playSound(
                    null,
                    this.blockPosition(),
                    this.getRoarSound(),
                    SoundSource.HOSTILE,
                    1.8F,
                    this.isBerserk() ? 0.7F : 0.82F
            );

            server.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 1.6D, this.getZ(),
                    this.isBerserk() ? 36 : 24,
                    0.9D, 0.5D, 0.9D,
                    0.03D
            );

            server.sendParticles(
                    ParticleTypes.SCULK_CHARGE_POP,
                    this.getX(), this.getY() + 1.0D, this.getZ(),
                    this.isBerserk() ? 26 : 16,
                    1.1D, 0.35D, 1.1D,
                    0.02D
            );
        }

        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius))) {
            if (entity == this) continue;
            if (this.isGolemAlly(entity)) continue;

            Vec3 dir = entity.position().subtract(this.position());
            if (dir.lengthSqr() < 1.0E-4D) {
                dir = new Vec3(
                        (this.random.nextDouble() - 0.5D),
                        0.0D,
                        (this.random.nextDouble() - 0.5D)
                );
            }

            dir = dir.normalize();

            entity.push(dir.x * horizontalStrength, verticalStrength, dir.z * horizontalStrength);
            entity.hurtMarked = true;

            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
            }

            if (entity instanceof Mob mob && !this.isGolemAlly(mob)) {
                mob.setTarget(null);

                Vec3 fleePos = entity.position().add(dir.scale(7.0D));
                mob.getNavigation().moveTo(fleePos.x, fleePos.y, fleePos.z, 1.35D);
            }
        }
    }

    // --------------------
    // Slam Ability
    // --------------------
    protected void performSlam() {
        double radius = this.isBerserk() ? 5.3D : 4.4D;
        float slamDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (this.isBerserk() ? 4.0F : 2.0F);

        if (this.level() instanceof ServerLevel server) {
            server.playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.IRON_GOLEM_ATTACK,
                    SoundSource.HOSTILE,
                    1.5F,
                    0.55F
            );

            server.playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.WARDEN_STEP,
                    SoundSource.HOSTILE,
                    1.2F,
                    0.65F
            );

            server.sendParticles(
                    ParticleTypes.SCULK_CHARGE_POP,
                    this.getX(), this.getY() + 0.15D, this.getZ(),
                    this.isBerserk() ? 30 : 18,
                    1.0D, 0.15D, 1.0D,
                    0.03D
            );

            server.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 0.2D, this.getZ(),
                    this.isBerserk() ? 18 : 10,
                    0.8D, 0.1D, 0.8D,
                    0.02D
            );
        }

        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius))) {
            if (entity == this) continue;
            if (this.isGolemAlly(entity)) continue;

            DamageSource source = this.damageSources().mobAttack(this);
            boolean hurt = entity.hurt(source, slamDamage);

            Vec3 dir = entity.position().subtract(this.position());
            if (dir.lengthSqr() < 1.0E-4D) {
                dir = new Vec3(
                        (this.random.nextDouble() - 0.5D),
                        0.0D,
                        (this.random.nextDouble() - 0.5D)
                );
            }

            dir = dir.normalize();

            double horizontal = this.isBerserk() ? 2.1D : 1.6D;
            double vertical = this.isBerserk() ? 0.8D : 0.6D;

            entity.push(dir.x * horizontal, vertical, dir.z * horizontal);
            entity.hurtMarked = true;

            if (hurt && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        this.isBerserk() ? 70 : 50,
                        1
                ));
            }
        }
    }

    // --------------------
    // Rush
    // --------------------
    protected void performRush() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        Vec3 dir = target.position().subtract(this.position());
        if (dir.lengthSqr() < 1.0E-4D) return;

        dir = dir.normalize();

        double speed = this.isBerserk() ? 1.6D : 1.3D;
        this.setDeltaMovement(dir.x * speed, 0.25D, dir.z * speed);

        if (this.level() instanceof ServerLevel server) {
            server.playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.RAVAGER_ROAR,
                    SoundSource.HOSTILE,
                    1.0F,
                    this.isBerserk() ? 0.7F : 0.85F
            );

            server.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 1.2D, this.getZ(),
                    12,
                    0.45D, 0.3D, 0.45D,
                    0.02D
            );
        }
    }

    // --------------------
    // Entity Events
    // --------------------
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationTick = 10;
            this.attackAnimationState.start(this.tickCount);
        } else if (id == 61) {
            this.roarAnimationTick = 30;
            this.roarAnimationState.start(this.tickCount);
        } else if (id == 62) {
            this.slamAnimationTick = 18;
            this.slamAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    // --------------------
    // Save / Load
    // --------------------
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Berserk", this.isBerserk());
        tag.putBoolean("BerserkTriggered", this.berserkTriggered);
        tag.putInt("RoarCooldown", this.roarCooldown);
        tag.putInt("SlamCooldown", this.slamCooldown);
        tag.putInt("RushCooldown", this.rushCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setBerserk(tag.getBoolean("Berserk"));
        this.berserkTriggered = tag.getBoolean("BerserkTriggered");
        this.roarCooldown = tag.getInt("RoarCooldown");
        this.slamCooldown = tag.getInt("SlamCooldown");
        this.rushCooldown = tag.getInt("RushCooldown");
    }

    // --------------------
    // Spawn Rules
    // --------------------
    public static boolean checkSculkGolemSpawnRules(EntityType<SculkGolemEntity> type, LevelAccessor level,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        BlockState below = level.getBlockState(pos.below());

        return (below.is(ModBlocks.SCULK_SANCTUM_GRASS_BLOCK.get())
                || below.is(ModBlocks.SCULK_SANCTUM_DIRT_BLOCK.get()))
                && level.getMaxLocalRawBrightness(pos) < 8;
    }

    // --------------------
    // Inner Goals
    // --------------------
    static class SculkGolemRoarGoal extends Goal {
        private final SculkGolemEntity mob;
        private int warmupTicks;
        private int totalTicks;
        private boolean usedRoar;

        public SculkGolemRoarGoal(SculkGolemEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null
                    && target.isAlive()
                    && this.mob.roarCooldown <= 0
                    && !this.mob.isRoaring()
                    && !this.mob.isSlamming()
                    && this.mob.onGround()
                    && this.mob.distanceTo(target) <= 10.0F;
        }

        @Override
        public void start() {
            this.usedRoar = false;
            this.warmupTicks = this.mob.isBerserk() ? 10 : 16;
            this.totalTicks = this.warmupTicks + 8;

            this.mob.setRoaring(true);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 61);
            this.mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            if (!this.usedRoar) {
                this.warmupTicks--;
                if (this.warmupTicks <= 0) {
                    this.usedRoar = true;
                    this.mob.performRoar();
                    this.mob.roarCooldown = this.mob.isBerserk() ? 120 : 180;
                }
            }

            this.totalTicks--;
        }

        @Override
        public boolean canContinueToUse() {
            return this.totalTicks > 0;
        }

        @Override
        public void stop() {
            this.mob.setRoaring(false);
        }
    }

    static class SculkGolemSlamGoal extends Goal {
        private final SculkGolemEntity mob;
        private int warmupTicks;
        private int totalTicks;
        private boolean usedSlam;

        public SculkGolemSlamGoal(SculkGolemEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null
                    && target.isAlive()
                    && this.mob.slamCooldown <= 0
                    && !this.mob.isRoaring()
                    && !this.mob.isSlamming()
                    && this.mob.onGround()
                    && this.mob.distanceTo(target) <= 4.5F;
        }

        @Override
        public void start() {
            this.usedSlam = false;
            this.warmupTicks = this.mob.isBerserk() ? 8 : 12;
            this.totalTicks = this.warmupTicks + 6;

            this.mob.setSlamming(true);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 62);
            this.mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            if (!this.usedSlam) {
                this.warmupTicks--;
                if (this.warmupTicks <= 0) {
                    this.usedSlam = true;
                    this.mob.performSlam();
                    this.mob.slamCooldown = this.mob.isBerserk() ? 70 : 110;
                }
            }

            this.totalTicks--;
        }

        @Override
        public boolean canContinueToUse() {
            return this.totalTicks > 0;
        }

        @Override
        public void stop() {
            this.mob.setSlamming(false);
        }
    }

    static class SculkGolemRushGoal extends Goal {
        private final SculkGolemEntity mob;

        public SculkGolemRushGoal(SculkGolemEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null
                    && target.isAlive()
                    && this.mob.rushCooldown <= 0
                    && !this.mob.isRoaring()
                    && !this.mob.isSlamming()
                    && this.mob.onGround()
                    && this.mob.distanceTo(target) >= 5.0F
                    && this.mob.distanceTo(target) <= 14.0F;
        }

        @Override
        public void start() {
            this.mob.rushCooldown = this.mob.isBerserk() ? 65 : 100;
            this.mob.performRush();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
}