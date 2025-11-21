package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SculkFoxEntity extends TamableAnimal {
    float crouchAmount;
    float crouchAmountO;
    private float interestedAngle;
    private float interestedAngleO;
    private int ticksSinceEaten;


    // Ability cooldowns
    private int senseCooldown = 0;
    private int pounceCooldown = 0;
    private int silentCooldown = 0;

    public SculkFoxEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    // -------------------------------
    // ATTRIBUTES
    // -------------------------------
    public static AttributeSupplier.Builder createSculkFoxAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 44.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_SPEED, 2.0D);
    }

    // -------------------------------
    // TICK LOGIC
    // -------------------------------
    @Override
    public void tick() {
        super.tick();

        if (senseCooldown > 0) senseCooldown--;
        if (pounceCooldown > 0) pounceCooldown--;
        if (silentCooldown > 0) silentCooldown--;

        // Auto silent-run every 10 seconds
        if (this.isTame() && silentCooldown == 0) {
            activateSilentRun();
            silentCooldown = 200;
        }
    }

    public void aiStep() {
        if (!this.level().isClientSide && this.isAlive() && this.isEffectiveAi()) {
            ++this.ticksSinceEaten;
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (this.canEat(itemstack)) {
                if (this.ticksSinceEaten > 600) {
                    ItemStack itemstack1 = itemstack.finishUsingItem(this.level(), this);
                    if (!itemstack1.isEmpty()) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack1);
                    }

                    this.ticksSinceEaten = 0;
                } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
                    this.level().broadcastEntityEvent(this, (byte)45);
                }
            }

            LivingEntity livingentity = this.getTarget();
            if (livingentity == null || !livingentity.isAlive()) {
                this.setIsCrouching(false);
                this.setIsInterested(false);
            }
        }

        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }

        super.aiStep();
        if (this.isDefending() && this.random.nextFloat() < 0.05F) {
            this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
        }
    }

    // -------------------------------
    // TAME / INTERACTION
    // -------------------------------
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);

        // Attempt to tame
        if (!this.isTame() && isTamingItem(item)) {
            return tryTame(player, item);
        }

        // Sit toggle
        if (this.isTame() && player.isShiftKeyDown()) {
            this.setOrderedToSit(!this.isOrderedToSit());
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    private boolean isTamingItem(ItemStack item) {
        return item.is(Items.ECHO_SHARD)
                || item.is(Items.SCULK)
                || item.is(ModItems.SCULK_SHARD.get());
    }

    private InteractionResult tryTame(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        this.heal(3);

        level().playSound(null, this,
                SoundEvents.ALLAY_ITEM_GIVEN,
                SoundSource.NEUTRAL, 1, 1.3F);

        if (this.random.nextInt(4) == 0) {
            this.tame(player);
            level().broadcastEntityEvent(this, (byte) 7); // hearts
        } else {
            level().broadcastEntityEvent(this, (byte) 6); // smoke
        }

        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    // -------------------------------
    // SILENT RUN
    // -------------------------------
    private void activateSilentRun() {
        this.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                100, 1, false, false
        ));

        level().playSound(null, this,
                SoundEvents.SCULK_BLOCK_CHARGE,
                SoundSource.AMBIENT, 1F, 1.8F);
    }


    @Override
    public boolean isFood(ItemStack item) {
        return item.is(ModItems.SCULK_SHARD.get());
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob otherParent) {
        return ModEntities.SCULK_FOX.get().create(serverLevel); // No breeding
    }


    // -------------------------------
