package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.variant.CorruptedSculkSkeletonVariant;
import net.ronm19.sculky.entity.variant.CorruptedSculkStalkerVariant;

import javax.annotation.Nullable;
import java.util.Objects;

public class SculkStalkerEntity extends Spider {

    public static final EntityDataAccessor<Integer> CORRUPTED =
            SynchedEntityData.defineId(SculkStalkerEntity.class, EntityDataSerializers.INT);

    private int stealthTimer = 0;
    private boolean inStealth = false;

    private int stealthCooldown = 0;
    private int stealthDuration = 0;
    private int backstabCooldown = 0;


    public SculkStalkerEntity( EntityType<? extends Spider> type, Level level ) {
        super(type, level);
        this.xpReward = 18;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        // Ambush attack → runs fast when close
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, true));

        // Wander
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));

        // Look Around
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (stealthCooldown > 0) stealthCooldown--;
        if (stealthDuration > 0) stealthDuration--;

        boolean dark = this.level().getMaxLocalRawBrightness(this.blockPosition()) <= 4;
        boolean night = this.level().isNight();

        if (!inStealth && stealthCooldown == 0 && dark) {
            startStealth(night);
        }

        if (inStealth && stealthDuration <= 0) {
            stopStealth();
        }

        if (this.getTarget() != null) {
            if (backstabCooldown > 0) backstabCooldown--;
            tryBackstab(this.getTarget());
        }
    }


    private void startStealth( boolean night ) {
        inStealth = true;

        stealthDuration = night ? 100 : 50; // 5s night, 2.5s day
        stealthCooldown = night ? 160 : 220;

        this.addEffect(new MobEffectInstance(
                MobEffects.INVISIBILITY,
                stealthDuration,
                0,
                false,
                false
        ));

        this.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                stealthDuration,
                0,
                false,
                false
        ));
    }

    private void stopStealth() {
        inStealth = false;
        this.removeEffect(MobEffects.INVISIBILITY);
        this.removeEffect(MobEffects.MOVEMENT_SPEED);
    }


    // Backstab if behind player
    private void tryBackstab( LivingEntity target ) {
        if (backstabCooldown > 0) return;

        Vec3 stalkerDir = this.getLookAngle().normalize();
        Vec3 targetDir = target.getViewVector(1.0F).normalize();

        double dot = stalkerDir.dot(targetDir);

        if (dot < -0.7 && this.distanceTo(target) < 1.8F) {
            target.hurt(
                    this.damageSources().mobAttack(this),
                    (float) (getAttackDamage() * 1.6F)
            );
            backstabCooldown = 40; // 2 seconds
        }
    }


    private double getAttackDamage() {
        return this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public static AttributeSupplier.Builder createSculkStalkerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    /* ------------------------- Data ------------------------------ */

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData
    ) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);

        boolean corrupted;

        if (spawnType == MobSpawnType.SPAWN_EGG) {
            // Spawn egg: small, fixed chance
            corrupted = this.random.nextFloat() < 0.15F; // 15%
        } else {
            // Natural spawn: environment-based
            corrupted = shouldSpawnCorrupted(
                    level,
                    this.blockPosition(),
                    level.getRandom(),
                    difficulty
            );
        }

        if (corrupted) {
            this.setVariant(CorruptedSculkStalkerVariant.CORRUPTED);
            applyCorruptedAttributes();
        } else {
            this.setVariant(CorruptedSculkStalkerVariant.NORMAL);
        }

        return spawnData;
    }


    public static boolean shouldSpawnCorrupted(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            DifficultyInstance difficulty
    ) {
        float baseChance;

        // Surface bias
        if (pos.getY() >= 60) {
            baseChance = 0.12F; // noticeable but not spammy
        }
        // Underground (rare but possible)
        else {
            baseChance = 0.04F;
        }

        // Difficulty scaling
        switch (difficulty.getDifficulty()) {
            case PEACEFUL -> baseChance = 0.0F;
            case EASY -> baseChance *= 0.6F;
            case NORMAL -> baseChance *= 1.0F;
            case HARD -> baseChance *= 1.4F;
        }

        return random.nextFloat() < baseChance;
    }

    @Override
    protected void defineSynchedData( SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(CORRUPTED, 0);
    }

    @Override
    public void addAdditionalSaveData( CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(CORRUPTED, tag.getInt("Variant"));

        if (getVariant() == CorruptedSculkStalkerVariant.CORRUPTED) {
            applyCorruptedAttributes();
        }
    }

    /* ------------------------- Variant ------------------------------ */

    private void applyCorruptedAttributes() {
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(36.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(7.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.30D);
        Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(32.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(1.0D);

        this.setHealth(this.getMaxHealth());
    }

    private int getTypeVariant() {
        return this.entityData.get(CORRUPTED);
    }

    public CorruptedSculkStalkerVariant getVariant() {
        return CorruptedSculkStalkerVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(CorruptedSculkStalkerVariant variant) {
        this.entityData.set(CORRUPTED, variant.getId() & 255);
    }
}
