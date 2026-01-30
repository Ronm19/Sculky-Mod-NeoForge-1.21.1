package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.worldgen.biome.ModBiomes;

public class SculkBeetleEntity extends PathfinderMob implements NeutralMob {

    // 10–30 seconds anger
    private static final UniformInt PERSISTENT_ANGER_TIME = UniformInt.of(20 * 10, 20 * 30);

    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public SculkBeetleEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 3;
    }

    // ✅ Beetle eye height override (low to the ground)
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.22F;
    }

    // ✅ Attributes
    public static AttributeSupplier.Builder createSculkBeetleAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1D);
    }

    // ✅ AI
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Only matters when it has a target (it gets one when provoked)
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, true));

        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // Neutral targeting: retaliate only
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    // ✅ Become angry at players who hurt it
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (result && !this.level().isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker instanceof Player player) {
                this.setPersistentAngerTarget(player.getUUID());
                this.startPersistentAngerTimer();
                this.setTarget(player);
            }
        }
        return result;
    }

    // ✅ Anger ticking
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            // true = do not forgive if target still close-ish
            this.updatePersistentAnger(serverLevel, true);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        RandomSource random = this.getRandom();

        int count = random.nextInt(2); // 0–1 base drop

        if (count > 0) {
            this.spawnAtLocation(new ItemStack(ModItems.SCULK_CHITIN.get(), count));
        }
    }



    // ✅ Spawn predicate (register with SpawnPlacements)
    public static boolean canSpawn(EntityType<SculkBeetleEntity> type, LevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {

        // Must be in SCULK_WASTES
        if (!level.getBiome(pos).is(ModBiomes.SCULK_WASTES)) return false;

        // Must be on SCULK_SAND
        BlockState below = level.getBlockState(pos.below());
        if (!below.is(ModBlocks.INFESTED_SCULK_SAND.get())) return false;

        // Need air at spawn position
        if (!level.getBlockState(pos).isAir()) return false;

        // Vanilla mob spawn rules (space, etc.)
        return Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }


    // ---- NeutralMob required methods ----


    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.remainingPersistentAngerTime = time;
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    // Save/load anger
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
    }

    // ✅ Sounds (placeholders — change later if you want)
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