package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.ronm19.sculky.entity.variant.CorruptedSculkCreeperVariant;
import net.ronm19.sculky.entity.variant.CorruptedSculkEndermanVariant;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;

public class SculkEndermanEntity extends EnderMan implements NeutralMob {

    public static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SculkEndermanEntity.class, EntityDataSerializers.INT);

    public SculkEndermanEntity(EntityType<? extends EnderMan> entityType, Level level) {
        super(entityType, level);
    }

    /* ===================== */
    /*   ATTRIBUTES          */
    /* ===================== */

    public static AttributeSupplier.Builder createSculkEndermanAttributes() {
        return EnderMan.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, (double) 1.0F, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, (double) 1.0F, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new VibrationAwarenessGoal(this));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Endermite.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, TamableAnimal.class, true, false));

        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));


    }

    /* ============================= */
    /* TICK                          */
    /* ============================= */

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnAmbientParticles();
        }
    }

    private void spawnTeleportParticles() {
        int count = isCorrupted() ? 24 : 14;

        for (int i = 0; i < count; i++) {
            double x = getX() + (random.nextDouble() - 0.5D);
            double y = getY() + random.nextDouble() * getBbHeight();
            double z = getZ() + (random.nextDouble() - 0.5D);

            double vx = (random.nextDouble() - 0.5D) * 0.12D;
            double vy = random.nextDouble() * 0.12D;
            double vz = (random.nextDouble() - 0.5D) * 0.12D;

            level().addParticle(
                    isCorrupted()
                            ? (net.minecraft.core.particles.ParticleOptions) ParticleTypes.SCULK_CHARGE
                            : net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                    x, y, z,
                    vx, vy, vz
            );
        }

        // Extra instability cue for corrupted variant
        if (isCorrupted()) {
            level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SOUL,
                    getX(), getY() + 1.1D, getZ(),
                    0.0D, 0.06D, 0.0D
            );
        }
    }


    /* ============================= */
    /* AMBIENT PARTICLES METHOD       */
    /* ============================= */


    private void spawnAmbientParticles() {
        // Throttle particle spawning (every few ticks)
        if (this.tickCount % (isCorrupted() ? 3 : 6) != 0) return;

        double x = getX() + (random.nextDouble() - 0.5D) * 0.6D;
        double y = getY() + random.nextDouble() * getBbHeight();
        double z = getZ() + (random.nextDouble() - 0.5D) * 0.6D;

        double vx = (random.nextDouble() - 0.5D) * 0.02D;
        double vy = random.nextDouble() * 0.02D;
        double vz = (random.nextDouble() - 0.5D) * 0.02D;

        if (isCorrupted()) {
            // Corrupted: stronger sculk energy
            level().addParticle(
                    ParticleTypes.SCULK_CHARGE_POP,
                    x, y, z,
                    vx, vy, vz
            );

            // Rare soul flicker
            if (random.nextFloat() < 0.15f) {
                level().addParticle(
                        ParticleTypes.SOUL,
                        x, y + 0.2D, z,
                        0.0D, 0.01D, 0.0D
                );
            }
        } else {
            // Normal skulk enderman
            level().addParticle(
                    ParticleTypes.SCULK_SOUL,
                    x, y, z,
                    vx, vy, vz
            );
        }
    }


    /* ============================= */
    /* VARIANT / SAVE                */
    /* ============================= */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(CorruptedSculkEndermanVariant.byId(tag.getInt("Variant")));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData data) {
        data = super.finalizeSpawn(level, difficulty, spawnType, data);

        boolean corrupted = spawnType == MobSpawnType.SPAWN_EGG
                ? random.nextFloat() < 0.6f
                : random.nextFloat() < (blockPosition().getY() < 40 ? 0.45f : 0.12f);

        setVariant(corrupted
                ? CorruptedSculkEndermanVariant.CORRUPTED
                : CorruptedSculkEndermanVariant.NORMAL);

        if (corrupted) applyCorruptedAttributes();
        return data;
    }

    private void applyCorruptedAttributes() {
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(56.0D);
        Objects.requireNonNull(getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.30D);
        Objects.requireNonNull(getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(58.0D);
        Objects.requireNonNull(getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(10.0D);
        setHealth(getMaxHealth());
    }

    public CorruptedSculkEndermanVariant getVariant() {
        return CorruptedSculkEndermanVariant.byId(entityData.get(VARIANT));
    }

    private void setVariant(CorruptedSculkEndermanVariant variant) {
        entityData.set(VARIANT, variant.getId());
    }

    private boolean isCorrupted() {
        return getVariant() == CorruptedSculkEndermanVariant.CORRUPTED;
    }


    /* ============================= */
    /* SOUNDS                        */
    /* ============================= */

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource src) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SHRIEKER_SHRIEK;
    }

    /* ============================= */
    /* INNER GOALS                   */
    /* ============================= */

    static class VibrationAwarenessGoal extends Goal {

        private final SculkEndermanEntity enderman;
        private Player vibrationSource;
        private int lookTime;

        public VibrationAwarenessGoal(SculkEndermanEntity enderman) {
            this.enderman = enderman;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (enderman.isAggressive() || enderman.getTarget() != null) return false;

            double range = enderman.isCorrupted() ? 16.0D : 12.0D;

            Player nearest = enderman.level().getNearestPlayer(
                    enderman.getX(),
                    enderman.getY(),
                    enderman.getZ(),
                    range,
                    player -> isVibrating((Player) player)
            );

            if (nearest != null) {
                vibrationSource = nearest;
                return true;
            }

            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return lookTime > 0
                    && vibrationSource != null
                    && vibrationSource.isAlive()
                    && !enderman.isAggressive();
        }

        @Override
        public void start() {
            lookTime = enderman.isCorrupted() ? 30 : 20;
        }

        @Override
        public void stop() {
            vibrationSource = null;
        }

        @Override
        public void tick() {
            if (enderman.level().isClientSide) return;

            lookTime--;

            enderman.getLookControl().setLookAt(
                    vibrationSource.getX(),
                    vibrationSource.getEyeY(),
                    vibrationSource.getZ(),
                    30.0F,
                    30.0F
            );
        }

        /* ============================= */
        /* VIBRATION CHECK               */
        /* ============================= */

        private boolean isVibrating(Player player) {
            if (player.isSpectator() || player.isCreative()) return false;
            if (player.isCrouching()) return false;

            // Horizontal movement check
            return player.getDeltaMovement().horizontalDistanceSqr() > 0.002D;
        }
    }
}
