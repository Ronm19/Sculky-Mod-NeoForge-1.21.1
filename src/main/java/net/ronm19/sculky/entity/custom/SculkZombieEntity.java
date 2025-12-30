package net.ronm19.sculky.entity.custom;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.variant.CorruptedSculkZombieVariant;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class SculkZombieEntity extends Zombie implements Enemy {

    public static final EntityDataAccessor<Integer> CORRUPTED =
            SynchedEntityData.defineId(SculkZombieEntity.class, EntityDataSerializers.INT);


    private int pulseCooldown = 0;
    private int infectionCooldown = 0;

    public SculkZombieEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    // ---------------------------------------------------------
    //               DATA
    // ---------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
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

        if (getVariant() == CorruptedSculkZombieVariant.CORRUPTED) {
            applyCorruptedAttributes();
        }
    }


    // ---------------------------------------------------------
    //               VARIANT
    // ---------------------------------------------------------

    private int getTypeVariant() {
        return this.entityData.get(CORRUPTED);
    }

    public CorruptedSculkZombieVariant getVariant() {
        return CorruptedSculkZombieVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(CorruptedSculkZombieVariant variant) {
        this.entityData.set(CORRUPTED, variant.getId() & 255);
    }

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
            corrupted = this.random.nextFloat() < 0.6F;
        } else {
            corrupted = shouldSpawnCorrupted(
                    level,
                    this.blockPosition(),
                    level.getRandom(),
                    difficulty
            );
        }

        if (corrupted) {
            this.setVariant(CorruptedSculkZombieVariant.CORRUPTED);
            applyCorruptedAttributes();
        } else {
            this.setVariant(CorruptedSculkZombieVariant.NORMAL);
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


    private void applyCorruptedAttributes() {
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(70.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(8.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.23D);
        Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(35.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(3.5D);

        this.setHealth(this.getMaxHealth());
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 2; // usually alone, sometimes paired
    }






    // ---------------------------------------------------------
    //               ATTRIBUTES
    // ---------------------------------------------------------
    public static AttributeSupplier.Builder createSculkZombieAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    // ---------------------------------------------------------
    //                   MAIN TICK LOGIC
    // ---------------------------------------------------------
    @Override
    public void tick() {
        super.tick();

        if (pulseCooldown > 0) pulseCooldown--;
        if (infectionCooldown > 0) infectionCooldown--;

        if (pulseCooldown == 0) {
            pulseCooldown = 60 + this.random.nextInt(60);
            spawnSculkPulse();
        }

        applyDarknessEmpowerment();
        tryInfectNearbyZombies();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();  // KEEP VANILLA ZOMBIE AI

        // ------------------------------
        //      SCULK ZOMBIE EXTRAS
        // ------------------------------

        // Slower, creepy dark wandering
        this.goalSelector.addGoal(8, new DarkWanderGoal(this, 1.0));

        // Sculk stare at player (creepy effect)
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 12.0F));

        // Random sculk twitch (idle movement)
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));

    }

    // ---------------------------------------------------------
    //                   SCULK PULSE EFFECT
    // ---------------------------------------------------------
    private void spawnSculkPulse() {
        if (!this.level().isClientSide) {
            this.level().playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.SCULK_BLOCK_CHARGE,
                    SoundSource.AMBIENT,
                    0.4F,
                    1.3F
            );
        }

        for (int i = 0; i < 5; i++) {
            this.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SCULK_CHARGE_POP,
                    this.getX() + (random.nextDouble() - 0.5) * 0.4,
                    this.getY() + random.nextDouble() * 1.2,
                    this.getZ() + (random.nextDouble() - 0.5) * 0.4,
                    0, 0, 0
            );
        }
    }

    // ---------------------------------------------------------
    //                   DARKNESS EMPOWERMENT
    // ---------------------------------------------------------
    private void applyDarknessEmpowerment() {
        int brightness = this.level().getMaxLocalRawBrightness(this.blockPosition());
        if (brightness <= 2) {
            // Deep-dark buff
            if (!this.hasEffect(MobEffects.DAMAGE_BOOST)) {
                this.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_BOOST, 100, 0, true, false
                ));
            }
        }
    }

    // ---------------------------------------------------------
    //              SCULK INFECTION OF NORMAL ZOMBIES
    // ---------------------------------------------------------
    private void tryInfectNearbyZombies() {
        if (infectionCooldown > 0) return;

        infectionCooldown = 200 + this.random.nextInt(200);

        this.level().getEntitiesOfClass(Zombie.class, this.getBoundingBox().inflate(3))
                .forEach(zombie -> {

                    if (zombie == this) return;

                    // Already Sculk? Skip
                    if (zombie instanceof SculkZombieEntity) return;

                    // Small chance to infect
                    if (this.random.nextDouble() < 0.05) {

                        BlockPos pos = zombie.blockPosition();
                        zombie.discard(); // Remove vanilla zombie

                        // Spawn sculk zombie
                        SculkZombieEntity sculk = (SculkZombieEntity) this.getType().create(level());
                        if (sculk != null) {
                            sculk.moveTo(pos.getX(), pos.getY(), pos.getZ());
                            level().addFreshEntity(sculk);

                            level().playSound(
                                    null,
                                    pos,
                                    SoundEvents.SCULK_BLOCK_SPREAD,
                                    SoundSource.AMBIENT,
                                    1.0F,
                                    1.2F
                            );
                        }
                    }
                });
    }

    // ---------------------------------------------------------
    //                   SOUND OVERRIDES
    // ---------------------------------------------------------
    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_BLOCK_CHARGE;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( @NotNull DamageSource dmg) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SENSOR_HIT;
    }

    // ---------------------------------------------------------
    //                   INNER GOALS
    // ---------------------------------------------------------

    public static class DarkWanderGoal extends Goal {

        private final SculkZombieEntity zombie;
        private final double speed;

        public DarkWanderGoal(SculkZombieEntity zombie, double speed) {
            this.zombie = zombie;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !zombie.isAggressive() && zombie.getRandom().nextInt(10) == 0;
        }

        @Override
        public void tick() {
            BlockPos targetPos = zombie.blockPosition().offset(
                    zombie.random.nextInt(7) - 3,
                    zombie.random.nextInt(2) - 1,
                    zombie.random.nextInt(7) - 3
            );

            int light = zombie.level().getMaxLocalRawBrightness(targetPos);

            // Only wander toward dark spots
            if (light <= 5) {
                zombie.getNavigation().moveTo(
                        targetPos.getX(),
                        targetPos.getY(),
                        targetPos.getZ(),
                        speed
                );
            }
        }
    }
}
