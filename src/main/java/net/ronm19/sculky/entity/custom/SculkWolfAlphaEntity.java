package net.ronm19.sculky.entity.custom;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SculkWolfAlphaEntity extends Wolf implements NeutralMob {

    private boolean aggressive = false;
    private int warningTicks = 0;
    private int calmReset = 0;

    private int jumpCooldown = 0; // For jump attack
    private int darknessBuffTimer = 0; // For darkness empowerment

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public SculkWolfAlphaEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
    }

    public boolean isAlpha() {
        return true;
    }

    // ---------------------------------------------------------
    //           GOALS & AI
    // ---------------------------------------------------------
    @Override
    protected void registerGoals() {
        super.registerGoals(); // keep wolf behavior

        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    // ---------------------------------------------------------
    //           ANGER OVERRIDE
    // ---------------------------------------------------------
    @Override
    public boolean isAngry() {
        return this.aggressive;
    }

    public void setAngry(boolean angry) {
        this.aggressive = angry;
    }

    // ---------------------------------------------------------
    //           TICK LOGIC
    // ---------------------------------------------------------
    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        if (jumpCooldown > 0) jumpCooldown--;
        darknessBuff();

        Player player = this.level().getNearestPlayer(this, 12);
        if (player != null && !player.isCreative() && !player.isSpectator()) {

            double dist = this.distanceTo(player);

            if (!aggressive) {
                if (dist <= 6) {
                    handleWarning(player);
                } else {
                    warningTicks = 0;
                }
            }

            if (aggressive) {
                calmReset++;

                // Jump to attack opportunity
                if (dist <= 4 && dist >= 1.5 && jumpCooldown == 0) {
                    performLeap(player);
                }

                if (dist >= 12 && calmReset > 120) {
                    aggressive = false;
                    calmReset = 0;
                    warningTicks = 0;
                }
            }
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 24;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    // ---------------------------------------------------------
    //           WARNING PHASE + SPIKE FLARE
    // ---------------------------------------------------------
    private void handleWarning(Player player) {

        // Soft spike flare (renderer will check this)
        warningTicks++;

        if (warningTicks % 20 == 0) {
            this.level().playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.WOLF_GROWL,
                    SoundSource.HOSTILE,
                    1.0F,
                    0.6F
            );
        }

        if (warningTicks > 40) {
            becomeAggressive(player);
        }
    }

    private void becomeAggressive(Player player) {
        this.aggressive = true;
        this.calmReset = 0;
        this.warningTicks = 0;

        // Darkness pulse
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));

        // Mini roar
        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_ROAR,
                SoundSource.HOSTILE,
                1.2F,
                0.75F
        );

        // Pack fear effect
        for (Wolf wolf : level().getEntitiesOfClass(Wolf.class, this.getBoundingBox().inflate(10))) {
            if (wolf != this) {
                wolf.getNavigation().moveTo(this, 1.5D);
            }
        }
    }

    public boolean isWarning() {
        return !aggressive && warningTicks > 0;
    }

    // ---------------------------------------------------------
    //           DARKNESS EMPOWERMENT
    // ---------------------------------------------------------
    private void darknessBuff() {
        int brightness = this.level().getMaxLocalRawBrightness(this.blockPosition());
        if (brightness <= 2) {
            darknessBuffTimer++;
            if (darknessBuffTimer % 40 == 0) {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, true, false));
            }
        } else {
            darknessBuffTimer = 0;
        }
    }

    // ---------------------------------------------------------
    //           MEDIUM LEAP ATTACK
    // ---------------------------------------------------------
    private void performLeap(Player player) {
        jumpCooldown = 40;

        double dx = player.getX() - this.getX();
        double dz = player.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        this.setDeltaMovement(
                (dx / distance) * 0.7,
                0.4,
                (dz / distance) * 0.7
        );

        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.WOLF_GROWL,
                SoundSource.HOSTILE,
                1.0F,
                0.8F
        );
    }

    // ---------------------------------------------------------
    //           STRONGER MELEE HIT WITH MINI KNOCKBACK
    // ---------------------------------------------------------
    @Override
    public boolean doHurtTarget(@NotNull Entity target) {

        boolean result = super.doHurtTarget(target);

        if (result) {
            target.push(
                    this.getLookAngle().x * 0.5,
                    0.1,
                    this.getLookAngle().z * 0.5
            );
        }

        return result;
    }

    // ---------------------------------------------------------
    //           ATTRIBUTES
    // ---------------------------------------------------------
    public static AttributeSupplier.Builder createSculkWolfAlphaAttributes() {
        return Wolf.createAttributes()
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    // ---------------------------------------------------------
    //           SOUNDS
    // ---------------------------------------------------------
    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return aggressive ? SoundEvents.WOLF_GROWL : SoundEvents.WOLF_PANT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( @NotNull DamageSource damageSource) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }
}
