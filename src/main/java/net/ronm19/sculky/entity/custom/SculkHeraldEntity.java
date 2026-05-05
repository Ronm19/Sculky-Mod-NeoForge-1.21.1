package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.item.ModItems;

public class SculkHeraldEntity extends Monster {
    public final AnimationState idleAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int resonanceCooldown = 0;

    private static final float ECHOES_OF_THE_CROWN_DROP_CHANCE = 0.05F; // 5%

    private static final int RESONANCE_COOLDOWN_TICKS = 120; // 6 seconds
    private static final double RESONANCE_RANGE = 9.0D;
    private static final float RESONANCE_DAMAGE = 5.0F;

    public SculkHeraldEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 14;
    }

    public static AttributeSupplier.Builder createSculkHeraldAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));

        // This is technically walking AI, but visually your animation can make it look like gliding.
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.75D));

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        } else {
            this.tickResonanceWave();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private void tickResonanceWave() {
        if (this.resonanceCooldown > 0) {
            --this.resonanceCooldown;
            return;
        }

        LivingEntity target = this.getTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        if (this.distanceToSqr(target) <= RESONANCE_RANGE * RESONANCE_RANGE && this.hasLineOfSight(target)) {
            this.performResonanceWave(target);
            this.resonanceCooldown = RESONANCE_COOLDOWN_TICKS;
        }
    }

    private void performResonanceWave(LivingEntity target) {
        DamageSource damageSource = this.damageSources().magic();

        target.hurt(damageSource, RESONANCE_DAMAGE);
        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));

        Vec3 pushDirection = target.position().subtract(this.position()).normalize();
        target.push(pushDirection.x * 0.65D, 0.18D, pushDirection.z * 0.65D);
        target.hurtMarked = true;

        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE,
                1.4F,
                0.75F + this.random.nextFloat() * 0.15F
        );

        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_SONIC_BOOM,
                SoundSource.HOSTILE,
                0.65F,
                1.25F
        );

        if (this.level() instanceof ServerLevel serverLevel) {
            this.spawnResonanceParticles(serverLevel, target);
        }
    }

    private void spawnResonanceParticles(ServerLevel serverLevel, LivingEntity target) {
        Vec3 start = this.position().add(0.0D, this.getBbHeight() * 0.6D, 0.0D);
        Vec3 end = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 direction = end.subtract(start);

        int steps = 18;

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            Vec3 pos = start.add(direction.scale(progress));

            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    pos.x,
                    pos.y,
                    pos.z,
                    2,
                    0.08D,
                    0.08D,
                    0.08D,
                    0.01D
            );
        }

        serverLevel.sendParticles(
                ParticleTypes.SONIC_BOOM,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5D,
                target.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hurt = super.doHurtTarget(entity);

        if (hurt && entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0));
        }

        return hurt;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        // Main Herald drop: guaranteed Royal Sculk Fragment
        this.spawnAtLocation(new ItemStack(ModItems.ROYAL_SCULK_FRAGMENT.get()));

        // Rare music disc drop, only if killed by a player
        if (recentlyHit && damageSource.getEntity() instanceof Player) {
            if (this.random.nextFloat() < ECHOES_OF_THE_CROWN_DROP_CHANCE) {
                this.spawnAtLocation(new ItemStack(ModItems.ECHOES_OF_THE_CROWN_MUSIC_DISC.get()));
            }
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public static boolean canSpawn(
            EntityType<SculkHeraldEntity> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random)
                && level.getRawBrightness(pos, 0) <= 7;
    }
}