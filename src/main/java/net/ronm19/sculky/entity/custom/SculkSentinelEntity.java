package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.util.ModTags;

import java.util.EnumSet;
import java.util.List;

public class SculkSentinelEntity extends Monster {
    private int attackAnimationTick;
    private int offerFlowerTick;

    public SculkSentinelEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
    }

    // --------------------
    // Goals / AI Behavior
    // --------------------
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SculkSentinelRoarGoal(this));
        this.goalSelector.addGoal(2, new SculkSentinelChargeGoal(this, 1.3D, 40));
        this.goalSelector.addGoal(3, new SculkSentinelGuardBlockGoal(this, 60));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    // --------------------
    // Attributes
    // --------------------
    public static AttributeSupplier.Builder createSculkSentinelAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    // --------------------
    // Sounds
    // --------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_HEARTBEAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SHRIEKER_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.WARDEN_STEP, 0.25F, 0.8F + this.random.nextFloat() * 0.2F);
    }

    // --------------------
    // Custom Abilities
    // --------------------
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        // Ambient heartbeat
        if (this.tickCount % 200 == 0 && this.level() instanceof ServerLevel server) {
            server.playSound(null, this.blockPosition(),
                    SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 0.6F, 0.9F);

            server.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    10, 0.4, 0.4, 0.4, 0.02);
        }


        if (this.attackAnimationTick > 0) --this.attackAnimationTick;
        if (this.offerFlowerTick > 0) --this.offerFlowerTick;

    }


    public boolean isSculkAlly(Entity entity) {
        return entity.getType().is(ModTags.Entities.SCULK_ALLIES);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return super.isAlliedTo(entity) || isSculkAlly(entity);
    }

    public int getAttackAnimationTick() { return this.attackAnimationTick; }
    public int getOfferFlowerTick() { return this.offerFlowerTick; }

    private float getAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.attackAnimationTick = 10;
        this.level().broadcastEntityEvent(this, (byte) 4);

        float baseDamage = this.getAttackDamage();
        float finalDamage = (int) baseDamage > 0 ? baseDamage / 2.0F + this.random.nextInt((int) baseDamage) : baseDamage;
        DamageSource source = this.damageSources().mobAttack(this);
        boolean flag = entity.hurt(source, finalDamage);

        if (flag) {
            double resist = entity instanceof LivingEntity living ? living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0.0D;
            double knock = Math.max(0.0D, 1.0D - resist);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.4D * knock, 0.0D));

            if (this.level() instanceof ServerLevel server)
                EnchantmentHelper.doPostAttackEffects(server, entity, source);
        }

        this.playSound(SoundEvents.SCULK_BLOCK_CHARGE, 1.0F, 1.0F);
        return flag;
    }

    // --------------------
    // Spawn Rules
    // --------------------
    public static boolean checkSculkSentinelSpawnRules(EntityType<SculkSentinelEntity> type, LevelAccessor level,
                                                       MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get())
                && level.getMaxLocalRawBrightness(pos) < 8
                && Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    // ---------------------------------------------
    // Inner Custom Goals
    // ---------------------------------------------
    static class SculkSentinelRoarGoal extends Goal {
        private final SculkSentinelEntity mob;
        private int cooldown = 0;

        public SculkSentinelRoarGoal(SculkSentinelEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) cooldown--;
            List<Player> players = mob.level().getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(6));
            return cooldown == 0 && !players.isEmpty();
        }

        @Override
        public void start() {
            cooldown = mob.random.nextInt(200) + 200;
            if (!mob.level().isClientSide()) {
                ((ServerLevel) mob.level()).playSound(null, mob.blockPosition(),
                        SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.0F, 0.8F);
                ((ServerLevel) mob.level()).sendParticles(
                        net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                        mob.getX(), mob.getY() + 1.5, mob.getZ(),
                        20, 0.5, 0.5, 0.5, 0.02);
            }

            // Knockback nearby entities (ignore allies)
            double radius = 5.0D;
            for (LivingEntity entity : mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(radius))) {
                if (entity == mob || mob.isSculkAlly(entity)) continue;
                Vec3 dir = entity.position().subtract(mob.position()).normalize().scale(1.3);
                entity.push(dir.x, 0.8, dir.z);
            }
        }
    }

    static class SculkSentinelGuardBlockGoal extends Goal {
        private final SculkSentinelEntity mob;
        private final int guardDuration;
        private int guardTicks;

        public SculkSentinelGuardBlockGoal(SculkSentinelEntity mob, int guardDuration) {
            this.mob = mob;
            this.guardDuration = guardDuration;
        }

        @Override
        public boolean canUse() {
            return mob.level().getBlockState(mob.blockPosition().below()).is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get());
        }

        @Override
        public void start() {
            guardTicks = guardDuration;
            mob.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public void tick() {
            guardTicks--;
            if (mob.level() instanceof ServerLevel server && guardTicks % 20 == 0) {
                server.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_CHARGE_POP,
                        mob.getX(), mob.getY() + 1.5, mob.getZ(), 4, 0.3, 0.3, 0.3, 0.02);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return guardTicks > 0 && mob.level().getBlockState(mob.blockPosition().below())
                    .is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get());
        }
    }

    static class SculkSentinelChargeGoal extends Goal {
        private final SculkSentinelEntity mob;
        private final double speed;
        private final int cooldown;
        private int ticks;

        public SculkSentinelChargeGoal(SculkSentinelEntity mob, double speed, int cooldown) {
            this.mob = mob;
            this.speed = speed;
            this.cooldown = cooldown;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = mob.getTarget();
            return target != null && mob.distanceTo(target) < 12.0F && ticks <= 0;
        }

        @Override
        public void start() {
            ticks = cooldown;
            mob.playSound(SoundEvents.WARDEN_SONIC_BOOM, 1.2F, 0.9F);

            LivingEntity target = mob.getTarget();
            if (target != null) {
                Vec3 dir = target.position().subtract(mob.position()).normalize().scale(speed);
                mob.setDeltaMovement(dir.x, 0.3D, dir.z);

                if (mob.level() instanceof ServerLevel server) {
                    server.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                            mob.getX(), mob.getY() + 1.4D, mob.getZ(),
                            15, 0.5, 0.4, 0.5, 0.02);
                }
            }
        }

        @Override
        public void tick() {
            if (ticks > 0) ticks--;

            if (mob.level() instanceof ServerLevel server) {
                Vec3 pos = mob.position();
                for (int i = 0; i < 3; i++) {
                    double x = pos.x - mob.getLookAngle().x * (0.5 + i * 0.3);
                    double y = pos.y + 1.0;
                    double z = pos.z - mob.getLookAngle().z * (0.5 + i * 0.3);
                    server.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                            x, y, z, 1, 0.1, 0.1, 0.1, 0.005);
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
}
