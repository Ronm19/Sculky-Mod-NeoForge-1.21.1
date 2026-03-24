package net.ronm19.sculky.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.ai.ShadowPantherAttackGoal;
import net.ronm19.sculky.entity.ai.ShadowPantherCircleGoal;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.sounds.ModSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShadowPantherEntity extends TamableAnimal {

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(ShadowPantherEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> STALKING =
            SynchedEntityData.defineId(ShadowPantherEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> CIRCLING =
            SynchedEntityData.defineId(ShadowPantherEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> COMMAND_STATE =
            SynchedEntityData.defineId(ShadowPantherEntity.class, EntityDataSerializers.INT);

    private static final byte EVENT_STALK = 10;
    private static final byte EVENT_PRESSURE = 11;
    private static final byte EVENT_ATTACK = 12;
    private static final byte EVENT_HEARTBEAT = 13;

    private static final SoundEvent WARDEN_HEARTBEAT_SOUND =
            SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "entity.warden.heartbeat")
            );

    private static final double NORMAL_SPEED = 0.32D;
    private static final double DARK_SPEED = 0.38D;

    private static final double STALK_MIN_DISTANCE = 4.0D;
    private static final double STALK_MAX_DISTANCE = 12.0D;

    private static final double POUNCE_MIN_DISTANCE = 3.0D;
    private static final double POUNCE_MAX_DISTANCE = 6.0D;

    private int idleAnimationTimeout = 0;
    private int attackAnimationTimeout = 0;
    private int attackTicks = 0;
    private int stalkTimer = 0;
    private int pounceCooldown = 0;

    private int stalkSoundCooldown = 0;
    private int pressureSoundCooldown = 0;
    private int heartbeatSoundCooldown = 0;
    private int fearEffectCooldown = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    public ShadowPantherEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    private enum PantherCommand {
        FOLLOW,
        HOLD,
        PATROL;

        public PantherCommand next() {
            return switch (this) {
                case FOLLOW -> HOLD;
                case HOLD -> PATROL;
                case PATROL -> FOLLOW;
            };
        }

        public static PantherCommand fromId(int id) {
            PantherCommand[] values = values();
            if (id < 0 || id >= values.length) {
                return FOLLOW;
            }
            return values[id];
        }
    }

    public static AttributeSupplier.Builder createShadowPantherAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 67.0D)
                .add(Attributes.MOVEMENT_SPEED, NORMAL_SPEED)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 45.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new ShadowPantherAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new ShadowPantherCircleGoal(this));

        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 6.0F, 2.0F) {
            @Override
            public boolean canUse() {
                return ShadowPantherEntity.this.getPantherCommand() == PantherCommand.FOLLOW && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return ShadowPantherEntity.this.getPantherCommand() == PantherCommand.FOLLOW && super.canContinueToUse();
            }
        });

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D) {
            @Override
            public boolean canUse() {
                return (!ShadowPantherEntity.this.isTame()
                        || ShadowPantherEntity.this.getPantherCommand() == PantherCommand.PATROL)
                        && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return (!ShadowPantherEntity.this.isTame()
                        || ShadowPantherEntity.this.getPantherCommand() == PantherCommand.PATROL)
                        && super.canContinueToUse();
            }
        });

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                target -> !this.isTame()
                        && target instanceof Player player
                        && !player.isCreative()
                        && !player.isSpectator()));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(5, new HurtByTargetGoal(this));
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        boolean diedByExplosion = damageSource.is(DamageTypeTags.IS_EXPLOSION);

        boolean diedByEnchantedSword = false;
        if (damageSource.getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            diedByEnchantedSword = weapon.getItem() instanceof SwordItem && weapon.isEnchanted();
        }

        if (diedByExplosion || diedByEnchantedSword) {
            this.spawnAtLocation(ModItems.SHADOW_PANTHER_THEME_MUSIC_DISC.get());
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract( Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.isTame() && stack.is(ModItems.SCULK_HEARTFRUIT.get())) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (!this.level().isClientSide) {
                if (this.random.nextFloat() < 0.35F) {
                    this.tame(player);
                    this.setOwnerUUID(player.getUUID());
                    this.setPantherCommand(PantherCommand.FOLLOW);
                    this.getNavigation().stop();
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isTame() && this.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND) {
            if (!this.level().isClientSide) {
                PantherCommand next = this.getPantherCommand().next();
                this.setPantherCommand(next);

                switch (next) {
                    case FOLLOW -> {
                        this.setTarget(null);
                        this.setStalking(false);
                        this.setCircling(false);
                        this.stalkTimer = 0;
                        player.sendSystemMessage(Component.literal("[Panther] Following"));
                    }
                    case HOLD -> {
                        this.getNavigation().stop();
                        this.setTarget(null);
                        this.setStalking(false);
                        this.setCircling(false);
                        this.stalkTimer = 0;
                        player.sendSystemMessage(Component.literal("[Panther] Holding Position"));
                    }
                    case PATROL -> {
                        this.getNavigation().stop();
                        this.setTarget(null);
                        this.setStalking(false);
                        this.setCircling(false);
                        this.stalkTimer = 0;
                        player.sendSystemMessage(Component.literal("[Panther] Patrolling"));
                    }
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isOrderedToSit() {
        return this.getPantherCommand() == PantherCommand.HOLD;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.setupAnimationStates();
            return;
        }

        if (this.attackTicks > 0 && --this.attackTicks <= 0) {
            this.setAttacking(false);
        }

        if (this.pounceCooldown > 0) {
            this.pounceCooldown--;
        }

        if (this.stalkSoundCooldown > 0) {
            this.stalkSoundCooldown--;
        }

        if (this.pressureSoundCooldown > 0) {
            this.pressureSoundCooldown--;
        }

        if (this.heartbeatSoundCooldown > 0) {
            this.heartbeatSoundCooldown--;
        }

        if (this.fearEffectCooldown > 0) {
            this.fearEffectCooldown--;
        }

        this.updateShadowSpeed();

        if (this.getPantherCommand() == PantherCommand.HOLD) {
            this.getNavigation().stop();
            this.setTarget(null);
            this.setStalking(false);
            this.setCircling(false);
            this.stalkTimer = 0;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide || this.getPantherCommand() == PantherCommand.HOLD) {
            return;
        }

        LivingEntity target = this.getTarget();

        if (target == null || !target.isAlive()) {
            this.setStalking(false);
            this.setCircling(false);
            this.stalkTimer = 0;
            return;
        }

        this.updateStalkingState(target);

        if (target instanceof Player player && this.distanceTo(player) <= 5.0D) {
            this.tryPlayHeartbeatSound();
            this.tryApplyFearEffect(player);
        }

        this.tryApplyStealthFlicker();
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && this.attackAnimationTimeout <= 0) {
            this.attackAnimationTimeout = 24;
            this.attackAnimationState.start(this.tickCount);
        } else if (this.attackAnimationTimeout > 0) {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            this.attackAnimationState.stop();
        }
    }

    private void updateShadowSpeed() {
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) == null) {
            return;
        }

        double wantedSpeed = this.isInDarkness() ? DARK_SPEED : NORMAL_SPEED;

        if (this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() != wantedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(wantedSpeed);
        }
    }

    private void updateStalkingState(LivingEntity target) {
        boolean wasStalking = this.isStalking();

        double distance = this.distanceTo(target);
        boolean hasLineOfSight = this.getSensing().hasLineOfSight(target);
        boolean darkEnough = this.isInDarkness();

        boolean stalkingNow = hasLineOfSight
                && darkEnough
                && distance >= STALK_MIN_DISTANCE
                && distance <= STALK_MAX_DISTANCE
                && distance > 5.0D
                && !this.isCircling()
                && !this.isAttacking();

        this.setStalking(stalkingNow);

        if (stalkingNow) {
            if (!wasStalking) {
                this.tryPlayStalkSound();
            }

            this.stalkTimer++;

            if (this.getNavigation().isDone()) {
                this.getNavigation().moveTo(target, 0.7D);
            }

            this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.tryPounce(target, distance);
        } else {
            this.stalkTimer = 0;
        }
    }

    private void tryPounce(LivingEntity target, double distance) {
        if (this.pounceCooldown > 0) return;
        if (!this.onGround()) return;
        if (!this.getSensing().hasLineOfSight(target)) return;
        if (distance < POUNCE_MIN_DISTANCE || distance > POUNCE_MAX_DISTANCE) return;
        if (this.stalkTimer < 20) return;
        if (this.random.nextFloat() > 0.08F) return;

        Vec3 jump = new Vec3(
                target.getX() - this.getX(),
                0.0D,
                target.getZ() - this.getZ()
        ).normalize().scale(0.95D);

        this.setDeltaMovement(jump.x, 0.42D, jump.z);
        this.startAttackAnimation();
        this.pounceCooldown = 60;
    }

    private void tryPlayStalkSound() {
        if (this.stalkSoundCooldown > 0) {
            return;
        }

        this.level().broadcastEntityEvent(this, EVENT_STALK);
        this.stalkSoundCooldown = 220;
    }

    public void tryPlayPressureSound() {
        if (this.pressureSoundCooldown > 0) {
            return;
        }

        this.level().broadcastEntityEvent(this, EVENT_PRESSURE);
        this.pressureSoundCooldown = 60;
    }

    private void tryPlayHeartbeatSound() {
        if (this.heartbeatSoundCooldown > 0) {
            return;
        }

        this.level().broadcastEntityEvent(this, EVENT_HEARTBEAT);
        this.heartbeatSoundCooldown = 30;
    }

    private void tryApplyFearEffect(Player player) {
        if (this.fearEffectCooldown > 0) {
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 30, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0, false, false));

        player.push(
                (this.random.nextDouble() - 0.5D) * 0.04D,
                0.0D,
                (this.random.nextDouble() - 0.5D) * 0.04D
        );

        this.fearEffectCooldown = 40;
    }

    private void tryApplyStealthFlicker() {
        if (!this.isStalking() || !this.isInDarkness()) {
            return;
        }

        if (this.tickCount % 20 == 0) {
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30, 0, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30, 0, false, false));
        }
    }

    public boolean isInDarkness() {
        return this.level().getMaxLocalRawBrightness(this.blockPosition()) < 7;
    }

    public float getEyeGlowStrength() {
        int light = this.level().getMaxLocalRawBrightness(this.blockPosition());
        return 1.0F - (light / 15.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
        builder.define(STALKING, false);
        builder.define(CIRCLING, false);
        builder.define(COMMAND_STATE, PantherCommand.FOLLOW.ordinal());
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);

        if (!this.level().isClientSide) {
            return;
        }

        switch (id) {
            case EVENT_STALK -> this.playSound(ModSounds.SHADOW_PANTHER_STALK.get(), 0.6F, 1.0F);
            case EVENT_PRESSURE -> this.playSound(ModSounds.SHADOW_PANTHER_PRESSURE.get(), 0.8F, 1.0F);
            case EVENT_ATTACK -> this.playSound(ModSounds.SHADOW_PANTHER_ATTACK_STINGER.get(), 1.2F, 1.0F);
            case EVENT_HEARTBEAT -> this.playSound(WARDEN_HEARTBEAT_SOUND, 0.7F, 1.0F);
        }
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isStalking() {
        return this.entityData.get(STALKING);
    }

    public void setStalking(boolean stalking) {
        this.entityData.set(STALKING, stalking);
    }

    public boolean isCircling() {
        return this.entityData.get(CIRCLING);
    }

    public void setCircling(boolean circling) {
        this.entityData.set(CIRCLING, circling);
    }

    private PantherCommand getPantherCommand() {
        return PantherCommand.fromId(this.entityData.get(COMMAND_STATE));
    }

    private void setPantherCommand(PantherCommand command) {
        this.entityData.set(COMMAND_STATE, command.ordinal());
    }

    public void startAttackAnimation() {
        boolean wasAttacking = this.isAttacking();

        this.attackTicks = 12;
        this.setStalking(false);
        this.setCircling(false);
        this.setAttacking(true);

        if (!wasAttacking) {
            this.level().broadcastEntityEvent(this, EVENT_ATTACK);
        }
    }

    public int getStalkTimer() {
        return this.stalkTimer;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return super.doHurtTarget(target);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.SCULK_APPLE.get());
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SHADOW_PANTHER_AMBIENT.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SHADOW_PANTHER_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return ModSounds.SHADOW_PANTHER_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("PantherCommand", this.getPantherCommand().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("PantherCommand")) {
            try {
                this.setPantherCommand(PantherCommand.valueOf(tag.getString("PantherCommand")));
            } catch (IllegalArgumentException ignored) {
                this.setPantherCommand(PantherCommand.FOLLOW);
            }
        } else {
            this.setPantherCommand(PantherCommand.FOLLOW);
        }
    }
}