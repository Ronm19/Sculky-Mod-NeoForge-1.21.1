package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.effect.ModEffects;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.util.ModTags;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class SculkParasiteEntity extends Monster implements Enemy {

    @javax.annotation.Nullable
    private SculkWakeUpFriendsGoal friendsGoal;

    // FIX: newly spawned parasites are locked out of the wake-up goal
    // for 5 seconds (100 ticks) to break the chain reaction
    private int spawnProtectionTicks = 100;

    public SculkParasiteEntity(EntityType<? extends SculkParasiteEntity> type, Level level) {
        super((EntityType<? extends Monster>) type, level);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.xpReward = 2;
    }

    @Override
    protected void registerGoals() {
        this.friendsGoal = new SculkWakeUpFriendsGoal(this);

        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, this.friendsGoal);
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(4, new SculkMergeWithBlockGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
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
    public void tick() {
        super.tick();
        this.yBodyRot = this.getYRot();

        if (!this.level().isClientSide && this.spawnProtectionTicks > 0) {
            this.spawnProtectionTicks--;
        }

        if (this.level().isClientSide) {
            if (this.tickCount % 10 == 0) {
                Vec3 pos = this.position();
                for (int i = 0; i < 2; i++) {
                    this.level().addParticle(ParticleTypes.SCULK_SOUL,
                            pos.x + (this.random.nextDouble() - 0.5D) * 0.3D,
                            pos.y + this.random.nextDouble() * 0.2D,
                            pos.z + (this.random.nextDouble() - 0.5D) * 0.3D,
                            0, 0.01D, 0);
                }
            }

            if (this.tickCount % 40 == 0) {
                this.level().addParticle(ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY() + 0.1, this.getZ(),
                        0, 0.02, 0);
            }
        }
    }

    public boolean isSpawnProtected() {
        return this.spawnProtectionTicks > 0;
    }

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_BLOCK_CHARGE;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource source) {
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
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        BlockState below = level.getBlockState(pos.below());
        return isSculkHostBlock(below) ? 10.0F : super.getWalkTargetValue(pos, level);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entity) {
        return super.isAlliedTo(entity) || entity.getType().is(ModTags.Entities.SCULK_ALLIES);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    boolean isSculkHostBlock(BlockState state) {
        return state.is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get())
                || state.is(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get())
                || state.is(ModBlocks.INFESTED_SCULK_SAND.get())
                || state.is(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK.get());
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = super.doHurtTarget(target);
        if (flag && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.SCULK_INFECTION_EFFECT, 120, 0));
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.HOSTILE, 0.5F, 1.2F);
        }
        return flag;
    }

    // Custom burrow/merge behavior
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
                return;
            }

            LevelAccessor level = mob.level();
            BlockPos pos = mob.blockPosition().relative(dir);
            BlockState state = level.getBlockState(pos);

            if (((SculkParasiteEntity) mob).isSculkHostBlock(state)) {
                level.playSound(null, pos,
                        SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.HOSTILE, 0.6F, 1.1F);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                            pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                            6, 0.3D, 0.3D, 0.3D, 0.01D);
                }

                mob.discard();
            }
        }
    }

    // When hurt, wake up and spawn nearby parasites from sculk host blocks
    static class SculkWakeUpFriendsGoal extends Goal {
        private final SculkParasiteEntity parasite;
        private int lookForFriends;

        // FIX: hard cap on how many parasites can exist near this one
        private static final int MAX_NEARBY_PARASITES = 6;
        private static final double CROWD_CHECK_RADIUS = 10.0D;

        public SculkWakeUpFriendsGoal(SculkParasiteEntity parasite) {
            this.parasite = parasite;
            this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        }

        public void notifyHurt() {
            // FIX: spawn-protected parasites cannot trigger the wake-up goal,
            // breaking the chain reaction where new spawns immediately spawn more
            if (parasite.isSpawnProtected()) return;
            if (lookForFriends == 0) lookForFriends = this.adjustedTickDelay(20);
        }

        @Override
        public boolean canUse() {
            return lookForFriends > 0;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void tick() {
            --lookForFriends;
            if (lookForFriends > 0) return;

            if (!(parasite.level() instanceof ServerLevel serverLevel)) return;

            // FIX: count existing nearby parasites first — if already crowded, do nothing
            AABB searchBox = parasite.getBoundingBox().inflate(CROWD_CHECK_RADIUS);
            List<SculkParasiteEntity> nearby = serverLevel.getEntitiesOfClass(
                    SculkParasiteEntity.class, searchBox, e -> e != parasite
            );

            if (nearby.size() >= MAX_NEARBY_PARASITES) return;

            BlockPos center = parasite.blockPosition();
            RandomSource random = parasite.getRandom();

            // FIX: spawn limit is now dynamic — only spawn enough to reach the cap,
            // so the total nearby count never exceeds MAX_NEARBY_PARASITES
            int canSpawn = MAX_NEARBY_PARASITES - nearby.size();
            int spawned = 0;

            for (BlockPos pos : BlockPos.betweenClosed(
                    center.offset(-5, -3, -5),
                    center.offset(5, 3, 5))) {

                if (spawned >= canSpawn) break;

                BlockState state = serverLevel.getBlockState(pos);
                if (!parasite.isSculkHostBlock(state)) continue;

                BlockPos spawnPos = pos.above();
                if (!serverLevel.getBlockState(spawnPos).canBeReplaced()) continue;

                SculkParasiteEntity newParasite = ModEntities.SCULK_PARASITE.get().create(serverLevel);
                if (newParasite == null) continue;

                newParasite.moveTo(
                        spawnPos.getX() + 0.5D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5D,
                        random.nextFloat() * 360.0F,
                        0.0F
                );

                if (parasite.getTarget() != null) {
                    newParasite.setTarget(parasite.getTarget());
                }

                serverLevel.addFreshEntity(newParasite);

                serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                        spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                        4, 0.2D, 0.1D, 0.2D, 0.01D);

                spawned++;

                if (random.nextBoolean()) break;
            }
        }
    }
}