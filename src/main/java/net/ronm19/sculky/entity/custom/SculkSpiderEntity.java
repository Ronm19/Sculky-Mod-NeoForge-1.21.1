package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SculkSpiderEntity extends Spider implements Enemy {

    // === CONFIG ===
    private static final int GROUP_RADIUS = 12;
    private static final double GROUP_DAMAGE_BONUS = 0.15;
    private static final double GROUP_SPEED_BONUS = 0.10;

    public SculkSpiderEntity(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    // ===============================
    // ATTRIBUTES
    // ===============================
    public static AttributeSupplier.Builder createSculkSpiderAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)      // stronger than vanilla
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 28.0D);
    }

    // ===============================
    // AI GOALS
    // ===============================
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    // ===============================
    // GROUP BEHAVIOR (THE IMPORTANT PART)
    // ===============================
    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;
        if (this.getTarget() == null) return;

        applyGroupBuffs();
    }

    private void applyGroupBuffs() {
        AABB box = this.getBoundingBox().inflate(GROUP_RADIUS);

        List<SculkSpiderEntity> spiders =
                this.level().getEntitiesOfClass(SculkSpiderEntity.class, box);

        int nearby = spiders.size() - 1; // exclude self
        if (nearby <= 0) return;

        double damageBonus = 1.0 + (nearby * GROUP_DAMAGE_BONUS);
        double speedBonus = 1.0 + (nearby * GROUP_SPEED_BONUS);

        this.getAttribute(Attributes.ATTACK_DAMAGE)
                .setBaseValue(4.5D * damageBonus);

        this.getAttribute(Attributes.MOVEMENT_SPEED)
                .setBaseValue(0.32D * speedBonus);
    }

    // ===============================
    // SPAWNING LOGIC (GROUP SPAWN)
    // ===============================
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);

        if (spawnType == MobSpawnType.NATURAL && level instanceof ServerLevel serverLevel) {
            spawnGroup(serverLevel);
        }

        return data;
    }

    private void spawnGroup(ServerLevel level) {
        int count = 2 + this.random.nextInt(3); // total 3–5 spiders

        for (int i = 0; i < count; i++) {
            SculkSpiderEntity spider =
                    (SculkSpiderEntity) this.getType().create(level);

            if (spider == null) continue;

            spider.moveTo(
                    this.getX() + this.random.nextInt(-2, 3),
                    this.getY(),
                    this.getZ() + this.random.nextInt(-2, 3),
                    this.getYRot(),
                    this.getXRot()
            );

            level.addFreshEntity(spider);
        }
    }

    // ===============================
    // SOUNDS
    // ===============================
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SENSOR_FALL;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void playStepSound( BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SCULK_BLOCK_STEP, 0.15F, 1.0F);
    }

}
