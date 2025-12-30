package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.event.EventHooks;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class SculkTailEntity extends Animal {

    private static final int MAX_ANGER_TIME = 20 * 15;
    private int angerTime = 0;

    public SculkTailEntity( EntityType<? extends Animal> type, Level level ) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 16.0F);
    }

    /* ---------------- ATTRIBUTES ---------------- */

    public static AttributeSupplier.Builder createSculkTailAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 19.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    /* ---------------- AI GOALS ---------------- */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));

        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true) {
            @Override
            public boolean canUse() {
                return isAgitated() && super.canUse();
            }
        });

        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
    }

    /* ---------------- BEHAVIOR ---------------- */

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        // Darkness preference
        if (this.level().getMaxLocalRawBrightness(this.blockPosition()) > 7 && !isAgitated()) {
            this.getNavigation().stop();
        }

        // Movement particles
        if (this.getDeltaMovement().horizontalDistanceSqr() > 0.002D && this.tickCount % 5 == 0) {
            spawnMovementParticles();
        }

        // Danger sensing
        if (sensesDanger()) {
            this.getNavigation().stop();
            this.setYRot(this.getYRot() + this.random.nextFloat() * 4F - 2F);

            if (this.tickCount % 20 == 0) {
                ((ServerLevel) this.level()).sendParticles(
                        new SculkChargeParticleOptions(0.0F),
                        this.getX(), this.getY() + 0.3D, this.getZ(),
                        2, 0.1D, 0.05D, 0.1D, 0.0D
                );
            }
        }

        // Anger decay
        if (isAgitated()) {
            calmDown(sensesDanger() ? 1 : 2);
        }

        // Agitated feedback
        if (isAgitated() && this.tickCount % 10 == 0) {
            ((ServerLevel) this.level()).sendParticles(
                    ParticleTypes.SCULK_CHARGE_POP,
                    this.getX(), this.getY() + 0.25D, this.getZ(),
                    2, 0.1D, 0.05D, 0.1D, 0.0D
            );
        }
    }

    /* ---------------- SOUNDS ---------------- */

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SENSOR_BREAK;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource source ) {
        return SoundEvents.SCULK_BLOCK_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    /* ---------------- BREEDING ---------------- */

    @Nullable
    @Override
    public AgeableMob getBreedOffspring( ServerLevel level, AgeableMob partner ) {
        return ModEntities.SCULK_TAIL.get().create(level);
    }

    @Override
    public boolean isFood( ItemStack stack ) {
        return stack.is(ModItems.SCULK_RESONANCE); // Not breedable for now
    }

    private boolean isAngry() {
        return false;
    }


    /* ---------------- MISC ---------------- */

    @Override
    protected void playStepSound( BlockPos pos, BlockState state) {
        // Silent movement
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    private boolean sensesDanger() {
        return !this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(6.0D),
                e -> e instanceof Monster || (e instanceof Player p && p.isSprinting())
        ).isEmpty();
    }

    private void spawnMovementParticles() {
        if (!(this.level() instanceof ServerLevel level)) return;

        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 0.05D,
                this.getZ(),
                1,
                0.08D,
                0.01D,
                0.08D,
                0.0D
        );
    }

    public boolean isAgitated() {
        return angerTime > 0;
    }

    private void increaseAnger(int amount) {
        this.angerTime = Math.min(MAX_ANGER_TIME, this.angerTime + amount);
    }

    private void calmDown(int amount) {
        this.angerTime = Math.max(0, this.angerTime - amount);
    }

    @Override
    public boolean hurt( DamageSource source, float amount ) {
        boolean result = super.hurt( source, amount );

        if (!this.level().isClientSide && result) {
            increaseAnger(20 * 6);
        }

        return result;
    }
}
