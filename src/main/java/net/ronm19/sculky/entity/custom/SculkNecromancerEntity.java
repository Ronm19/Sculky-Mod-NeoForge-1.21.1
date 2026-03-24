package net.ronm19.sculky.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.entity.ModEntities;
import org.jetbrains.annotations.NotNull;

public class SculkNecromancerEntity extends Bogged implements Enemy, RangedAttackMob {

    private int summonCooldown = 0;

    public SculkNecromancerEntity(EntityType<? extends Bogged> entityType, Level level) {
        super(entityType, level);
    }

    // =========================
    // ATTRIBUTES
    // =========================
    public static AttributeSupplier.Builder createSculkNecromancerAttributes() {
        return Bogged.createAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    // =========================
    // GOALS
    // =========================
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Ranged caster
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 40, 12.0F));

        // Avoid getting too close
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.2D));

        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Targets
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    // =========================
    // AI TICK
    // =========================
    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {

            if (summonCooldown > 0) {
                summonCooldown--;
            }

            LivingEntity target = this.getTarget();

            if (target != null && this.distanceTo(target) < 16.0F && summonCooldown <= 0) {
                summonMinions();
                summonCooldown = 200 + this.random.nextInt(100); // 10–15 sec
            }
        }
    }

    // =========================
    // SUMMON
    // =========================
    private void summonMinions() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        this.playSound(SoundEvents.SCULK_SHRIEKER_SHRIEK, 1.0F, 0.9F);

        RandomSource random = this.random;

        for (int i = 0; i < 2; i++) {
            double offsetX = (random.nextDouble() - 0.5D) * 4.0D;
            double offsetZ = (random.nextDouble() - 0.5D) * 4.0D;

            BlockPos spawnPos = this.blockPosition().offset((int) offsetX, 0, (int) offsetZ);

            SculkSkeletonEntity minion = ModEntities.SCULK_SKELETON.get().create(serverLevel);
            if (minion == null) continue;

            minion.moveTo(
                    spawnPos.getX() + 0.5D,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5D,
                    0.0F,
                    0.0F
            );

            minion.finalizeSpawn(
                    serverLevel,
                    serverLevel.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.MOB_SUMMONED,
                    null
            );

            if (this.getTarget() != null) {
                minion.setTarget(this.getTarget());
            }

            serverLevel.addFreshEntity(minion);
        }
    }

    // =========================
    // SUN BURN IMMUNITY
    // =========================


    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    // =========================
    // RANGED ATTACK
    // =========================
    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {

        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.85F);

        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));

        target.hurt(this.damageSources().mobAttack(this), 3.0F);
    }

    // =========================
    // SOUNDS
    // =========================
    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.BOGGED_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.BOGGED_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.BOGGED_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BOGGED_STEP, 0.15F, 1.0F);
    }

    // =========================
    // SPAWN FINALIZE
    // =========================

    public SpawnGroupData finalizeSpawn(ServerLevel level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        SpawnGroupData spawnData) {
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }
}