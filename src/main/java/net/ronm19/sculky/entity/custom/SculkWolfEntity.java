package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.EventHooks;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class SculkWolfEntity extends Wolf implements NeutralMob {

    // ============================================================
    //                         MODES
    // ============================================================

    public static final int MODE_FOLLOW = 0;
    public static final int MODE_GUARD  = 1;
    public static final int MODE_PATROL = 2;
    public static final int MODE_STAY   = 3;

    // ============================================================
    //                   SYNCHED ENTITY DATA
    // ============================================================

    private static final EntityDataAccessor<Integer> DATA_MODE =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> DATA_CLOAKED =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ANGER_TIME =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.INT);

    // ============================================================
    //                          CONSTANTS
    // ============================================================

    private static final String NBT_ARMOR = "ArmorItem";
    private static final String NBT_PATROL_CENTER = "PatrolCenter";

    private static final float UNTAMED_HEALTH = 50.0F;
    private static final float TAMED_HEALTH = 90.0F;

    private static final int HOWL_COOLDOWN_TICKS = 240;
    private static final int DASH_COOLDOWN_TICKS = 60;

    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);

    // ============================================================
    //                         VARIABLES
    // ============================================================

    @Nullable
    private UUID angerTarget;

    @Nullable
    private BlockPos patrolCenter;

    private int howlCooldown;
    private int dashCooldown;

    // Wet / shake
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;

    // Head tilt
    private float interestedAngle;
    private float interestedAngleO;

    // ============================================================
    //                        CONSTRUCTOR
    // ============================================================

    public SculkWolfEntity(EntityType<? extends SculkWolfEntity> type, Level level) {
        super(type, level);
        this.setTame(false, true);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    // ============================================================
    //                         ATTRIBUTES
    // ============================================================

    public static AttributeSupplier.Builder createsSculkWolfAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, UNTAMED_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D);
    }

    // ============================================================
    //                     DEFINE SYNCHED DATA
    // ============================================================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MODE, MODE_FOLLOW);
        builder.define(DATA_CLOAKED, false);
        builder.define(DATA_COLLAR_COLOR, DyeColor.CYAN.getId());
        builder.define(DATA_ANGER_TIME, 0);
    }

    // ============================================================
    //                           GOALS
    // ============================================================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true) {
            @Override
            public boolean canUse() {
                return !SculkWolfEntity.this.isOrderedToSit() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !SculkWolfEntity.this.isOrderedToSit() && super.canContinueToUse();
            }
        });

        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F) {
            @Override
            public boolean canUse() {
                return !SculkWolfEntity.this.isOrderedToSit() && super.canUse();
            }
        });

        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F) {
            @Override
            public boolean canUse() {
                return SculkWolfEntity.this.isTame()
                        && SculkWolfEntity.this.getMode() == MODE_FOLLOW
                        && !SculkWolfEntity.this.isOrderedToSit()
                        && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return SculkWolfEntity.this.isTame()
                        && SculkWolfEntity.this.getMode() == MODE_FOLLOW
                        && !SculkWolfEntity.this.isOrderedToSit()
                        && super.canContinueToUse();
            }
        });

        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !SculkWolfEntity.this.isOrderedToSit()
                        && SculkWolfEntity.this.getMode() != MODE_STAY
                        && super.canUse();
            }
        });

        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(6, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    // ============================================================
    //                         MAIN TICK
    // ============================================================

    @Override
    public void tick() {
        super.tick();

        if (this.howlCooldown > 0) this.howlCooldown--;
        if (this.dashCooldown > 0) this.dashCooldown--;

        this.handleModeBehavior();
        this.handleCloak();
        this.handleEchoSense();
        this.handlePackLink();
        this.handleAlphaFollow();
        this.handleDashAttack();

        this.interestedAngleO = this.interestedAngle;
        this.interestedAngle += (this.shouldLookInterested() ? 1.0F : -1.0F) * 0.4F;
        this.interestedAngle = Mth.clamp(this.interestedAngle, 0.0F, 1.0F);

        if (this.isInWaterRainOrBubble()) {
            this.isWet = true;
            if (this.isShaking) {
                this.level().broadcastEntityEvent(this, (byte) 56);
                this.cancelShake();
            }
        } else if (this.isWet && this.isShaking) {
            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05F;

            if (this.shakeAnimO >= 2.0F) {
                this.isWet = false;
                this.isShaking = false;
                this.shakeAnimO = 0.0F;
                this.shakeAnim = 0.0F;
            }

            if (this.shakeAnim > 0.4F) {
                int count = (int) (Mth.sin((this.shakeAnim - 0.4F) * (float) Math.PI) * 7.0F);
                Vec3 motion = this.getDeltaMovement();

                for (int i = 0; i < count; ++i) {
                    double x = this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth();
                    double y = this.getY() + 0.8D;
                    double z = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth();
                    this.level().addParticle(ParticleTypes.SPLASH, x, y, z, motion.x, motion.y, motion.z);
                }
            }
        }
    }

    // ============================================================
    //                      MODE SYSTEM
    // ============================================================

    public int getMode() {
        return this.entityData.get(DATA_MODE);
    }

    public void setMode(int mode) {
        this.entityData.set(DATA_MODE, Mth.clamp(mode, MODE_FOLLOW, MODE_STAY));

        if (mode == MODE_PATROL && this.patrolCenter == null) {
            this.patrolCenter = this.blockPosition();
        }
    }

    public void cycleMode() {
        int next = this.getMode() + 1;
        if (next > MODE_STAY) next = MODE_FOLLOW;
        this.setMode(next);
    }

    public String getModeName() {
        return switch (this.getMode()) {
            case MODE_GUARD -> "Guard";
            case MODE_PATROL -> "Patrol";
            case MODE_STAY -> "Stay";
            default -> "Follow";
        };
    }

    private void handleModeBehavior() {
        if (this.level().isClientSide || !this.isTame()) return;

        if (this.isOrderedToSit()) {
            this.getNavigation().stop();
            return;
        }

        if (this.getMode() == MODE_STAY) {
            this.getNavigation().stop();
            return;
        }

        if (this.getMode() == MODE_GUARD) {
            LivingEntity owner = this.getOwner();
            if (owner != null) {
                double distSqr = this.distanceToSqr(owner);
                if (distSqr > 100.0D) {
                    this.getNavigation().moveTo(owner, 1.1D);
                }
            }
        }

        if (this.getMode() == MODE_PATROL && this.patrolCenter != null) {
            double distToCenter = this.distanceToSqr(
                    this.patrolCenter.getX() + 0.5D,
                    this.patrolCenter.getY(),
                    this.patrolCenter.getZ() + 0.5D
            );

            if (distToCenter > 144.0D) {
                this.getNavigation().moveTo(
                        this.patrolCenter.getX() + 0.5D,
                        this.patrolCenter.getY(),
                        this.patrolCenter.getZ() + 0.5D,
                        1.0D
                );
            }
        }
    }

    // ============================================================
    //                     UNTAMED ALPHA FOLLOW
    // ============================================================

    private void handleAlphaFollow() {
        if (this.level().isClientSide || this.isTame() || this.isOrderedToSit()) return;
        if (this.getTarget() != null) return;

        SculkWolfAlphaEntity alpha = this.level().getNearestEntity(
                SculkWolfAlphaEntity.class,
                TargetingConditions.forNonCombat(),
                this,
                this.getX(), this.getY(), this.getZ(),
                this.getBoundingBox().inflate(12.0D)
        );

        if (alpha != null) {
            this.followAlpha(alpha);
        }
    }

    private void followAlpha(SculkWolfAlphaEntity alpha) {
        double distance = this.distanceTo(alpha);

        if (distance > 3.0D) {
            this.getNavigation().moveTo(alpha, 1.2D);
        }

        LivingEntity attacker = alpha.getLastHurtByMob();
        if (attacker != null && attacker.isAlive()) {
            this.setTarget(attacker);
        }
    }

    // ============================================================
    //                     INTEREST / HEAD TILT
    // ============================================================

    private boolean shouldLookInterested() {
        Player player = this.level().getNearestPlayer(this, 8.0D);
        if (player == null) return false;

        return this.isInterestItem(player.getMainHandItem()) || this.isInterestItem(player.getOffhandItem());
    }

    private boolean isInterestItem(ItemStack stack) {
        return stack.is(ModItems.SCULK_BONE) || this.isFood(stack);
    }

    public boolean isInterested() {
        return this.interestedAngle > 0.0F;
    }

    // ============================================================
    //                     ABILITY: SCULK HOWL
    // ============================================================

    public void tryHowl() {
        if (this.howlCooldown > 0) return;

        this.howlCooldown = HOWL_COOLDOWN_TICKS;

        this.level().getEntitiesOfClass(
                Monster.class,
                this.getBoundingBox().inflate(24.0D),
                monster -> monster.isAlive()
        ).forEach(monster -> monster.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100)));

        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 0, false, true));

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_CHARGE_POP,
                    this.getX(), this.getY() + 1.0D, this.getZ(),
                    20, 1.0D, 0.3D, 1.0D, 0.01D
            );
        }

        this.playSound(SoundEvents.WARDEN_ROAR, 1.0F, 1.4F);
    }

    // ============================================================
    //                    ABILITY: SCULK CLOAK
    // ============================================================

    public boolean isCloaked() {
        return this.entityData.get(DATA_CLOAKED);
    }

    private void setCloakedFlag(boolean cloaked) {
        this.entityData.set(DATA_CLOAKED, cloaked);
    }

    private void handleCloak() {
        boolean dark = this.level().getMaxLocalRawBrightness(this.blockPosition()) <= 4;
        boolean shouldCloak = dark && !this.isInSittingPose() && this.getTarget() == null && !this.isAngry();
        this.setCloakedFlag(shouldCloak);
    }

    // ============================================================
    //                    ABILITY: ECHO SENSE
    // ============================================================

    private void handleEchoSense() {
        if (this.level().isClientSide) return;

        boolean detected = !this.level().getEntitiesOfClass(
                Monster.class,
                this.getBoundingBox().inflate(12.0D)
        ).isEmpty();

        if (detected && this.random.nextInt(40) == 0) {
            this.playSound(SoundEvents.SCULK_SENSOR_HIT, 0.6F, 1.8F);
        }
    }

    // ============================================================
    //                    ABILITY: PACK LINK
    // ============================================================

    private void handlePackLink() {
        if (this.level().isClientSide || !this.isTame()) return;

        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        this.level().getEntitiesOfClass(
                SculkWolfEntity.class,
                this.getBoundingBox().inflate(12.0D),
                wolf -> wolf != this && wolf.isTame() && wolf.getOwner() == owner
        ).forEach(wolf -> {
            wolf.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 0, false, false));
            wolf.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 0, false, false));
        });
    }

    // ============================================================
    //                    ABILITY: DASH ATTACK
    // ============================================================

    private void handleDashAttack() {
        if (this.level().isClientSide || this.dashCooldown > 0 || this.isOrderedToSit()) return;

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) return;

        double distance = this.distanceTo(target);
        if (distance >= 4.0D && distance <= 8.0D && this.onGround() && this.random.nextInt(40) == 0) {
            this.tryDashAttack(target);
        }
    }

    public void tryDashAttack(LivingEntity target) {
        if (this.dashCooldown > 0) return;

        this.dashCooldown = DASH_COOLDOWN_TICKS;

        Vec3 dir = new Vec3(
                target.getX() - this.getX(),
                target.getY() - this.getY() + 0.2D,
                target.getZ() - this.getZ()
        ).normalize().scale(1.4D);

        this.setDeltaMovement(dir);
        this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 1.0F, 1.3F);
    }

    // ============================================================
    //                    PLAYER INTERACTION
    // ============================================================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        if (this.level().isClientSide) {
            boolean flag = this.isOwnedBy(player)
                    || this.isTame()
                    || (!this.isTame() && !this.isAngry() && stack.is(ModItems.SCULK_BONE));
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        // Shift + empty hand:
        // - if in combat and howl ready -> howl
        // - otherwise cycle modes if owner
        if (player.isShiftKeyDown() && stack.isEmpty() && this.isOwnedBy(player)) {
            if (this.getTarget() != null && this.howlCooldown == 0) {
                this.tryHowl();
                return InteractionResult.SUCCESS;
            }

            this.cycleMode();

            if (this.getMode() == MODE_PATROL) {
                this.patrolCenter = this.blockPosition();
            }

            this.getNavigation().stop();
            player.displayClientMessage(Component.literal("Sculk Wolf Mode: " + this.getModeName()), true);
            return InteractionResult.SUCCESS;
        }

        if (this.isTame()) {
            // Healing
            if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
                FoodProperties food = stack.getFoodProperties(this);
                float nutrition = food != null ? food.nutrition() : 1.0F;
                this.heal(2.0F * nutrition);
                stack.consume(1, player);
                this.gameEvent(GameEvent.EAT);
                return InteractionResult.SUCCESS;
            }

            // Collar dye
            if (item instanceof DyeItem dyeItem && this.isOwnedBy(player)) {
                DyeColor color = dyeItem.getDyeColor();
                if (color != this.getCollarColor()) {
                    this.setCollarColor(color);
                    stack.consume(1, player);
                    return InteractionResult.SUCCESS;
                }
            }

            // Equip armor
            if (stack.is(Items.WOLF_ARMOR) && this.isOwnedBy(player) && !this.hasArmor() && !this.isBaby()) {
                this.setBodyArmorItem(stack.copyWithCount(1));
                stack.consume(1, player);
                this.playSound(SoundEvents.ARMOR_EQUIP_WOLF.value());
                return InteractionResult.SUCCESS;
            }

            // Remove armor with shears
            if (stack.canPerformAction(ItemAbilities.SHEARS_REMOVE_ARMOR)
                    && this.isOwnedBy(player)
                    && this.hasArmor()
                    && !(EnchantmentHelper.has(this.getBodyArmorItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) && !player.isCreative())) {

                stack.hurtAndBreak(1, player, getSlotForHand(hand));
                this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
                ItemStack armor = this.getBodyArmorItem();
                this.setBodyArmorItem(ItemStack.EMPTY);
                this.spawnAtLocation(armor);
                return InteractionResult.SUCCESS;
            }

            // Repair armor with scute
            if (this.isOwnedBy(player)
                    && this.isInSittingPose()
                    && this.hasArmor()
                    && this.getBodyArmorItem().isDamaged()
                    && stack.is(Items.ARMADILLO_SCUTE)) {

                stack.consume(1, player);
                this.playSound(SoundEvents.WOLF_ARMOR_REPAIR);

                ItemStack armor = this.getBodyArmorItem();
                int repairAmount = (int) (armor.getMaxDamage() * 0.125F);
                armor.setDamageValue(Math.max(0, armor.getDamageValue() - repairAmount));
                return InteractionResult.SUCCESS;
            }

            // Default owner interaction: toggle sit with empty hand
            InteractionResult result = super.mobInteract(player, hand);
            if (!result.consumesAction() && this.isOwnedBy(player) && stack.isEmpty()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.getNavigation().stop();
                this.setTarget(null);
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }

            return result;
        }

        // Taming
        if (stack.is(ModItems.SCULK_BONE) && !this.isAngry()) {
            stack.consume(1, player);
            this.tryToTame(player);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // ============================================================
    //                    WET / SHAKE LOGIC
    // ============================================================

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide
                && this.isWet
                && !this.isShaking
                && !this.isPathFinding()
                && this.onGround()) {

            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level().broadcastEntityEvent(this, (byte) 8);
        }

        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            this.updatePersistentAnger(serverLevel, true);
        }
    }

    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (id == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(id);
        }
    }

    // ============================================================
    //                          FOOD
    // ============================================================

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.WOLF_FOOD);
    }

    // ============================================================
    //                         BREEDING
    // ============================================================

    @Nullable
    @Override
    public SculkWolfEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        SculkWolfEntity pup = ModEntities.SCULK_WOLF.get().create(level);

        if (pup != null && otherParent instanceof SculkWolfEntity) {
            if (this.isTame()) {
                pup.setOwnerUUID(this.getOwnerUUID());
                pup.setTame(true, true);
                pup.setCollarColor(this.getCollarColor());
            }
        }

        return pup;
    }

    @Override
    public boolean canMate(Animal mate) {
        if (mate == this) return false;
        if (!(mate instanceof SculkWolfEntity wolf)) return false;
        if (!this.isTame() || !wolf.isTame()) return false;

        return !wolf.isInSittingPose() && this.isInLove() && wolf.isInLove();
    }

    // ============================================================
    //                       COLLAR COLOR
    // ============================================================

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor color) {
        this.entityData.set(DATA_COLLAR_COLOR, color.getId());
    }

    // ============================================================
    //                        ARMOR SYSTEM
    // ============================================================

    public boolean hasArmor() {
        return !this.getBodyArmorItem().isEmpty();
    }

    @Override
    public ItemStack getBodyArmorItem() {
        return this.getItemBySlot(EquipmentSlot.BODY);
    }

    public void setBodyArmorItem(ItemStack stack) {
        this.setItemSlot(EquipmentSlot.BODY, stack);
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        if (!this.canArmorAbsorb(source)) {
            super.actuallyHurt(source, amount);
            return;
        }

        ItemStack armor = this.getBodyArmorItem();
        int before = armor.getDamageValue();
        armor.hurtAndBreak(Mth.ceil(amount), this, EquipmentSlot.BODY);

        if (before != armor.getDamageValue()) {
            this.playSound(SoundEvents.WOLF_ARMOR_DAMAGE);
        }

        if (armor.isEmpty()) {
            this.playSound(SoundEvents.WOLF_ARMOR_CRACK);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE.getDefaultInstance()),
                        this.getX(), this.getY() + 1.0D, this.getZ(),
                        20, 0.2D, 0.1D, 0.2D, 0.1D
                );
            }
        }
    }

    private boolean canArmorAbsorb(DamageSource source) {
        return this.hasArmor() && !source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
    }

    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, new EquipmentSlot[]{EquipmentSlot.BODY});
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.BODY || super.canUseSlot(slot);
    }

    // ============================================================
    //                         SAVE / LOAD
    // ============================================================

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putByte("CollarColor", (byte) this.getCollarColor().getId());
        tag.putInt("Mode", this.getMode());
        tag.putBoolean("Cloaked", this.isCloaked());

        if (this.patrolCenter != null) {
            CompoundTag patrolTag = new CompoundTag();
            patrolTag.putInt("X", this.patrolCenter.getX());
            patrolTag.putInt("Y", this.patrolCenter.getY());
            patrolTag.putInt("Z", this.patrolCenter.getZ());
            tag.put(NBT_PATROL_CENTER, patrolTag);
        }

        if (this.hasArmor()) {
            CompoundTag armorTag = new CompoundTag();
            tag.put(NBT_ARMOR, this.getBodyArmorItem().save(this.registryAccess(), armorTag));
        }

        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        }

        if (tag.contains("Mode", 99)) {
            this.setMode(tag.getInt("Mode"));
        }

        if (tag.contains(NBT_PATROL_CENTER)) {
            CompoundTag patrolTag = tag.getCompound(NBT_PATROL_CENTER);
            this.patrolCenter = new BlockPos(
                    patrolTag.getInt("X"),
                    patrolTag.getInt("Y"),
                    patrolTag.getInt("Z")
            );
        }

        if (tag.contains(NBT_ARMOR)) {
            ItemStack armor = ItemStack.parseOptional(this.registryAccess(), tag.getCompound(NBT_ARMOR));
            this.setBodyArmorItem(armor);
        }

        this.readPersistentAngerSaveData(this.level(), tag);

        if (tag.contains("Cloaked")) {
            this.setCloakedFlag(tag.getBoolean("Cloaked"));
        }

        this.applyTamingSideEffects();
    }

    // ============================================================
    //                         TAME LOGIC
    // ============================================================

    @Override
    public void setTame(boolean tamed, boolean applySideEffects) {
        super.setTame(tamed, applySideEffects);
        if (applySideEffects) {
            this.applyTamingSideEffects();
        }
    }

    protected void applyTamingSideEffects() {
        if (this.isTame()) {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(TAMED_HEALTH);
            this.setHealth(Math.min(this.getHealth(), TAMED_HEALTH));
            if (this.getHealth() < TAMED_HEALTH) {
                this.setHealth(TAMED_HEALTH);
            }
        } else {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(UNTAMED_HEALTH);
            if (this.getHealth() > UNTAMED_HEALTH) {
                this.setHealth(UNTAMED_HEALTH);
            }
        }
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.getNavigation().stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.setMode(MODE_FOLLOW);
            this.level().broadcastEntityEvent(this, (byte) 7);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    // ============================================================
    //                    ANGER / NEUTRAL MOB
    // ============================================================

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_ANGER_TIME, time);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_RANGE.sample(this.random));
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.angerTarget = uuid;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }

        if (!this.level().isClientSide) {
            this.setOrderedToSit(false);
        }

        return super.hurt(source, amount);
    }

    // ============================================================
    //                      VISUAL HELPERS
    // ============================================================

    public float getTailAngle() {
        if (this.isAngry()) {
            return 1.5393804F;
        } else if (this.isTame()) {
            float max = this.getMaxHealth();
            float healthRatio = (max - this.getHealth()) / max;
            return (0.55F - healthRatio * 0.4F) * (float) Math.PI;
        } else {
            return (float) Math.PI / 5.0F;
        }
    }

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float partialTicks) {
        return Math.min(
                0.75F + Mth.lerp(partialTicks, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F,
                1.0F
        );
    }

    public float getBodyRollAngle(float partialTicks, float offset) {
        float f = (Mth.lerp(partialTicks, this.shakeAnimO, this.shakeAnim) + offset) / 1.8F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        return Mth.sin(f * (float) Math.PI) * Mth.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    public float getHeadRollAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.interestedAngleO, this.interestedAngle) * 0.15F * (float) Math.PI;
    }

    @Override
    public int getMaxHeadXRot() {
        return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
    }

    // ============================================================
    //                         LEASH OFFSET
    // ============================================================

    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, this.getEyeHeight() * 0.6D, this.getBbWidth() * 0.4D);
    }

    // ============================================================
    //                         SPAWN RULES
    // ============================================================

    public static boolean checkSpawnRules(
            EntityType<SculkWolfEntity> type,
            LevelAccessor level,
            net.minecraft.world.entity.MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getBlockState(pos.below()).is(BlockTags.WOLVES_SPAWNABLE_ON)
                && Animal.isBrightEnoughToSpawn(level, pos);
    }

    // ============================================================
    //                           SOUNDS
    // ============================================================

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return this.isAngry() ? SoundEvents.WOLF_GROWL : SoundEvents.WOLF_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }
}