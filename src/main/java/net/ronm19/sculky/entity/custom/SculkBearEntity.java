package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class SculkBearEntity extends TamableAnimal {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(SculkBearEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> COMMAND_MODE =
            SynchedEntityData.defineId(SculkBearEntity.class, EntityDataSerializers.INT);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int attackAnimationTimeout = 0;

    public SculkBearEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
        builder.define(COMMAND_MODE, BearCommandMode.FOLLOW.getId());
    }

    public enum BearCommandMode {
        FOLLOW(0, "Follow"),
        STAY(1, "Stay"),
        ATTACK(2, "Attack");

        private final int id;
        private final String displayName;

        BearCommandMode(int id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public int getId() {
            return this.id;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public BearCommandMode next() {
            return switch (this) {
                case FOLLOW -> STAY;
                case STAY -> ATTACK;
                case ATTACK -> FOLLOW;
            };
        }

        public boolean shouldFollowOwner() {
            return this == FOLLOW || this == ATTACK;
        }

        public boolean allowsOwnerDefense() {
            return this == FOLLOW || this == ATTACK;
        }

        public static BearCommandMode byId(int id) {
            for (BearCommandMode mode : values()) {
                if (mode.id == id) {
                    return mode;
                }
            }

            return FOLLOW;
        }
    }

    public BearCommandMode getCommandMode() {
        return BearCommandMode.byId(this.entityData.get(COMMAND_MODE));
    }

    public void setCommandMode(BearCommandMode mode) {
        this.entityData.set(COMMAND_MODE, mode.getId());
    }

    public boolean isStaying() {
        return this.getCommandMode() == BearCommandMode.STAY;
    }

    public boolean isAttackCommanded() {
        return this.getCommandMode() == BearCommandMode.ATTACK;
    }

    private void cycleCommandMode(Player player) {
        BearCommandMode nextMode = this.getCommandMode().next();
        this.setCommandMode(nextMode);

        this.setTarget(null);
        this.navigation.stop();

        player.displayClientMessage(
                Component.literal("Sculk Bear: " + nextMode.getDisplayName()),
                true
        );
    }

    @Override
    public void addAdditionalSaveData( CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CommandMode", this.getCommandMode().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCommandMode(BearCommandMode.byId(compound.getInt("CommandMode")));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(2, new SculkBearAttackGoal(this, 1.15D));

        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));

        this.goalSelector.addGoal(4, new CommandFollowOwnerGoal(this, 1.05D, 8.0F, 3.0F));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0D));

        this.goalSelector.addGoal(6, new CommandRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new CommandOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CommandOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers(SculkBearEntity.class));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    public static AttributeSupplier.Builder createSculkBearAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 42.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.isVehicle()) {
            this.getNavigation().stop();
        }

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        if (!this.level().isClientSide() && this.isStaying()) {
            this.navigation.stop();

            if (this.getTarget() != null && this.distanceToSqr(this.getTarget()) > 6.0D) {
                this.setTarget(null);
            }
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking()) {
            if (this.attackAnimationTimeout <= 0) {
                this.attackAnimationTimeout = 18;
                this.attackAnimationState.start(this.tickCount);
            } else {
                --this.attackAnimationTimeout;
            }
        } else {
            this.attackAnimationState.stop();
            this.attackAnimationTimeout = 0;
        }
    }
    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // CLIENT-SIDE prediction.
        if (this.level().isClientSide()) {
            if (!this.isTame() && this.isTamingItem(stack)) {
                return InteractionResult.SUCCESS;
            }

            if (this.isTame() && this.isOwnedBy(player) && this.isBreedFood(stack)) {
                return InteractionResult.SUCCESS;
            }

            // Sneak + empty hand = command.
            if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty() && player.isShiftKeyDown()) {
                return InteractionResult.SUCCESS;
            }

            // Normal empty hand = ride.
            if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty() && !player.isShiftKeyDown()) {
                return InteractionResult.SUCCESS;
            }

            return super.mobInteract(player, hand);
        }

        // Taming.
        if (!this.isTame() && this.isTamingItem(stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (this.getRandom().nextInt(3) == 0) {
                this.tame(player);
                this.setTarget(null);
                this.navigation.stop();
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }

            return InteractionResult.SUCCESS;
        }

        // Healing / breeding.
        if (this.isTame() && this.isOwnedBy(player) && this.isBreedFood(stack)) {
            if (this.getHealth() < this.getMaxHealth()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                this.heal(6.0F);
                return InteractionResult.SUCCESS;
            }

            if (this.getAge() == 0 && !this.isInLove()) {
                this.usePlayerItem(player, hand, stack);
                this.setInLove(player);
                return InteractionResult.SUCCESS;
            }
        }

        // Sneak + empty hand = cycle command.
        if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty() && player.isShiftKeyDown()) {
            this.cycleCommandMode(player);
            return InteractionResult.SUCCESS;
        }

        // Normal empty hand = ride.
        if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty() && !player.isShiftKeyDown()) {
            return this.startRidingBear(player);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    private boolean isTamingItem(ItemStack stack) {
        return stack.is(ModItems.SCULK_CORE.get());
    }


    private boolean isBreedFood(ItemStack stack) {
        return stack.is(Items.ECHO_SHARD);
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);

        if (hurt) {
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 0.75F, 0.75F);

            if (target instanceof LivingEntity livingTarget) {
                double x = livingTarget.getX() - this.getX();
                double z = livingTarget.getZ() - this.getZ();
                livingTarget.knockback(0.45D, -x, -z);
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        target.getX(),
                        target.getY() + 0.6D,
                        target.getZ(),
                        8,
                        0.25D,
                        0.25D,
                        0.25D,
                        0.02D
                );
            }
        }

        return hurt;
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.isOwnedBy(target)) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        }

        if (this.isTame()) {
            LivingEntity owner = this.getOwner();

            if (entity == owner) {
                return true;
            }

            if (owner != null) {
                return owner.isAlliedTo(entity);
            }
        }

        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return this.isBreedFood(stack);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        SculkBearEntity baby = ModEntities.SCULK_BEAR.get().create(level);

        if (baby == null) {
            return null;
        }

        if (this.isTame()) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true, true);
        }

        return baby;
    }

    @Override
    public boolean canMate( @NotNull Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        }

        if (!(otherAnimal instanceof SculkBearEntity otherBear)) {
            return false;
        }

        if (!this.isTame() || !otherBear.isTame()) {
            return false;
        }

        return this.isInLove() && otherBear.isInLove();
    }

    private InteractionResult startRidingBear(Player player) {
        if (!this.level().isClientSide()) {
            this.setTarget(null);
            this.navigation.stop();

            // Force true helps avoid vanilla checks blocking the mount.
            player.startRiding(this, true);
        }

        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();

        if (passenger instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        return null;
    }


    public double getPassengersRidingOffset() {
        return 1.15D;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.isVehicle()) {
            LivingEntity rider = this.getControllingPassenger();

            if (rider != null) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();

                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());

                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;

                float strafe = rider.xxa * 0.45F;
                float forward = rider.zza;

                // Slow reverse movement so it feels heavy.
                if (forward <= 0.0F) {
                    forward *= 0.25F;
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(strafe, travelVector.y, forward));
                return;
            }
        }

        super.travel(travelVector);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound( @NotNull DamageSource damageSource) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.85F;
    }

    @Override
    public float getVoicePitch() {
        return 0.75F;
    }

    private static class SculkBearAttackGoal extends Goal {
        private final SculkBearEntity bear;
        private final double speedModifier;

        private LivingEntity target;
        private int attackTicks;
        private int cooldownTicks;
        private boolean windingUp;

        private static final int ATTACK_DURATION = 18;
        private static final int HIT_TICK = 11;
        private static final int COOLDOWN = 18;

        public SculkBearAttackGoal(SculkBearEntity bear, double speedModifier) {
            this.bear = bear;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.bear.isVehicle()) {
                return false;
            }

            if (this.cooldownTicks > 0) {
                --this.cooldownTicks;
                return false;
            }

            LivingEntity livingEntity = this.bear.getTarget();

            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }

            if (this.bear.isStaying()) {
                double distance = this.bear.distanceToSqr(livingEntity);
                double reach = this.getAttackReachSqr(livingEntity) + 2.0D;

                if (distance > reach) {
                    return false;
                }
            }

            this.target = livingEntity;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null
                    && this.target.isAlive()
                    && this.bear.distanceToSqr(this.target) <= 32.0D * 32.0D;
        }

        @Override
        public void stop() {
            this.bear.setAttacking(false);
            this.bear.getNavigation().stop();

            this.target = null;
            this.attackTicks = 0;
            this.windingUp = false;
        }

        @Override
        public void tick() {
            if (this.target == null) {
                return;
            }

            if (this.bear.isStaying()) {
                double distance = this.bear.distanceToSqr(this.target);
                double reach = this.getAttackReachSqr(this.target) + 2.0D;

                if (distance > reach) {
                    this.bear.getNavigation().stop();
                    this.bear.setTarget(null);
                    return;
                }
            }

            this.bear.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

            double distance = this.bear.distanceToSqr(this.target);
            double attackReach = this.getAttackReachSqr(this.target);

            if (!this.windingUp) {
                if (distance > attackReach) {
                    this.bear.getNavigation().moveTo(this.target, this.speedModifier);
                    return;
                }

                this.bear.getNavigation().stop();
                this.windingUp = true;
                this.attackTicks = 0;
                this.bear.setAttacking(true);
                return;
            }

            this.bear.getNavigation().stop();
            ++this.attackTicks;

            if (this.attackTicks == HIT_TICK && distance <= attackReach + 1.5D) {
                this.bear.doHurtTarget(this.target);
            }

            if (this.attackTicks >= ATTACK_DURATION) {
                this.bear.setAttacking(false);
                this.windingUp = false;
                this.attackTicks = 0;
                this.cooldownTicks = COOLDOWN;
            }
        }

        private double getAttackReachSqr(LivingEntity target) {
            double reach = this.bear.getBbWidth() * 2.4D + target.getBbWidth();
            return reach * reach;
        }
    }

    private static class CommandFollowOwnerGoal extends FollowOwnerGoal {
        private final SculkBearEntity bear;

        public CommandFollowOwnerGoal(SculkBearEntity bear, double speedModifier, float startDistance, float stopDistance) {
            super(bear, speedModifier, startDistance, stopDistance);
            this.bear = bear;
        }

        @Override
        public boolean canUse() {
            return this.bear.getCommandMode().shouldFollowOwner() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.bear.getCommandMode().shouldFollowOwner() && super.canContinueToUse();
        }
    }

    private static class CommandRandomStrollGoal extends WaterAvoidingRandomStrollGoal {
        private final SculkBearEntity bear;

        public CommandRandomStrollGoal(SculkBearEntity bear, double speedModifier) {
            super(bear, speedModifier);
            this.bear = bear;
        }

        @Override
        public boolean canUse() {
            return !this.bear.isTame() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.bear.isTame() && super.canContinueToUse();
        }
    }

    private static class CommandOwnerHurtByTargetGoal extends OwnerHurtByTargetGoal {
        private final SculkBearEntity bear;

        public CommandOwnerHurtByTargetGoal(SculkBearEntity bear) {
            super(bear);
            this.bear = bear;
        }

        @Override
        public boolean canUse() {
            return this.bear.getCommandMode().allowsOwnerDefense() && super.canUse();
        }
    }

    private static class CommandOwnerHurtTargetGoal extends OwnerHurtTargetGoal {
        private final SculkBearEntity bear;

        public CommandOwnerHurtTargetGoal(SculkBearEntity bear) {
            super(bear);
            this.bear = bear;
        }

        @Override
        public boolean canUse() {
            return this.bear.getCommandMode().allowsOwnerDefense() && super.canUse();
        }
    }
}