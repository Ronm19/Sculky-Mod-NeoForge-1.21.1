package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.command.RatCommandMode;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SculkRatEntity extends TamableAnimal {

    /* ===================== DATA ===================== */

    private static final EntityDataAccessor<Integer> DATA_COMMAND =
            SynchedEntityData.defineId(SculkRatEntity.class, EntityDataSerializers.INT);

    public static final String NBT_COMMAND = "SculkRatCommand";

    public static final double PACK_RADIUS = 12.0D;
    private int packAlertCooldown = 0;


    /* ===================== CONSTRUCTOR ===================== */

    public SculkRatEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);

        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 8.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 8.0F);
    }

    /* ===================== ATTRIBUTES ===================== */

    public static AttributeSupplier.Builder createSculkRatAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 65.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D)
                .add(Attributes.MOVEMENT_SPEED, 0.40D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 34.0D);
    }


    /* ===================== BREEDING ===================== */

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return ModEntities.SCULK_RAT.get().create(level);
    }

    /* ===================== SYNC / NBT ===================== */

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COMMAND, RatCommandMode.FOLLOW.id);
    }

    public RatCommandMode getCommand() {
        return RatCommandMode.byId(this.entityData.get(DATA_COMMAND));
    }

    /**
     * Always use this so behavior stays consistent.
     * This also hard-resets combat state so FOLLOW is always "reachable".
     */
    public void applyCommand(RatCommandMode mode) {
        this.entityData.set(DATA_COMMAND, mode.id);

        // Hard reset combat state so command switching never gets "stuck"
        this.setTarget(null);
        this.setAggressive(false);
        this.getNavigation().stop();

        if (mode == RatCommandMode.STAY) {
            this.setOrderedToSit(true);
        } else {
            this.setOrderedToSit(false);
        }
    }

    /**
     * Makes FOLLOW instantly visible (call this after applyCommand(FOLLOW)).
     * Does NOT re-apply command (avoids recursion / side effects).
     */
    public void forceFollowNow() {
        if (!this.isTame()) return;

        this.setOrderedToSit(false);
        this.setTarget(null);
        this.setAggressive(false);

        LivingEntity owner = this.getOwner();
        if (owner != null) {
            this.getNavigation().stop();
            this.getNavigation().moveTo(owner, 1.2D);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_COMMAND, this.entityData.get(DATA_COMMAND));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(NBT_COMMAND)) {
            this.entityData.set(DATA_COMMAND, tag.getInt(NBT_COMMAND));
        }

        // Keep sit state consistent
        if (getCommand() == RatCommandMode.STAY) {
            this.setOrderedToSit(true);
        }
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

    /* ===================== AI ===================== */

    @Override
    protected void registerGoals() {
        // Core
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        // Combat (single unified melee)
        this.goalSelector.addGoal(2, new ConditionalMeleeAttackGoal(this, 1.25D, true));

        // Movement (Follow has higher priority than Wander)
        this.goalSelector.addGoal(3, new CommandedFollowOwnerGoal(this, 1.15D, 3.0F, 12.0F));
        this.goalSelector.addGoal(4, new CommandedRandomStrollGoal(this, 1.0D));

        // Look
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Targets – tamed defense
        this.targetSelector.addGoal(1, new CommandedOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CommandedOwnerHurtTargetGoal(this));

        // Targets – Monsters ONLY when (wild OR kill on sight)
        this.targetSelector.addGoal(3, new ConditionalNearestMonsterGoal(this));

        // Retaliation (works for both; optional to gate if you want STAY to never retaliate)
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers(SculkRatEntity.class));
    }


    private boolean isWild() {
        return !this.isTame();
    }

    private boolean isFollowingMode() {
        return this.isTame() && this.getCommand() == RatCommandMode.FOLLOW && !this.isOrderedToSit();
    }

    private boolean isWanderingMode() {
        return this.isTame() && this.getCommand() == RatCommandMode.WANDER && !this.isOrderedToSit();
    }

    private boolean canDefendOwner() {
        // Only defend owner in FOLLOW stance (and not sitting)
        return isFollowingMode();
    }

    private boolean canBeCommandedToFight() {
        // If sitting, it shouldn't fight (unless your staff forces it up before setting target)
        return !this.isTame() || this.isOrderedToSit() || this.getCommand() == RatCommandMode.STAY;
    }

    public boolean isKillOnSight() {
        return this.isTame()
                && this.getCommand() == RatCommandMode.KILL_ON_SIGHT
                && !this.isOrderedToSit();
    }

    public boolean canWander() {
        return this.isTame()
                && (this.getCommand() == RatCommandMode.WANDER
                || this.getCommand() == RatCommandMode.KILL_ON_SIGHT)
                && !this.isOrderedToSit();
    }


    /* ===================== INTERACTION (TAMING ONLY) ===================== */

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Client prediction
        if (level().isClientSide) {
            if (stack.is(ModItems.SCULK_SHARD)) return InteractionResult.SUCCESS;
            return super.mobInteract(player, hand);
        }

        // Right-click taming with shard
        if (!this.isTame() && stack.is(ModItems.SCULK_SHARD)) {
            if (!player.getAbilities().instabuild) stack.shrink(1);

            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.applyCommand(RatCommandMode.FOLLOW);
                this.forceFollowNow();
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    /* ===================== Q-THROW TAMING ===================== */

    @Override
    public void aiStep() {
        super.aiStep();

        if (level().isClientSide || !this.isAlive()) return;

        // --------------------------------------------
        // 0) Cooldowns
        // --------------------------------------------
        if (packAlertCooldown > 0) packAlertCooldown--;

        // --------------------------------------------
        // 1) WILD taming by thrown SCULK_SHARD
        // --------------------------------------------
        if (!this.isTame()) {
            // scan for thrown shards near the rat
            List<ItemEntity> items = level().getEntitiesOfClass(
                    ItemEntity.class,
                    this.getBoundingBox().inflate(1.25D, 0.75D, 1.25D),
                    e -> e.isAlive() && !e.getItem().isEmpty() && e.getItem().is(ModItems.SCULK_SHARD)
            );

            if (!items.isEmpty()) {
                ItemEntity item = items.get(0);
                ItemStack stack = item.getItem();

                // consume exactly 1 shard
                stack.shrink(1);
                if (stack.isEmpty()) item.discard();
                else item.setItem(stack);

                // tame chance
                if (this.random.nextInt(3) == 0) { // ~33%
                    Player owner = level().getNearestPlayer(this, 5.0D);
                    if (owner != null) {
                        this.tame(owner);
                        this.applyCommand(RatCommandMode.FOLLOW);
                        this.forceFollowNow();
                        this.setTarget(null);
                        this.setAggressive(false);
                        this.level().broadcastEntityEvent(this, (byte) 7); // hearts
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6); // smoke
                    }
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6); // smoke
                }
            }

            // IMPORTANT: stop here. Wild rats don't do pack KOS logic.
            return;
        }

        // --------------------------------------------
        // 2) KILL ON SIGHT pack behavior (tamed only)
        // --------------------------------------------
        if (!this.isKillOnSight()) return;

        // only check every 10 ticks (0.5s) to reduce cost
        if (this.tickCount % 10 != 0) return;

        UUID ownerId = this.getOwnerUUID();
        if (ownerId == null) return;

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) return;

        // Gather nearby owned KOS rats
        List<SculkRatEntity> pack = this.level().getEntitiesOfClass(
                SculkRatEntity.class,
                this.getBoundingBox().inflate(10.0D),
                r -> r != this
                        && r.isAlive()
                        && r.isTame()
                        && ownerId.equals(r.getOwnerUUID())
                        && r.isKillOnSight()
                        && !r.isOrderedToSit()
        );

        if (pack.isEmpty()) return;

        // Share target with the pack (only if they don't already have one)
        for (SculkRatEntity r : pack) {
            if (r.getTarget() == null || !r.getTarget().isAlive()) {
                r.setTarget(target);
                r.setAggressive(true);
            }
        }

        // Optional: prevent spam (sound/particles/messages) with your cooldown
        if (packAlertCooldown == 0) {
            packAlertCooldown = 40; // 2 seconds
            // Example: play a subtle sound once (optional)
            this.level().playSound(null, this.blockPosition(), SoundEvents.SCULK_CLICKING, SoundSource.NEUTRAL, 0.6F, 1.1F);
        }
    }


    /* ===================== RECALL ===================== */

    public static void recallAllOwnedRats(ServerLevel level, Player owner) {
        UUID id = owner.getUUID();
        int radius = 96;

        BlockPos pos = owner.blockPosition();
        AABB box = new AABB(
                pos.getX() - radius, pos.getY() - 64, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + 64, pos.getZ() + radius
        );

        for (SculkRatEntity rat : level.getEntitiesOfClass(
                SculkRatEntity.class,
                box,
                r -> r.isAlive() && r.isTame() && id.equals(r.getOwnerUUID())
        )) {
            if (rat.distanceToSqr(owner) <= 16.0D) continue;

            double angle = level.random.nextDouble() * Math.PI * 2;
            rat.teleportTo(
                    owner.getX() + Math.cos(angle) * 1.5D,
                    owner.getY(),
                    owner.getZ() + Math.sin(angle) * 1.5D
            );
            rat.getNavigation().stop();

            // keep sitting if STAY
            if (rat.getCommand() == RatCommandMode.STAY) {
                rat.setOrderedToSit(true);
            }
        }
    }

    /* ===================== DAMAGE ===================== */

    @Override
    protected void actuallyHurt(@NotNull DamageSource source, float amount) {
        super.actuallyHurt(source, amount);

        // If you want STAY to break when hit, keep this.
        // If you want STAY to NEVER break, delete this block.
        if (this.getCommand() == RatCommandMode.STAY) {
            this.setOrderedToSit(false);
        }
    }

    public void orderAttack(LivingEntity target) {
        if (target == null || !target.isAlive()) return;

        this.setOrderedToSit(false);
        this.setTarget(target);
        this.setAggressive(true);

        if (this.getCommand() == RatCommandMode.KILL_ON_SIGHT) {
            alertPack(target);
        }

        this.getNavigation().stop();
        this.getNavigation().moveTo(target, 1.25D);
    }


    private void alertPack(LivingEntity target) {
        if (packAlertCooldown > 0) return;
        packAlertCooldown = 40; // 2 seconds

        if (!(this.level() instanceof ServerLevel sl)) return;

        List<SculkRatEntity> pack = sl.getEntitiesOfClass(
                SculkRatEntity.class,
                this.getBoundingBox().inflate(PACK_RADIUS),
                r -> r != this
                        && r.isAlive()
                        && r.isTame()
                        && r.getCommand() == RatCommandMode.KILL_ON_SIGHT
                        && !r.isOrderedToSit()
                        && this.getOwnerUUID() != null
                        && this.getOwnerUUID().equals(r.getOwnerUUID())
        );

        for (SculkRatEntity rat : pack) {
            rat.setOrderedToSit(false);
            rat.setTarget(target);
            rat.setAggressive(true);
        }
    }

    public boolean canAutoHuntMonsters() {
        // Wild rats always hunt
        if (!this.isTame()) return true;

        // Tamed rules
        if (this.isOrderedToSit()) return false;

        // Only KILL_ON_SIGHT does automatic hunting
        return this.getCommand() == RatCommandMode.KILL_ON_SIGHT;
    }

    public boolean canRetaliate() {
        // Wild rats always retaliate
        if (!this.isTame()) return true;

        // Tamed rats: only if not sitting and not STAY
        if (this.isOrderedToSit()) return false;
        return this.getCommand() != RatCommandMode.STAY;
    }




    /* ===================== GOALS ===================== */

    /**
     * Combat:
     * - Wild rats: attack if they have a target (targets come from wild target goals)
     * - Tamed rats: attack ONLY if they have a target AND they are not sitting/STAY
     * <p>
     * (Your staff can setTarget() to create a temporary attack order.)
     */
    private static class ConditionalMeleeAttackGoal extends MeleeAttackGoal {
        private final SculkRatEntity rat;

        public ConditionalMeleeAttackGoal(SculkRatEntity rat, double speed, boolean memory) {
            super(rat, speed, memory);
            this.rat = rat;
        }

        private boolean hasValidTarget() {
            LivingEntity t = rat.getTarget();
            if (t == null || !t.isAlive()) return true;
            // never attack owner
            if (rat.isTame() && rat.getOwner() != null && t == rat.getOwner()) return true;
            return false;
        }

        @Override
        public void start() {
            super.start();

            LivingEntity target = rat.getTarget();
            if (target != null && rat.getCommand() == RatCommandMode.KILL_ON_SIGHT) {
                rat.alertPack(target);
            }
        }


        @Override
        public boolean canUse() {
            if (hasValidTarget()) return false;

            if (rat.isWild()) {
                return super.canUse();
            }

            // tamed: only fight when allowed (not STAY / not sitting)
            if (rat.canBeCommandedToFight()) return false;

            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (hasValidTarget()) return false;

            if (rat.isWild()) {
                return super.canContinueToUse();
            }

            if (rat.canBeCommandedToFight()) return false;

            return super.canContinueToUse();
        }
    }

    private static class CommandedFollowOwnerGoal extends FollowOwnerGoal {
        private final SculkRatEntity rat;

        public CommandedFollowOwnerGoal(SculkRatEntity rat, double speed, float start, float stop) {
            super(rat, speed, start, stop);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            return rat.isFollowingMode() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return rat.isFollowingMode() && super.canContinueToUse();
        }
    }

    private static class CommandedRandomStrollGoal extends WaterAvoidingRandomStrollGoal {
        private final SculkRatEntity rat;

        public CommandedRandomStrollGoal(SculkRatEntity rat, double speed) {
            super(rat, speed);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            if (rat.isOrderedToSit()) return false;
            if (rat.getTarget() != null) return false; // optional but recommended
            return (rat.isWanderingMode() || rat.isKillOnSight()) && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (rat.isOrderedToSit()) return false;
            if (rat.getTarget() != null) return false; // optional but recommended
            return (rat.isWanderingMode() || rat.isKillOnSight()) && super.canContinueToUse();
        }
    }

    private static class CommandedOwnerHurtByTargetGoal extends OwnerHurtByTargetGoal {
        private final SculkRatEntity rat;

        public CommandedOwnerHurtByTargetGoal(SculkRatEntity rat) {
            super(rat);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            return rat.canDefendOwner() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return rat.canDefendOwner() && super.canContinueToUse();
        }
    }

    private static class CommandedOwnerHurtTargetGoal extends OwnerHurtTargetGoal {
        private final SculkRatEntity rat;

        public CommandedOwnerHurtTargetGoal(SculkRatEntity rat) {
            super(rat);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            return rat.canDefendOwner() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return rat.canDefendOwner() && super.canContinueToUse();
        }
    }

    private static class CommandedHurtByTargetGoal extends HurtByTargetGoal {
        private final SculkRatEntity rat;

        public CommandedHurtByTargetGoal(SculkRatEntity rat) {
            super(rat);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && rat.canRetaliate();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && rat.canRetaliate();
        }
    }

    private static class ConditionalNearestMonsterGoal
            extends NearestAttackableTargetGoal<Monster> {

        private final SculkRatEntity rat;

        public ConditionalNearestMonsterGoal(SculkRatEntity rat) {
            super(rat, Monster.class, true);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && (rat.isKillOnSight() || rat.isWild());
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && (rat.isKillOnSight() || rat.isWild());
        }
    }

    private static class WildOrKillOnSightNearestMonsterGoal extends NearestAttackableTargetGoal<Monster> {
        private final SculkRatEntity rat;

        public WildOrKillOnSightNearestMonsterGoal(SculkRatEntity rat) {
            super(rat, Monster.class, true);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && rat.canAutoHuntMonsters();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && rat.canAutoHuntMonsters();
        }
    }




    /* ===================== SOUNDS ===================== */

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
    }
}
