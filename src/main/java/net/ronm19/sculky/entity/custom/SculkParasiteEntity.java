package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.effect.ModEffects;
import net.ronm19.sculky.util.ModTags;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class SculkParasiteEntity extends Monster implements Enemy {
    @javax.annotation.Nullable
    private SculkWakeUpFriendsGoal friendsGoal;

    public SculkParasiteEntity(EntityType<? extends SculkParasiteEntity> type, Level level) {
        super((EntityType<? extends Monster>) type, level);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.xpReward = 2;


    }

    @Override
    protected void registerGoals() {
        this.friendsGoal = new SculkWakeUpFriendsGoal(this);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(3, new SculkMergeWithBlockGoal(this)); // custom burrow
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    public static AttributeSupplier.Builder createSculkParasiteAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 2.5D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ARMOR, 1.0D);
    }


    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        // Visual sculk particle emission
        if (level().isClientSide && tickCount % 10 == 0) {
            Vec3 pos = this.position();
            for (int i = 0; i < 2; i++) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                        pos.x + (random.nextDouble() - 0.5D) * 0.3D,
                        pos.y + random.nextDouble() * 0.2D,
                        pos.z + (random.nextDouble() - 0.5D) * 0.3D,
                        0, 0.01D, 0);
            }
        }
    }

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_BLOCK_CHARGE;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_BLOCK_SPREAD;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if ((source.getEntity() != null || source.is(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) && this.friendsGoal != null)
            this.friendsGoal.notifyHurt();
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        this.yBodyRot = this.getYRot();

        if (this.tickCount % 40 == 0 && this.level().isClientSide)
            this.level().addParticle(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 0.1, this.getZ(), 0, 0.02, 0);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        BlockState below = level.getBlockState(pos.below());
        return isSculkHostBlock(below) ? 10.0F : super.getWalkTargetValue(pos, level);
    }


    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.18F; // lower to ground
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }

    @Override
    public boolean isAlliedTo( @NotNull Entity entity) {
        return super.isAlliedTo(entity) || entity.getType().is(ModTags.Entities.SCULK_ALLIES);
    }


    @Override
    public boolean fireImmune() {
        return true;
    }

    // Determines which blocks can host parasites
    private boolean isSculkHostBlock(BlockState state) {
        return state.is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get())
                || state.is(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get())
                || state.is(ModBlocks.INFESTED_SCULK_SAND.get())
                || state.is(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK.get());
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = super.doHurtTarget(target);
        if (flag && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.SCULK_INFECTION_EFFECT, 120, 0)); // 6 s infection
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.HOSTILE, 0.5F, 1.2F);
        }
        return flag;
    }

    // 🧠 Custom burrow behavior
    static class SculkMergeWithBlockGoal extends RandomStrollGoal {
        private Direction dir;
        private boolean doMerge;

        public SculkMergeWithBlockGoal(SculkParasiteEntity entity) {
            super(entity, 1.0D, 10);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null || !mob.getNavigation().isDone()) return false;
            RandomSource rand = mob.getRandom();

            if (EventHooks.canEntityGrief(mob.level(), mob) && rand.nextInt(12) == 0) {
                dir = Direction.getRandom(rand);
                BlockPos pos = mob.blockPosition().relative(dir);
                BlockState state = mob.level().getBlockState(pos);
                if (((SculkParasiteEntity) mob).isSculkHostBlock(state)) {
                    doMerge = true;
                    return true;
                }
            }
            doMerge = false;
            return super.canUse();
        }

        @Override
        public void start() {
            if (!doMerge) {
                super.start();
            } else {
                LevelAccessor level = mob.level();
                BlockPos pos = mob.blockPosition().relative(dir);
                BlockState state = level.getBlockState(pos);

                if (((SculkParasiteEntity) mob).isSculkHostBlock(state)) {
                    level.setBlock(pos, state, 3); // optional visual pulse
                    mob.spawnAnim();
                    mob.discard();
                }
            }
        }
    }

    // 👁️ When hurt, wake nearby parasites
    static class SculkWakeUpFriendsGoal extends Goal {
        private final SculkParasiteEntity parasite;
        private int lookForFriends;

        public SculkWakeUpFriendsGoal(SculkParasiteEntity parasite) {
            this.parasite = parasite;
        }

        public void notifyHurt() {
            if (lookForFriends == 0) lookForFriends = this.adjustedTickDelay(20);
        }

        @Override
        public boolean canUse() {
            return lookForFriends > 0;
        }

        @Override
        public void tick() {
            --lookForFriends;
            if (lookForFriends <= 0) {
                Level level = parasite.level();
                BlockPos center = parasite.blockPosition();
                RandomSource random = parasite.getRandom();

                for (BlockPos pos : BlockPos.betweenClosed(center.offset(-5, -3, -5), center.offset(5, 3, 5))) {
                    BlockState state = level.getBlockState(pos);
                    if (((SculkParasiteEntity) parasite).isSculkHostBlock(state)) {
                        if (EventHooks.canEntityGrief(level, parasite)) {
                            level.destroyBlock(pos, true, parasite);
                        }
                        if (random.nextBoolean()) return;
                    }
                }
            }
        }
    }
}
