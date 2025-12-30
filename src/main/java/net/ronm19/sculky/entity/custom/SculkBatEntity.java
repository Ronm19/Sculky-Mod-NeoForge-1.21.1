package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class SculkBatEntity extends Bat implements NeutralMob {

    private boolean angry = false;
    private int angerTimer = 0;
    private int pulseCooldown = 0;

    public SculkBatEntity(EntityType<? extends Bat> type, Level level) {
        super(type, level);
    }

    // ---------------------------------------------------------
    //               ATTRIBUTES
    // ---------------------------------------------------------
    public static AttributeSupplier.Builder createSculkBatAttributes() {
        return Bat.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    // ---------------------------------------------------------
    //               GOALS
    // ---------------------------------------------------------
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AvoidLightGoal(this));
        this.goalSelector.addGoal(2, new PerchGoal(this));
        this.goalSelector.addGoal(3, new AmbientFlightGoal(this));
        this.goalSelector.addGoal(4, new AttackBackGoal(this));
    }

    // ---------------------------------------------------------
    //               DAMAGE → ANGER
    // ---------------------------------------------------------
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (result) {
            angry = true;
            angerTimer = 200; // 10 seconds
            this.setResting(false); // wake up if hit while perched
        }

        return result;
    }

    // ---------------------------------------------------------
    //               TICK LOGIC
    // ---------------------------------------------------------
    @Override
    public void tick() {
        super.tick();

        // Anger countdown
        if (angry) {
            angerTimer--;
            if (angerTimer <= 0) angry = false;
        }

        // Sculk pulse cooldown
        if (pulseCooldown > 0) pulseCooldown--;

        // Emit sculk pulse (only when calm)
        if (pulseCooldown == 0 && !angry) {
            pulseCooldown = 40 + this.random.nextInt(40);
            spawnSculkPulse();
        }
    }

    private void spawnSculkPulse() {
        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_BLOCK_CHARGE,
                SoundSource.AMBIENT,
                0.3F,
                1.4F
        );

        for (int i = 0; i < 4; i++) {
            this.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SCULK_CHARGE_POP,
                    this.getX() + (random.nextDouble() - 0.5) * 0.3,
                    this.getY() + (random.nextDouble() - 0.5) * 0.3,
                    this.getZ() + (random.nextDouble() - 0.5) * 0.3,
                    0, 0, 0
            );
        }
    }

    // ---------------------------------------------------------
    //               AMBIENT SOUNDS
    // ---------------------------------------------------------
    @Override
    public SoundEvent getAmbientSound() {
        return this.isResting() ? SoundEvents.BAT_AMBIENT : SoundEvents.BAT_LOOP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    // ---------------------------------------------------------
    //         GOAL: Avoid Light Sources
    // ---------------------------------------------------------
    static class AvoidLightGoal extends Goal {

        private final SculkBatEntity bat;

        public AvoidLightGoal(SculkBatEntity bat) {
            this.bat = bat;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return bat.level().getMaxLocalRawBrightness(bat.blockPosition()) > 6 && !bat.isResting();
        }

        @Override
        public void tick() {
            Vec3 dir = new Vec3(
                    bat.random.nextDouble() - 0.5,
                    bat.random.nextDouble() * 0.4,
                    bat.random.nextDouble() - 0.5
            ).normalize().scale(0.4);

            bat.setDeltaMovement(dir);
        }
    }

    // ---------------------------------------------------------
    //         GOAL: Perch on Ceilings
    // ---------------------------------------------------------
    static class PerchGoal extends Goal {

        private final SculkBatEntity bat;

        public PerchGoal(SculkBatEntity bat) {
            this.bat = bat;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (bat.angry) return false;

            BlockPos above = bat.blockPosition().above();
            return !bat.isResting()
                    && bat.level().getMaxLocalRawBrightness(bat.blockPosition()) <= 3
                    && bat.level().getBlockState(above).isSolid()
                    && bat.random.nextInt(40) == 0;
        }

        @Override
        public void start() {
            bat.setDeltaMovement(Vec3.ZERO);
            bat.setResting(true);
        }
    }

    // ---------------------------------------------------------
    //         GOAL: Ambient Flight
    // ---------------------------------------------------------
    static class AmbientFlightGoal extends Goal {

        private final SculkBatEntity bat;

        public AmbientFlightGoal(SculkBatEntity bat) {
            this.bat = bat;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !bat.angry && !bat.isResting() && bat.random.nextInt(10) == 0;
        }

        @Override
        public void tick() {
            Vec3 v = new Vec3(
                    bat.random.nextDouble() - 0.5,
                    bat.random.nextDouble() - 0.2,
                    bat.random.nextDouble() - 0.5
            ).normalize().scale(0.22);

            bat.setDeltaMovement(v);
        }
    }

    // ---------------------------------------------------------
    //         GOAL: Attack When Provoked
    // ---------------------------------------------------------
    static class AttackBackGoal extends Goal {

        private final SculkBatEntity bat;

        public AttackBackGoal(SculkBatEntity bat) {
            this.bat = bat;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return bat.angry && !bat.isResting();
        }

        @Override
        public void tick() {
            Player target = bat.level().getNearestPlayer(bat, 8);
            if (target == null) return;

            Vec3 dir = new Vec3(
                    target.getX() - bat.getX(),
                    target.getEyeY() - bat.getEyeY(),
                    target.getZ() - bat.getZ()
            ).normalize().scale(0.4);

            bat.setDeltaMovement(dir);

            if (bat.distanceTo(target) < 1.2) {
                target.hurt(bat.damageSources().mobAttack(bat), 2.0F);
                target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0));
            }
        }
    }

    // ---------------------------------------------------------
    //         NeutralMob Anger Implementation
    // ---------------------------------------------------------
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTimer;
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.angerTimer = time;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null; // no specific target tracking needed
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
    }

    @Override
    public void startPersistentAngerTimer() {

    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {

        if (level.isClientSide) return;

        // --- Echo Dust (30%) ---
        if (this.random.nextFloat() < 0.30F) {
            this.spawnAtLocation(ModItems.ECHO_DUST.get(), 1);
        }

        // --- Sculk Fang (7%) ---
        if (this.random.nextFloat() < 0.07F) {
            this.spawnAtLocation(ModItems.SCULK_FANG.get(), 1);
        }

        // --- Phantom Membrane (12%) ---
        if (this.random.nextFloat() < 0.12F) {
            this.spawnAtLocation(Items.PHANTOM_MEMBRANE, 1);
        }
    }


}
