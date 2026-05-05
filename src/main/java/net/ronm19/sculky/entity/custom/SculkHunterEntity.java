package net.ronm19.sculky.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ronm19.sculky.entity.ai.HunterSenseTargetGoal;
import net.ronm19.sculky.entity.ai.RelentlessMeleeHuntGoal;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SculkHunterEntity extends Animal {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;


    public SculkHunterEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(false); // prevent random gear replacing the dagger
        this.isValidHunterTarget(this);
    }

    public static AttributeSupplier.Builder createSculkHunterAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 72.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RelentlessMeleeHuntGoal(this, 1.2D));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new HunterSenseTargetGoal(this, 40.0D));
    }

    private boolean isValidHunterTarget(LivingEntity target) {
        return target != null
                && target.isAlive()
                && target != this
                && target.getType().is(ModTags.Entities.SCULK_ALLIES);
    }

    @Override
    public boolean isFood( @NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ECHO_DAGGER.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.085F); // 8.5% drop chance
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @javax.annotation.Nullable SpawnGroupData spawnGroupData) {

        spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);

        return spawnGroupData;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SCULK_CATALYST_BLOOM;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SHRIEKER_SHRIEK;
    }
}