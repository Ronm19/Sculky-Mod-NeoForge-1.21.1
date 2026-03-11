package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class InfestedEyeEntity extends TamableAnimal implements RangedAttackMob {

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(InfestedEyeEntity.class, EntityDataSerializers.BOOLEAN);

    // === Main tuning ===
    private static final double SONIC_BOOM_RANGE = 1000.0D;
    private static final double AUTO_TARGET_RANGE = 1000.0D; // set to 1000.0D for true madness
    private static final double GROUP_BUFF_RADIUS = 16.0D;

    private static final int BASE_SONIC_COOLDOWN = 36;
    private static final int ATTACK_WARMUP_TICKS = 12;
    private static final float BASE_SONIC_DAMAGE = 10.0F;
    private static final float BONUS_DAMAGE_PER_ALLY = 2.0F;
    private static final int BONUS_COOLDOWN_REDUCTION_PER_ALLY = 4;
    private static final int MAX_ALLY_BONUS = 5;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private int sonicCooldown;
    private int attackWarmupTicks;
    private Vec3 moveTargetPoint = Vec3.ZERO;

    @Nullable
    private LivingEntity pendingSonicTarget;

    public InfestedEyeEntity(EntityType<? extends InfestedEyeEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new InfestedEyeMoveControl(this);
        this.lookControl = new InfestedEyeLookControl(this);
        this.setNoGravity(true);
        this.xpReward = 8;
        this.moveTargetPoint = this.position();

        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createInfestedEyeAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FLYING_SPEED, 0.55D)
                .add(Attributes.FOLLOW_RANGE, AUTO_TARGET_RANGE)
                .add(Attributes.ATTACK_DAMAGE, BASE_SONIC_DAMAGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new InfestedEyeFollowOwnerGoal(this, 1.2D, 4.0F, 2.0F));
        this.goalSelector.addGoal(3, new InfestedEyeSonicBoomGoal(this));
        this.goalSelector.addGoal(4, new HoverAroundOwnerGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, false, mob -> mob instanceof Enemy && this.canAttackTarget(mob)));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            setupAnimationStates();
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

    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanPassDoors(true);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.sonicCooldown > 0) {
            this.sonicCooldown--;
        }

        if (!this.level().isClientSide) {
            this.setNoGravity(true);

            if (this.isOrderedToSit()) {
                this.moveTargetPoint = this.position();
                this.setDeltaMovement(this.getDeltaMovement().scale(0.6D));
            }

            // Owner-assisted long range targeting
            if (this.isTame() && !this.isOrderedToSit() && this.tickCount % 10 == 0) {
                LivingEntity ownerTarget = this.getOwnerPriorityTarget();
                if (this.canAttackTarget(ownerTarget)) {
                    this.setTarget(ownerTarget);
                }
            }

            // Optional autonomous scan
            if (!this.isOrderedToSit() && this.getTarget() == null && this.tickCount % 30 == 0) {
                LivingEntity scanned = this.findAutoTarget();
                if (this.canAttackTarget(scanned)) {
                    this.setTarget(scanned);
                }
            }
        }

        if (this.attackWarmupTicks > 0) {
            this.attackWarmupTicks--;

            if (this.attackWarmupTicks == 0) {
                this.entityData.set(ATTACKING, false);

                if (this.pendingSonicTarget != null && this.pendingSonicTarget.isAlive()) {
                    this.fireSonicBoom(this.pendingSonicTarget);
                }

                this.pendingSonicTarget = null;
            }
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) {
        // no fall damage
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.hasCustomName();
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.isTame();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.ECHO_SHARD);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.level().isClientSide) {
            boolean valid = this.isOwnedBy(player) || !this.isTame() && stack.is(Items.ECHO_SHARD) || this.isFood(stack);
            return valid ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        // Taming
        if (!this.isTame() && stack.is(Items.ECHO_SHARD)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.setInSittingPose(true);
                this.moveTargetPoint = this.position();
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }

            return InteractionResult.SUCCESS;
        }

        // Heal when tamed
        if (this.isTame() && this.isOwnedBy(player) && this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            this.heal(6.0F);
            return InteractionResult.SUCCESS;
        }

        // Sit / stand toggle
        if (this.isTame() && this.isOwnedBy(player) && !stack.is(Items.ECHO_SHARD)) {
            boolean sit = !this.isOrderedToSit();
            this.setOrderedToSit(sit);
            this.setInSittingPose(sit);
            this.navigation.stop();
            this.setTarget(null);
            if (sit) {
                this.moveTargetPoint = this.position();
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SonicCooldown", this.sonicCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.sonicCooldown = tag.getInt("SonicCooldown");
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // not breedable
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (!this.canAttackTarget(target)) {
            return;
        }

        if (this.sonicCooldown > 0 || this.attackWarmupTicks > 0) {
            return;
        }

        this.pendingSonicTarget = target;
        this.attackWarmupTicks = ATTACK_WARMUP_TICKS;
        this.entityData.set(ATTACKING, true);

        this.getLookControl().setLookAt(target, 180.0F, 180.0F);
        this.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 2.0F, 1.0F);
    }

    public boolean isChargingAttack() {
        return this.entityData.get(ATTACKING);
    }

    private void fireSonicBoom(LivingEntity mainTarget) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!this.canAttackTarget(mainTarget) && !this.isImmuneToInfestedEyeSonicBoom(mainTarget)) {
            return;
        }

        Vec3 from = this.getEyePosition();
        Vec3 to = mainTarget.getEyePosition();
        Vec3 diff = to.subtract(from);
        double length = diff.length();

        if (length <= 0.0001D || length > SONIC_BOOM_RANGE) {
            return;
        }

        Vec3 dir = diff.normalize();

        // Beam particles
        for (int i = 0; i < Mth.floor(length); i++) {
            Vec3 pos = from.add(dir.scale(i));
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);

        float damage = this.getScaledSonicDamage();

        // Main target gets full damage, unless it's a tamed Infested Eye
        if (this.canAttackTarget(mainTarget) && !this.isImmuneToInfestedEyeSonicBoom(mainTarget)) {
            mainTarget.hurt(this.damageSources().mobAttack(this), damage);
            Vec3 push = dir.scale(1.2D);
            mainTarget.setDeltaMovement(mainTarget.getDeltaMovement().add(push.x, 0.18D, push.z));
        }

        // Line splash for anything caught in the beam
        AABB beamBox = new AABB(from, to).inflate(1.5D);
        List<LivingEntity> victims = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                beamBox,
                entity -> entity != this && entity != mainTarget && entity.isAlive()
        );

        for (LivingEntity victim : victims) {
            if (this.isImmuneToInfestedEyeSonicBoom(victim)) {
                continue;
            }

            if (!this.canAttackTarget(victim)) {
                continue;
            }

            double distanceToLine = distanceToLineSegment(victim.getBoundingBox().getCenter(), from, to);
            if (distanceToLine <= 1.5D) {
                victim.hurt(this.damageSources().mobAttack(this), damage * 0.6F);
                Vec3 push = dir.scale(0.8D);
                victim.setDeltaMovement(victim.getDeltaMovement().add(push.x, 0.12D, push.z));
            }
        }

        this.sonicCooldown = this.getScaledSonicCooldown();
    }

    private float getScaledSonicDamage() {
        int allies = Math.min(this.countNearbyInfestedEyes() - 1, MAX_ALLY_BONUS);
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (allies * BONUS_DAMAGE_PER_ALLY);
    }

    private int getScaledSonicCooldown() {
        int allies = Math.min(this.countNearbyInfestedEyes() - 1, MAX_ALLY_BONUS);
        return Math.max(8, BASE_SONIC_COOLDOWN - (allies * BONUS_COOLDOWN_REDUCTION_PER_ALLY));
    }

    private int countNearbyInfestedEyes() {
        List<InfestedEyeEntity> nearby = this.level().getEntitiesOfClass(
                InfestedEyeEntity.class,
                this.getBoundingBox().inflate(GROUP_BUFF_RADIUS),
                eye -> eye.isAlive() && eye != this && this.isFriendlyEye(eye)
        );

        return nearby.size() + 1;
    }

    private boolean isFriendlyEye(InfestedEyeEntity other) {
        if (other == this) return true;

        if (this.isTame() != other.isTame()) {
            return false;
        }

        if (!this.isTame()) {
            return true;
        }

        LivingEntity myOwner = this.getOwner();
        LivingEntity otherOwner = other.getOwner();

        return myOwner != null && otherOwner != null && myOwner.getUUID().equals(otherOwner.getUUID());
    }

    @Nullable
    private LivingEntity getOwnerPriorityTarget() {
        LivingEntity owner = this.getOwner();
        if (owner == null) return null;

        LivingEntity target = owner.getLastHurtMob();
        if (this.canAttackTarget(target)) return target;

        target = owner.getLastHurtByMob();
        if (this.canAttackTarget(target)) return target;

        return null;
    }

    private boolean isProtectedInfestedEyeTarget(@Nullable LivingEntity target) {
        if (!(target instanceof InfestedEyeEntity other)) {
            return false;
        }

        if (other == this) {
            return true;
        }

        // Any two tamed Infested Eyes are friendly in multiplayer
        if (this.isTame() && other.isTame()) {
            return true;
        }

        // Extra safety for scoreboard/team alliance logic
        return this.isAlliedTo(other);
    }

    @Nullable
    private LivingEntity findAutoTarget() {
        AABB box = this.getBoundingBox().inflate(AUTO_TARGET_RANGE);

        List<Monster> mobs = this.level().getEntitiesOfClass(
                Monster.class,
                box,
                monster -> monster instanceof Enemy && this.canAttackTarget(monster)
        );

        LivingEntity closest = null;
        double bestDist = Double.MAX_VALUE;

        for (Monster monster : mobs) {
            double dist = this.distanceToSqr(monster);
            if (dist < bestDist) {
                bestDist = dist;
                closest = monster;
            }
        }

        return closest;
    }

    private boolean canAttackTarget(@Nullable LivingEntity target) {
        if (target == null) return false;
        if (!target.isAlive()) return false;
        if (target == this) return false;
        if (this.isProtectedInfestedEyeTarget(target)) return false;
        if (target == this.getOwner()) return false;
        if (this.isAlliedTo(target)) return false;
        if (this.distanceToSqr(target) > SONIC_BOOM_RANGE * SONIC_BOOM_RANGE) return false;
        return true;
    }

    private static double distanceToLineSegment(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 segment = end.subtract(start);
        Vec3 toPoint = point.subtract(start);

        double segmentLengthSqr = segment.lengthSqr();
        if (segmentLengthSqr <= 1.0E-7D) {
            return point.distanceTo(start);
        }

        double t = Mth.clamp(toPoint.dot(segment) / segmentLengthSqr, 0.0D, 1.0D);
        Vec3 projection = start.add(segment.scale(t));
        return point.distanceTo(projection);
    }

    private boolean isImmuneToInfestedEyeSonicBoom(@Nullable LivingEntity target) {
        return target instanceof InfestedEyeEntity eye && eye.isTame();
    }

    static class InfestedEyeLookControl extends LookControl {
        public InfestedEyeLookControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            // Intentionally empty, just like phantom.
            // Lets the movement logic dominate orientation.
        }
    }

    static class InfestedEyeMoveControl extends MoveControl {
        private final InfestedEyeEntity eye;
        private float speed = 0.1F;

        public InfestedEyeMoveControl(InfestedEyeEntity eye) {
            super(eye);
            this.eye = eye;
        }

        @Override
        public void tick() {
            if (this.eye.horizontalCollision) {
                this.eye.setYRot(this.eye.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = this.eye.moveTargetPoint.x - this.eye.getX();
            double d1 = this.eye.moveTargetPoint.y - this.eye.getY();
            double d2 = this.eye.moveTargetPoint.z - this.eye.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);

            if (Math.abs(d3) > 1.0E-5F) {
                double d4 = 1.0D - Math.abs(d1 * 0.7D) / d3;
                d0 *= d4;
                d2 *= d4;

                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);

                if (d5 < 1.0E-6D) {
                    return;
                }

                float oldYaw = this.eye.getYRot();
                float wantedYaw = (float) Mth.atan2(d2, d0);
                float wrappedYaw = Mth.wrapDegrees(this.eye.getYRot() + 90.0F);
                float targetYaw = Mth.wrapDegrees(wantedYaw * (180F / (float)Math.PI));

                this.eye.setYRot(Mth.approachDegrees(wrappedYaw, targetYaw, 4.0F) - 90.0F);
                this.eye.yBodyRot = this.eye.getYRot();

                if (Mth.degreesDifferenceAbs(oldYaw, this.eye.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.5F, 0.005F * (1.5F / Math.max(this.speed, 0.1F)));
                } else {
                    this.speed = Mth.approach(this.speed, 0.25F, 0.025F);
                }

                float pitch = (float)(-(Mth.atan2(-d1, d3) * 180.0D / Math.PI));
                this.eye.setXRot(pitch);

                float yawPlus90 = this.eye.getYRot() + 90.0F;
                double motionX = this.speed * Mth.cos(yawPlus90 * ((float)Math.PI / 180F)) * Math.abs(d0 / d5);
                double motionZ = this.speed * Mth.sin(yawPlus90 * ((float)Math.PI / 180F)) * Math.abs(d2 / d5);
                double motionY = this.speed * Mth.sin(pitch * ((float)Math.PI / 180F)) * Math.abs(d1 / d5);

                Vec3 vec3 = this.eye.getDeltaMovement();
                this.eye.setDeltaMovement(vec3.add((new Vec3(motionX, motionY, motionZ)).subtract(vec3).scale(0.2D)));
            }
        }
    }

    static class InfestedEyeFollowOwnerGoal extends Goal {
        private final InfestedEyeEntity eye;
        private final double speedModifier;
        private final float startDistance;
        private final float stopDistance;

        @Nullable
        private LivingEntity owner;

        public InfestedEyeFollowOwnerGoal(InfestedEyeEntity eye, double speedModifier, float startDistance, float stopDistance) {
            this.eye = eye;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.eye.isTame() || this.eye.isOrderedToSit()) {
                return false;
            }

            LivingEntity owner = this.eye.getOwner();
            if (owner == null) {
                return false;
            }

            if (owner instanceof Player player && player.isSpectator()) {
                return false;
            }

            if (this.eye.getTarget() != null) {
                return false;
            }

            if (this.eye.distanceToSqr(owner) < (double) (this.startDistance * this.startDistance)) {
                return false;
            }

            this.owner = owner;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.owner == null) return false;
            if (!this.owner.isAlive()) return false;
            if (this.eye.isOrderedToSit()) return false;
            if (this.eye.getTarget() != null) return false;
            return this.eye.distanceToSqr(this.owner) > (double) (this.stopDistance * this.stopDistance);
        }

        @Override
        public void stop() {
            this.owner = null;
        }

        @Override
        public void tick() {
            if (this.owner == null) return;

            this.eye.getLookControl().setLookAt(this.owner, 180.0F, 180.0F);

            double distSqr = this.eye.distanceToSqr(this.owner);

            // Teleport closer if the eye got way too far
            if (distSqr > 32.0D * 32.0D) {
                Vec3 tp = this.owner.position().add(
                        (this.eye.getRandom().nextDouble() - 0.5D) * 2.0D,
                        1.5D + this.eye.getRandom().nextDouble(),
                        (this.eye.getRandom().nextDouble() - 0.5D) * 2.0D
                );

                this.eye.moveTo(tp.x, tp.y, tp.z, this.eye.getYRot(), this.eye.getXRot());
                this.eye.moveTargetPoint = tp;
                this.eye.getNavigation().stop();
                return;
            }

            Vec3 hoverPos = this.owner.position().add(0.0D, this.owner.getBbHeight() + 1.2D, 0.0D);
            this.eye.moveTargetPoint = hoverPos;
        }
    }

    static class HoverAroundOwnerGoal extends Goal {
        private final InfestedEyeEntity eye;
        private int nextMoveTime;

        public HoverAroundOwnerGoal(InfestedEyeEntity eye) {
            this.eye = eye;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.eye.isOrderedToSit() && this.eye.getTarget() == null;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.eye.isOrderedToSit() && this.eye.getTarget() == null;
        }

        @Override
        public void tick() {
            if (this.nextMoveTime > 0) {
                this.nextMoveTime--;
                return;
            }

            this.nextMoveTime = 18 + this.eye.getRandom().nextInt(14);

            Vec3 center;
            LivingEntity owner = this.eye.getOwner();

            if (owner != null && this.eye.isTame()) {
                center = owner.position().add(0.0D, owner.getBbHeight() + 1.2D, 0.0D);
            } else {
                center = this.eye.position();
            }

            Vec3 next = center.add(
                    (this.eye.getRandom().nextDouble() - 0.5D) * 4.0D,
                    (this.eye.getRandom().nextDouble() - 0.25D) * 2.5D,
                    (this.eye.getRandom().nextDouble() - 0.5D) * 4.0D
            );

            this.eye.moveTargetPoint = next;
        }
    }

    static class InfestedEyeSonicBoomGoal extends Goal {
        private final InfestedEyeEntity eye;

        public InfestedEyeSonicBoomGoal(InfestedEyeEntity eye) {
            this.eye = eye;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.eye.getTarget();
            return target != null && this.eye.canAttackTarget(target) && !this.eye.isOrderedToSit();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.eye.getTarget();
            return target != null && target.isAlive() && this.eye.canAttackTarget(target) && !this.eye.isOrderedToSit();
        }

        @Override
        public void stop() {
            this.eye.moveTargetPoint = this.eye.position();
        }

        @Override
        public void tick() {
            LivingEntity target = this.eye.getTarget();
            if (target == null) return;

            this.eye.getLookControl().setLookAt(target, 180.0F, 180.0F);

            // Hover a little above target while fighting
            Vec3 hover = target.position().add(0.0D, target.getBbHeight() + 2.0D, 0.0D);
            this.eye.moveTargetPoint = hover;

            if (this.eye.sonicCooldown <= 0 && this.eye.attackWarmupTicks <= 0) {
                this.eye.performRangedAttack(target, 1.0F);
            }
        }
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SHRIEKER_PLACE;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SCULK_CATALYST_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SHRIEKER_SHRIEK;
    }

}