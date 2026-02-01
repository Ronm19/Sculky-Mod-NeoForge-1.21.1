package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.util.ModTags;

public class SculkSandsnareEntity extends Monster implements Enemy {

    public SculkSandsnareEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    /* ---------------- ATTRIBUTES ---------------- */

    public static AttributeSupplier.Builder createSculkSandsnareAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    /* ---------------- AI GOALS ---------------- */

    @Override
    protected void registerGoals() {

        // Attack
        this.goalSelector.addGoal(2,
                new MeleeAttackGoal(this, 1.1D, false));

        // Wander slowly (creepy)
        this.goalSelector.addGoal(7,
                new WaterAvoidingRandomStrollGoal(this, 0.6D));

        // Look at player
        this.goalSelector.addGoal(8,
                new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.goalSelector.addGoal(9,
                new RandomLookAroundGoal(this));

        // Target players
        this.targetSelector.addGoal(1,
                new HurtByTargetGoal(this));

        this.targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3,
                new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    /* ---------------- SOUNDS ---------------- */

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SHRIEKER_PLACE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SCULK_BLOCK_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    /* ---------------- STEP SOUND ---------------- */

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SCULK_BLOCK_STEP, 0.15F, 1.0F);
    }

    /* ---------------- LOOT ---------------- */

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        RandomSource random = this.getRandom();

        int chitin = random.nextInt(2) + 1; // 1–2 chitin
        this.spawnAtLocation(new ItemStack(ModItems.SCULK_CHITIN.get(), chitin));
    }

    /* ---------------- SPAWN RULES ---------------- */
    
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }
}
