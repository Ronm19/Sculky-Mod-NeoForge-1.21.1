package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.EventHooks;
import net.ronm19.sculky.entity.ModEntities;

import net.ronm19.sculky.entity.ai.FollowAlphaGoal;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class SculkWolfEntity extends TamableAnimal implements NeutralMob {

    // ============================================================
    //                      SYNCHRONIZED DATA
    // ============================================================

    // --- Mode system ---
    public static final int MODE_FOLLOW = 0;
    public static final int MODE_GUARD = 1;
    public static final int MODE_PATROL = 2;
    public static final int MODE_STAY = 3;

    private static final EntityDataAccessor<Integer> DATA_MODE =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.INT);

    // --- Sculk Cloak (invisibility in darkness) ---
    private static final EntityDataAccessor<Boolean> DATA_CLOAKED =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.BOOLEAN);

    // --- Collar color ---
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.INT);

    // --- Anger timer ---
    private static final EntityDataAccessor<Integer> DATA_ANGER_TIME =
            SynchedEntityData.defineId(SculkWolfEntity.class, EntityDataSerializers.INT);

    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID angerTarget;

    // ============================================================
    //                        EXTRA VARIABLES
    // ============================================================

    private BlockPos patrolCenter = null;

    private int howlCooldown = 0;
    private int dashCooldown = 0;

    private boolean isCloaked = false;

    // Shake animation
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;

    // Head tilt animation (like wolf beg)
    private float interestedAngle;
    private float interestedAngleO;

    // Constants
    private static final float UNTAMED_HEALTH = 8.0F;
    private static final float TAMED_HEALTH = 40.0F;

    // ============================================================
    //                        CONSTRUCTOR
    // ============================================================

    public SculkWolfEntity( EntityType<? extends SculkWolfEntity> type, Level level ) {
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
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D);
    }

    // ============================================================
    //                        DEFINE DATA
    // ============================================================

    @Override
    protected void defineSynchedData( SynchedEntityData.Builder builder ) {
        super.defineSynchedData(builder);

        builder.define(DATA_MODE, MODE_FOLLOW);
        builder.define(DATA_CLOAKED, false);
        builder.define(DATA_COLLAR_COLOR, DyeColor.CYAN.getId());
        builder.define(DATA_ANGER_TIME, 0);
    }

    // ============================================================
    //                          AI GOALS
    // ============================================================

    @Override
    protected void registerGoals() {

        // Basic behaviors
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));

        // Combat + Attack movement
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));

        // Follow owner ONLY in Follow Mode
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new FollowAlphaGoal(this, 1.0D));

        // Normal wolf-like wandering & looking
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // Target AI
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(6, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    // ============================================================
    //                          TICK LOGIC
    // ============================================================

    @Override
    public void tick() {
        super.tick();

        if (howlCooldown > 0) howlCooldown--;
        if (dashCooldown > 0) dashCooldown--;

        handleCloak();
        handleEchoSense();
        handlePackLink();

        // Interest head tilt
        this.interestedAngleO = this.interestedAngle;
        this.interestedAngle += (this.isInterested() ? 1.0F : -1.0F) * 0.4F;

        if (this.isInWaterRainOrBubble()) {
            this.isWet = true;
            if (this.isShaking) {
                this.level().broadcastEntityEvent(this, (byte)56);
                cancelShake();
            }
        } else if (this.isWet && this.isShaking) {
            // Shake animation logic
            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05F;

            if (this.shakeAnimO >= 2.0F) {
                this.isWet = false;
                this.isShaking = false;
                this.shakeAnimO = 0.0F;
                this.shakeAnim = 0.0F;
            }

            if (this.shakeAnim > 0.4F) {
                int count = (int)(Mth.sin((this.shakeAnim - 0.4F) * (float)Math.PI) * 7.0F);
                Vec3 vec3 = this.getDeltaMovement();

                for (int i = 0; i < count; ++i) {
                    double x = this.getX() + (random.nextDouble() - 0.5) * this.getBbWidth();
                    double y = this.getY() + 0.8F;
                    double z = this.getZ() + (random.nextDouble() - 0.5) * this.getBbWidth();
                    this.level().addParticle(ParticleTypes.SPLASH, x, y, z, vec3.x, vec3.y, vec3.z);
                }
            }

            SculkWolfAlphaEntity alpha = this.level().getNearestEntity(
                    SculkWolfAlphaEntity.class,
                    TargetingConditions.forNonCombat(),
                    this,
                    this.getX(), this.getY(), this.getZ(),
                    this.getBoundingBox().inflate(12)
            );

            if (alpha != null && !this.isTame()) {
                followAlpha(alpha);
            }

        }
    }

    private void followAlpha(SculkWolfAlphaEntity alpha) {
        double distance = this.distanceTo(alpha);

        if (distance > 3) {
            this.getNavigation().moveTo(alpha, 1.2D);
        }

        // Protect Alpha if he is hurt
        LivingEntity attacker = alpha.getLastHurtByMob();
        if (attacker != null) {
            this.setTarget(attacker);
        }
    }


    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    public boolean isInterested() {
        return this.interestedAngle > 0.0F;
    }

    // ============================================================
    //                   ABILITY: SCULK HOWL
    // ============================================================

    public void tryHowl(Player player) {
        if (howlCooldown > 0) return;

        howlCooldown = 240; // 12 sec cooldown

        // Reveal enemies
        this.level().getEntitiesOfClass(Monster.class,
                        this.getBoundingBox().inflate(24), e -> true)
                .forEach(e -> e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100)));

        // Buff wolf
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120));

        // Sculk particles
        for (int i = 0; i < 20; i++) {
            this.level().addParticle(
                    ParticleTypes.SCULK_CHARGE_POP,
                    this.getX() + (random.nextDouble() - 0.5) * 2,
                    this.getY() + 1,
                    this.getZ() + (random.nextDouble() - 0.5) * 2,
                    0, 0.1, 0
            );
        }

        this.playSound(SoundEvents.WARDEN_ROAR, 1f, 1.4f);
    }

    // ============================================================
    //                 ABILITY: SCULK CLOAK (STEALTH)
    // ============================================================

    public boolean isCloaked() {
        return this.entityData.get(DATA_CLOAKED);
    }

    private void setCloakedFlag(boolean v) {
        this.entityData.set(DATA_CLOAKED, v);
    }

    private void handleCloak() {
        boolean dark = this.level().getMaxLocalRawBrightness(this.blockPosition()) <= 4;

        if (dark && !this.isInSittingPose()) {
            if (!isCloaked) {
                isCloaked = true;
                setCloakedFlag(true);
            }
        } else {
            if (isCloaked) {
                isCloaked = false;
                setCloakedFlag(false);
            }
        }
    }

    // ============================================================
    //                 ABILITY: ECHO SENSE
    // ============================================================

    private void handleEchoSense() {
        if (this.level().isClientSide) return;

        boolean detected = !this.level().getEntitiesOfClass(Monster.class,
                this.getBoundingBox().inflate(12)).isEmpty();

        if (detected && random.nextInt(40) == 0) {
            this.playSound(SoundEvents.SCULK_SENSOR_HIT, 0.6f, 1.8f);
        }
    }

    // ============================================================
    //               ABILITY: PACK LINK AURA
    // ============================================================

    private void handlePackLink() {
        if (!this.isTame()) return;

        this.level().getEntitiesOfClass(SculkWolfEntity.class,
                this.getBoundingBox().inflate(12)).forEach(w -> {

            if (w != this && w.isTame() && w.getOwner() == this.getOwner()) {
                w.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 0, false, false));
                w.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,   10, 0, false, false));
            }
        });
    }

    // ============================================================
    //                ABILITY: DASH ATTACK
    // ============================================================

    public void tryDashAttack(LivingEntity target) {
        if (dashCooldown > 0) return;
        dashCooldown = 60; // 3 sec cooldown

        Vec3 dir = new Vec3(
                target.getX() - this.getX(),
                target.getY() - this.getY() + 0.2,
                target.getZ() - this.getZ()
        ).normalize().scale(1.4);

        this.setDeltaMovement(dir);

        this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 1.0f, 1.3f);
    }

    // ============================================================
    //                    PLAYER INTERACTION
    // ============================================================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (!this.level().isClientSide &&
                player.isShiftKeyDown() &&
                stack.isEmpty()) {

            // Prioritize Howl if wolf has a target
            if (this.getTarget() != null && howlCooldown == 0) {
                tryHowl(player);
                return InteractionResult.SUCCESS;
            }
        }

        if (!this.level().isClientSide || this.isBaby() && this.isFood(itemstack)) {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodproperties = itemstack.getFoodProperties(this);
                    float f = foodproperties != null ? (float) foodproperties.nutrition() : 1.0F;
                    this.heal(2.0F * f);
                    itemstack.consume(1, player);
                    this.gameEvent(GameEvent.EAT);
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                } else {
                    if (item instanceof DyeItem) {
                        DyeItem dyeitem = (DyeItem) item;
                        if (this.isOwnedBy(player)) {
                            DyeColor dyecolor = dyeitem.getDyeColor();
                            if (dyecolor != this.getCollarColor()) {
                                this.setCollarColor(dyecolor);
                                itemstack.consume(1, player);
                                return InteractionResult.SUCCESS;
                            }

                            return super.mobInteract(player, hand);
                        }
                    }

                    if (itemstack.is(Items.WOLF_ARMOR) && this.isOwnedBy(player) && this.getBodyArmorItem().isEmpty() && !this.isBaby()) {
                        this.setBodyArmorItem(itemstack.copyWithCount(1));
                        itemstack.consume(1, player);
                        return InteractionResult.SUCCESS;
                    } else if (!itemstack.canPerformAction(ItemAbilities.SHEARS_REMOVE_ARMOR) || !this.isOwnedBy(player) || !this.hasArmor() || EnchantmentHelper.has(this.getBodyArmorItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) && !player.isCreative()) {
                        if (((Ingredient) ((ArmorMaterial) ArmorMaterials.ARMADILLO.value()).repairIngredient().get()).test(itemstack) && this.isInSittingPose() && this.hasArmor() && this.isOwnedBy(player) && this.getBodyArmorItem().isDamaged()) {
                            itemstack.shrink(1);
                            this.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
                            ItemStack itemstack2 = this.getBodyArmorItem();
                            int i = (int) ((float) itemstack2.getMaxDamage() * 0.125F);
                            itemstack2.setDamageValue(Math.max(0, itemstack2.getDamageValue() - i));
                            return InteractionResult.SUCCESS;
                        } else {
                            InteractionResult interactionresult = super.mobInteract(player, hand);
                            if (!interactionresult.consumesAction() && this.isOwnedBy(player)) {
                                this.setOrderedToSit(!this.isOrderedToSit());
                                this.jumping = false;
                                this.navigation.stop();
                                this.setTarget((LivingEntity) null);
                                return InteractionResult.SUCCESS_NO_ITEM_USED;
                            } else {
                                return interactionresult;
                            }
                        }
                    } else {
                        itemstack.hurtAndBreak(1, player, getSlotForHand(hand));
                        this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
                        ItemStack itemstack1 = this.getBodyArmorItem();
                        this.setBodyArmorItem(ItemStack.EMPTY);
                        this.spawnAtLocation(itemstack1);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else if (itemstack.is(ModItems.SCULK_BONE) && !this.isAngry()) {
                itemstack.consume(1, player);
                this.tryToTame(player);
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(player, hand);
            }
        } else {
            boolean flag = this.isOwnedBy(player) || this.isTame() || itemstack.is(ModItems.SCULK_BONE) && !this.isTame() && !this.isAngry();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
    }


    // ============================================================
    //                     SHAKE / WETNESS LOGIC
    // ============================================================

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isWet && !this.isShaking
                && !this.isPathFinding() && this.onGround()) {

            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level().broadcastEntityEvent(this, (byte)8);
        }

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    // ============================================================
    //                       FEEDING
    // ============================================================

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.WOLF_FOOD);
    }

    private float getNutrition(ItemStack stack) {
        FoodProperties food = stack.getFoodProperties(this);
        return food != null ? food.nutrition() : 1.0F;
    }

    // ============================================================
    //                     BREEDING
    // ============================================================

    @Nullable
    @Override
    public SculkWolfEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        SculkWolfEntity pup = ModEntities.SCULK_WOLF.get().create(level);
        if (pup != null && otherParent instanceof SculkWolfEntity other) {

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
    //                     COLLAR COLOR
    // ============================================================

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor color) {
        this.entityData.set(DATA_COLLAR_COLOR, color.getId());
    }

    // ============================================================
    //                     WOLF ARMOR SYSTEM
    // ============================================================

    private static final String NBT_ARMOR = "SculkWolfArmorItem";

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
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        if (!this.canArmorAbsorb(damageSource)) {
            super.actuallyHurt(damageSource, damageAmount);
            return;
        }

        // Armor absorbs damage
        ItemStack armor = this.getBodyArmorItem();
        int before = armor.getDamageValue();
        armor.hurtAndBreak(Mth.ceil(damageAmount), this, EquipmentSlot.BODY);

        if (before != armor.getDamageValue()) {
            this.playSound(SoundEvents.WOLF_ARMOR_DAMAGE);
        }

        // If armor breaks → drop particles
        if (armor.isEmpty()) {
            this.playSound(SoundEvents.WOLF_ARMOR_CRACK);
            if (this.level() instanceof ServerLevel serverlevel) {
                serverlevel.sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE.getDefaultInstance()),
                        this.getX(), this.getY() + 1.0F, this.getZ(),
                        20, 0.2, 0.1, 0.2, 0.1
                );
            }
        }
    }

    // ============================================================
    //                       NBT SAVE/LOAD
    // ============================================================

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putByte("CollarColor", (byte)this.getCollarColor().getId());

        if (this.hasArmor()) {
            tag.put("ArmorItem", this.getBodyArmorItem().save((HolderLookup.Provider) new CompoundTag()));
        }

        this.addPersistentAngerSaveData(tag);

        // Save Sculk cloak flag
        tag.putBoolean("Cloaked", this.isCloaked());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        }

        if (tag.contains("ArmorItem")) {
            ItemStack armor = ItemStack.parseOptional(this.registryAccess(), tag.getCompound("ArmorItem"));
            this.setBodyArmorItem(armor);
        }

        this.readPersistentAngerSaveData(this.level(), tag);

        if (tag.contains("Cloaked"))
            this.setCloakedFlag(tag.getBoolean("Cloaked"));
    }

    // ============================================================
    //                     ANGER / NEUTRAL MOB
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

    protected void applyTamingSideEffects() {
        if (this.isTame()) {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue((double)40.0F);
            this.setHealth(40.0F);
        } else {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue((double)20.0F);
        }

    }

    protected void hurtArmor(DamageSource damageSource, float damageAmount) {
        this.doHurtEquipment(damageSource, damageAmount, new EquipmentSlot[]{EquipmentSlot.BODY});
    }

    private boolean canArmorAbsorb(DamageSource damageSource) {
        return this.hasArmor() && !damageSource.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
            }

            return super.hurt(source, amount);
        }
    }

    public boolean canUseSlot(EquipmentSlot slot) {
        return true;
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget((LivingEntity)null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }

    }

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

    public float getTailAngle() {
        if (this.isAngry()) {
            return 1.5393804F;
        } else if (this.isTame()) {
            float f = this.getMaxHealth();
            float f1 = (f - this.getHealth()) / f;
            return (0.55F - f1 * 0.4F) * (float)Math.PI;
        } else {
            return ((float)Math.PI / 5F);
        }
    }

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float partialTicks) {
        return Math.min(0.75F + Mth.lerp(partialTicks, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F, 1.0F);
    }

    public float getBodyRollAngle(float partialTicks, float offset) {
        float f = (Mth.lerp(partialTicks, this.shakeAnimO, this.shakeAnim) + offset) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float)Math.PI) * Mth.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    public float getHeadRollAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.interestedAngleO, this.interestedAngle) * 0.15F * (float)Math.PI;
    }

    public int getMaxHeadXRot() {
        return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
    }




    // ============================================================
    //                     LEASH OFFSET
    // ============================================================

    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight() * 0.6, this.getBbWidth() * 0.4);
    }

    // ============================================================
    //                     SPAWN RULES
    // ============================================================

    public static boolean checkSpawnRules(
            EntityType<SculkWolfEntity> type,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getBlockState(pos.below()).is(BlockTags.WOLVES_SPAWNABLE_ON)
                && Animal.isBrightEnoughToSpawn(level, pos);
    }

    // ============================================================
    //                     SOUNDS
    // ============================================================

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

}

