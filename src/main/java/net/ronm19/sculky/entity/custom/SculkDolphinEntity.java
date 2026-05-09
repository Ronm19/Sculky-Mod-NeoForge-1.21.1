package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class SculkDolphinEntity extends Dolphin implements OwnableEntity, PlayerRideableJumping {
    private static final double RIDDEN_FORWARD_SPEED = 0.48D;
    private static final double RIDDEN_BACKWARD_SPEED = 0.14D;
    private static final double RIDDEN_STRAFE_SPEED = 0.17D;

    private static final double RIDDEN_UP_SPEED = 0.14D;
    private static final double RIDDEN_DIVE_SPEED = 0.20D;

    private static final double RIDDEN_SMOOTHING = 0.70D;
    private static final double RIDDEN_IDLE_DRAG = 0.86D;
    private static final double RIDDEN_OUT_OF_WATER_DRAG = 0.92D;

    private float riderStrafeInput = 0.0F;
    private float riderForwardInput = 0.0F;
    private boolean riderJumpInput = false;
    private boolean riddenJumping = false;

    private static final EntityDataAccessor<Boolean> DATA_TAMED =
            SynchedEntityData.defineId(SculkDolphinEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DATA_ORDERED_TO_STAY =
            SynchedEntityData.defineId(SculkDolphinEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private UUID ownerUUID;

    public SculkDolphinEntity(EntityType<? extends Dolphin> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createSculkDolphinAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.35D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.10D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FollowOwnerInWaterGoal(this, 1.25D, 8.0F, 3.0F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TAMED, false);
        builder.define(DATA_ORDERED_TO_STAY, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.tickCount % 40 == 0) {
            this.giveOwnerSupportEffect();
        }

        if (!this.isVehicle()) {
            this.riddenJumping = false;
            this.riderStrafeInput = 0.0F;
            this.riderForwardInput = 0.0F;
            this.riderJumpInput = false;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()
                && this.isVehicle()
                && this.getControllingPassenger() instanceof Player player
                && this.isOwnedBy(player)) {
            this.applyStoredRiderMovement(player);
        }
    }

    private void applyStoredRiderMovement(Player player) {
        this.getNavigation().stop();

        this.setYRot(player.getYRot());
        this.yRotO = this.getYRot();

        this.setXRot(player.getXRot() * 0.5F);
        this.setRot(this.getYRot(), this.getXRot());

        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();

        boolean waterLike = this.isInWaterOrBubble() || this.isInWater();

        float forward = this.riderForwardInput;
        float strafe = this.riderStrafeInput;

        Vec3 wantedMovement = Vec3.ZERO;

        // IMPORTANT:
        // Use yaw-only movement so looking up does NOT make the dolphin fly upward.
        if (Math.abs(forward) > 0.001F) {
            double speed = forward > 0.0F ? RIDDEN_FORWARD_SPEED : RIDDEN_BACKWARD_SPEED;

            Vec3 horizontalForward = Vec3.directionFromRotation(0.0F, player.getYRot());
            double waterMultiplier = waterLike ? 1.0D : 0.55D;

            wantedMovement = wantedMovement.add(horizontalForward.scale(speed * forward * waterMultiplier));
        }

        if (Math.abs(strafe) > 0.001F) {
            Vec3 right = Vec3.directionFromRotation(0.0F, player.getYRot() + 90.0F);
            double waterMultiplier = waterLike ? 1.0D : 0.45D;

            wantedMovement = wantedMovement.add(right.scale(RIDDEN_STRAFE_SPEED * strafe * waterMultiplier));
        }

        // Space = swim upward.
        // Looking up does NOT make him rise anymore.
        if ((this.riderJumpInput || this.riddenJumping) && waterLike) {
            wantedMovement = wantedMovement.add(0.0D, RIDDEN_UP_SPEED, 0.0D);
        }

        // Look down + W = dive downward.
        // player.getXRot() is positive when looking down.
        if (forward > 0.0F && player.getXRot() > 15.0F) {
            double diveStrength = Math.min(1.0D, (player.getXRot() - 15.0F) / 45.0D);
            wantedMovement = wantedMovement.add(0.0D, -RIDDEN_DIVE_SPEED * diveStrength, 0.0D);
        }

        // Surface safety:
        // If he pops above water, gently pull him back down instead of freezing.
        if (!waterLike) {
            wantedMovement = wantedMovement.add(0.0D, -0.05D, 0.0D);
        }

        if (wantedMovement.lengthSqr() > 0.0D) {
            Vec3 smoothedMovement = this.getDeltaMovement().lerp(wantedMovement, RIDDEN_SMOOTHING);
            this.setDeltaMovement(smoothedMovement);
            this.move(MoverType.SELF, smoothedMovement);
        } else {
            double drag = waterLike ? RIDDEN_IDLE_DRAG : RIDDEN_OUT_OF_WATER_DRAG;
            Vec3 slowedMovement = this.getDeltaMovement().scale(drag);

            if (!waterLike) {
                slowedMovement = slowedMovement.add(0.0D, -0.04D, 0.0D);
            }

            this.setDeltaMovement(slowedMovement);
            this.move(MoverType.SELF, slowedMovement);
        }

        this.calculateEntityAnimation(false);
        this.hasImpulse = true;
    }

    private void giveOwnerSupportEffect() {
        if (!this.isTame()) {
            return;
        }

        if (this.getControllingPassenger() instanceof Player rider && this.isOwnedBy(rider)) {
            rider.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 60, 0, true, false, true));
            rider.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false, true));
            return;
        }

        LivingEntity owner = this.getOwner();

        if (owner instanceof Player player
                && this.distanceToSqr(player) <= 12.0D * 12.0D
                && player.isInWater()) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 60, 0, true, false, true));
        }
    }

    public boolean isTame() {
        return this.entityData.get(DATA_TAMED);
    }

    public void setTame(boolean tamed) {
        this.entityData.set(DATA_TAMED, tamed);
    }

    public boolean isOrderedToStay() {
        return this.entityData.get(DATA_ORDERED_TO_STAY);
    }

    public void setOrderedToStay(boolean orderedToStay) {
        this.entityData.set(DATA_ORDERED_TO_STAY, orderedToStay);
    }

    public void tame(Player player) {
        this.setTame(true);
        this.setOwnerUUID(player.getUUID());
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return entity instanceof Player player
                && this.ownerUUID != null
                && player.getUUID().equals(this.ownerUUID);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();

        if (uuid == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        if (serverLevel.getEntity(uuid) instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        return null;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide()) {
                if (player.isShiftKeyDown()) {
                    this.setOrderedToStay(!this.isOrderedToStay());

                    if (this.isOrderedToStay()) {
                        player.displayClientMessage(Component.literal("Sculk Dolphin will stay."), true);
                    } else {
                        player.displayClientMessage(Component.literal("Sculk Dolphin will follow."), true);
                    }
                } else {
                    this.setOrderedToStay(false);
                    player.setYRot(this.getYRot());
                    player.setXRot(this.getXRot());
                    player.startRiding(this);
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (!this.isTame() && itemStack.is(Items.ECHO_SHARD)) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    this.playSound(SoundEvents.DOLPHIN_AMBIENT, 1.0F, 1.2F);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                    this.playSound(SoundEvents.DOLPHIN_HURT, 0.7F, 1.5F);
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 7) {
            this.spawnTamingParticles(true);
        } else if (id == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnTamingParticles(boolean success) {
        ParticleOptions particle = success ? ParticleTypes.HEART : ParticleTypes.SMOKE;

        for (int i = 0; i < 7; ++i) {
            double xSpeed = this.random.nextGaussian() * 0.02D;
            double ySpeed = this.random.nextGaussian() * 0.02D;
            double zSpeed = this.random.nextGaussian() * 0.02D;

            this.level().addParticle(
                    particle,
                    this.getRandomX(1.0D),
                    this.getRandomY() + 0.5D,
                    this.getRandomZ(1.0D),
                    xSpeed,
                    ySpeed,
                    zSpeed
            );
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putBoolean("Tamed", this.isTame());
        tag.putBoolean("OrderedToStay", this.isOrderedToStay());

        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.setTame(tag.getBoolean("Tamed"));
        this.setOrderedToStay(tag.getBoolean("OrderedToStay"));

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    // ------------------------ RIDING ------------------------ //

    public void applyRiderInput(Player player, float strafeInput, float forwardInput, boolean jumpInput) {
        if (!this.isAlive()
                || !this.isVehicle()
                || !this.isOwnedBy(player)
                || this.getControllingPassenger() != player) {
            return;
        }

        this.setRiderInput(strafeInput, forwardInput, jumpInput);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty()
                && passenger instanceof Player player
                && this.isTame()
                && this.isOwnedBy(player);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();

        if (passenger instanceof Player player && this.isTame() && this.isOwnedBy(player)) {
            return player;
        }

        return null;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);

        this.getNavigation().stop();

        Vec2 riddenRotation = this.getRiddenRotation(player);
        this.setRot(riddenRotation.y, riddenRotation.x);

        this.yRotO = this.getYRot();
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();

        if (!this.isInWater()) {
            this.riddenJumping = false;
        }
    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    @Override
    public boolean canJump() {
        return this.isAlive()
                && this.isVehicle()
                && this.isInWater()
                && this.getControllingPassenger() instanceof Player player
                && this.isOwnedBy(player);
    }

    @Override
    public void handleStartJump(int jumpPower) {
        this.riddenJumping = true;
    }

    @Override
    public void handleStopJump() {
        this.riddenJumping = false;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        this.riddenJumping = true;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);

        if (passenger instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = this.yBodyRot;
        }
    }

    public void setRiderInput(float strafe, float forward, boolean jump) {
        this.riderStrafeInput = strafe;
        this.riderForwardInput = forward;
        this.riderJumpInput = jump;
    }

    private static class FollowOwnerInWaterGoal extends Goal {
        private final SculkDolphinEntity dolphin;
        private final double speedModifier;
        private final float startDistance;
        private final float stopDistance;

        @Nullable
        private LivingEntity owner;

        private int timeToRecalculatePath;

        public FollowOwnerInWaterGoal(SculkDolphinEntity dolphin, double speedModifier, float startDistance, float stopDistance) {
            this.dolphin = dolphin;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.dolphin.isTame()
                    || this.dolphin.isOrderedToStay()
                    || this.dolphin.isVehicle()) {
                return false;
            }

            LivingEntity owner = this.dolphin.getOwner();

            if (owner == null || owner.isSpectator()) {
                return false;
            }

            if (this.dolphin.distanceToSqr(owner) < this.startDistance * this.startDistance) {
                return false;
            }

            this.owner = owner;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.owner == null) {
                return false;
            }

            if (this.dolphin.isOrderedToStay() || this.dolphin.isVehicle()) {
                return false;
            }

            return this.dolphin.distanceToSqr(this.owner) > this.stopDistance * this.stopDistance;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.dolphin.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.owner == null) {
                return;
            }

            this.dolphin.getLookControl().setLookAt(this.owner, 10.0F, this.dolphin.getMaxHeadXRot());

            if (--this.timeToRecalculatePath <= 0) {
                this.timeToRecalculatePath = 10;
                this.dolphin.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }
}