// AI GOALS
// -------------------------------
    @Override
    protected void registerGoals() {

        // Basic survival
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Sit
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        // Follow owner
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.25D, 8F, 2F));

        // Melee attack — REQUIRED
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.3D, true));

        // Random stroll
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        // Look around
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 12F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // Sculk abilities
        this.goalSelector.addGoal(7, new SculkSenseGoal(this));
        this.goalSelector.addGoal(8, new SculkPounceGoal(this));

        // Targeting
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(10, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(11, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(12, new HurtByTargetGoal(this));

}

    // --------------------------------------
// SCULK SENSE — Detect nearby monsters
// --------------------------------------
    private static class SculkSenseGoal extends Goal {

        private final SculkFoxEntity fox;

        public SculkSenseGoal(SculkFoxEntity fox) {
            this.fox = fox;
        }

        @Override
        public boolean canUse() {
            return fox.isTame() && fox.senseCooldown == 0;
        }

        @Override
        public void start() {
            fox.senseCooldown = 50; // 2.5 seconds cooldown

            fox.level().playSound(null, fox,
                    SoundEvents.WARDEN_LISTENING,
                    SoundSource.AMBIENT, 1F, 1.4F);

            // Detect nearest monster
            Monster target = fox.level().getNearestEntity(
                    fox.level().getEntitiesOfClass(Monster.class,
                            fox.getBoundingBox().inflate(18)),
                    TargetingConditions.forCombat(),
                    fox,
                    fox.getX(), fox.getY(), fox.getZ()
            );

            if (target != null) {
                fox.setTarget(target);
            }
        }
    }

    // --------------------------------------
// SCULK POUNCE — Teleport attack
// --------------------------------------
    private static class SculkPounceGoal extends Goal {

        private final SculkFoxEntity fox;

        public SculkPounceGoal(SculkFoxEntity fox) {
            this.fox = fox;
        }

        @Override
        public boolean canUse() {
            return fox.isTame()
                    && fox.getTarget() != null
                    && fox.getTarget().isAlive()
                    && fox.pounceCooldown == 0;
        }

        @Override
        public void start() {

            fox.pounceCooldown = 40; // 2 seconds cooldown

            LivingEntity target = fox.getTarget();
            if (target == null) return;

            // Direction & teleport position
            Vec3 dir = target.position().subtract(fox.position());
            Vec3 step = dir.normalize().scale(3.2F); // 3 block teleport

            Vec3 newPos = fox.position().add(step);

            // Teleport silently
            fox.teleportTo(newPos.x, newPos.y, newPos.z);

            fox.level().playSound(null, fox,
                    SoundEvents.ENDER_PEARL_THROW,
                    SoundSource.PLAYERS, 0.8F, 1.6F);

            // Attack if close enough
            if (fox.distanceTo(target) < 2.1F) {
                fox.doHurtTarget(target);
                fox.level().playSound(null, fox,
                        SoundEvents.WARDEN_ATTACK_IMPACT,
                        SoundSource.HOSTILE, 1F, 1F);
            }
        }
    }

    // -------------------------------
// SAVE + LOAD
// -------------------------------
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("SenseCooldown", senseCooldown);
        tag.putInt("PounceCooldown", pounceCooldown);
        tag.putInt("SilentCooldown", silentCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        senseCooldown = tag.getInt("SenseCooldown");
        pounceCooldown = tag.getInt("PounceCooldown");
        silentCooldown = tag.getInt("SilentCooldown");
    }

    // -------------------------------
// SOUNDS
// -------------------------------
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SCULK_BLOCK_STEP, 0.25F, 1.4F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_CLICKING;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }


    @Override
    public boolean doHurtTarget(Entity target) {
        boolean success = super.doHurtTarget(target);

        if (success && target instanceof LivingEntity living) {
            // bonus magic damage
            living.hurt(this.damageSources().magic(), 3F);

            // sculk hit sound
            this.level().playSound(null, this,
                    SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE, 1F, 1.6F);
        }

        return success;
    }

    private boolean canEat(ItemStack stack) {
        return stack.has(DataComponents.FOOD) && this.getTarget() == null && this.onGround() && !this.isSleeping();
    }


    // -------------------------------
    // SPAWN RULES
    // -------------------------------
    public static boolean canSpawn(EntityType<SculkFoxEntity> type,
                                   LevelAccessor level,
                                   MobSpawnType reason,
                                   BlockPos pos,
                                   RandomSource random) {

        boolean sculkBlock =
                level.getBlockState(pos.below()).is(Blocks.SCULK) ||
                        level.getBlockState(pos.below()).is(Blocks.SCULK_VEIN) ||
                        level.getBlockState(pos.below()).is(ModBlocks.INFESTED_SCULK_GRASS_BLOCK);

        // must be dark-ish
        boolean darkEnough = level.getRawBrightness(pos, 0) < 7;

        return sculkBlock && darkEnough;
    }

    public boolean isSensitiveToLight() {
        return false; // it *is* a sculk fox after all
    }

    @Override
    public boolean isAffectedByPotions() {
        return true; // can receive buffs
    }

    public boolean isFaceplanted() {
        return this.getFlag(64);
    }


    public boolean isSitting() {
        return this.getFlag(1);
    }

    public float getCrouchAmount(float partialTick) {
        return Mth.lerp(partialTick, this.crouchAmountO, this.crouchAmount);
    }

    public float getHeadRollAngle(float partialTick) {
        return Mth.lerp(partialTick, this.interestedAngleO, this.interestedAngle) * 0.11F * (float)Math.PI;
    }

    void setFaceplanted(boolean faceplanted) {
        this.setFlag(64, faceplanted);
    }

    public void setIsCrouching(boolean isCrouching) {
        this.setFlag(4, isCrouching);
    }

    public boolean isPouncing() {
        return this.getFlag(16);
    }

    public void setIsPouncing(boolean isPouncing) {
        this.setFlag(16, isPouncing);
    }

    public void setIsInterested(boolean isInterested) {
        this.setFlag(8, isInterested);
    }

    public boolean isInterested() {
        return this.getFlag(8);
    }

    boolean isDefending() {
        return this.getFlag(128);
    }

    void setDefending(boolean defending) {
        this.setFlag(128, defending);
    }

    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        if (random.nextFloat() < 0.2F) {
            float f = random.nextFloat();
            ItemStack itemstack;
            if (f < 0.05F) {
                itemstack = new ItemStack(ModItems.SCULK_SHARD.asItem());
            } else if (f < 0.2F) {
                itemstack = new ItemStack(Items.EGG);
            } else if (f < 0.4F) {
                itemstack = random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
            } else if (f < 0.6F) {
                itemstack = new ItemStack(Items.WHEAT);
            } else if (f < 0.8F) {
                itemstack = new ItemStack(Items.LEATHER);
            } else {
                itemstack = new ItemStack(Items.FEATHER);

            }

            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        }

    }

    public void handleEntityEvent(byte id) {
        if (id == 45) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemstack.isEmpty()) {
                for(int i = 0; i < 8; ++i) {
                    Vec3 vec3 = (new Vec3(((double)this.random.nextFloat() - (double)0.5F) * 0.1, Math.random() * 0.1 + 0.1, (double)0.0F)).xRot(-this.getXRot() * ((float)Math.PI / 180F)).yRot(-this.getYRot() * ((float)Math.PI / 180F));
                    this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + this.getLookAngle().x / (double)2.0F, this.getY(), this.getZ() + this.getLookAngle().z / (double)2.0F, vec3.x, vec3.y + 0.05, vec3.z);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    private void setFlag(int flagId, boolean value) {
        if (value) {
            this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) | flagId));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) & ~flagId));
        }

    }

    private boolean getFlag(int flagId) {
        return ((Byte)this.entityData.get(DATA_FLAGS_ID) & flagId) != 0;
    }
}

