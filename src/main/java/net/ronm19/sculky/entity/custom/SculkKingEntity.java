package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.ronm19.sculky.util.SculkFactionHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SculkKingEntity extends Monster implements Enemy {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.sculky.sculk_king"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private int currentPhase = 1;

    private int axeSlamCooldown = 20 * 5;
    private int summonCooldown = 20 * 12;
    private int roarPulseCooldown = 20 * 18;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();

    private static final EntityDataAccessor<Integer> ATTACK_ANIMATION_TICKS =
            SynchedEntityData.defineId(SculkKingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ROAR_ANIMATION_TICKS =
            SynchedEntityData.defineId(SculkKingEntity.class, EntityDataSerializers.INT);

    private static final float MAX_PLAYER_DAMAGE_PER_HIT = 14.0F;
    private static final float MODDED_WEAPON_DAMAGE_MULTIPLIER = 0.50F;

    public SculkKingEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 250;

        this.bossEvent.setDarkenScreen(true);
        this.bossEvent.setCreateWorldFog(false);

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.KINGS_AXE.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_ANIMATION_TICKS, 0);
        builder.define(ROAR_ANIMATION_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.getAttackAnimationTicks() > 0) {
                this.setAttackAnimationTicks(this.getAttackAnimationTicks() - 1);
            }

            if (this.getRoarAnimationTicks() > 0) {
                this.setRoarAnimationTicks(this.getRoarAnimationTicks() - 1);
            }
        } else {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        this.idleAnimationState.startIfStopped(this.tickCount);

        if (this.getAttackAnimationTicks() > 0) {
            this.attackAnimationState.startIfStopped(this.tickCount);
        } else {
            this.attackAnimationState.stop();
        }

        if (this.getRoarAnimationTicks() > 0) {
            this.roarAnimationState.startIfStopped(this.tickCount);
        } else {
            this.roarAnimationState.stop();
        }
    }

    public int getAttackAnimationTicks() {
        return this.entityData.get(ATTACK_ANIMATION_TICKS);
    }

    public void setAttackAnimationTicks(int ticks) {
        this.entityData.set(ATTACK_ANIMATION_TICKS, ticks);
    }

    public int getRoarAnimationTicks() {
        return this.entityData.get(ROAR_ANIMATION_TICKS);
    }

    public void setRoarAnimationTicks(int ticks) {
        this.entityData.set(ROAR_ANIMATION_TICKS, ticks);
    }

    public void triggerAttackAnimation() {
        this.setAttackAnimationTicks(16);
    }

    public void triggerRoarAnimation() {
        this.setRoarAnimationTicks(32);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Basic prototype combat.
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));

        // Movement / presence.
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.55D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // Targets.
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createSculkKingAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 320.0D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        float healthPercent = this.getHealth() / this.getMaxHealth();
        this.bossEvent.setProgress(Mth.clamp(healthPercent, 0.0F, 1.0F));

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int phase = this.getPhase();

        if (phase != this.currentPhase) {
            this.currentPhase = phase;
            this.onPhaseChanged(serverLevel, phase);
        }

        this.tickKingAbilities(serverLevel, phase);

        int auraRate = phase == 3 ? 12 : phase == 2 ? 22 : 35;

        if (this.tickCount % auraRate == 0) {
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY() + 2.1D,
                    this.getZ(),
                    phase == 3 ? 14 : phase == 2 ? 9 : 6,
                    0.35D,
                    0.45D,
                    0.35D,
                    0.015D
            );
        }
    }

    private int getPhase() {
        float healthPercent = this.getHealth() / this.getMaxHealth();

        if (healthPercent <= 0.30F) {
            return 3;
        }

        if (healthPercent <= 0.65F) {
            return 2;
        }

        return 1;
    }

    private void onPhaseChanged(ServerLevel level, int phase) {
        this.triggerRoarAnimation();

        if (phase == 2) {
            this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);

            this.axeSlamCooldown = 20 * 3;
            this.summonCooldown = 20 * 4;

            if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.27D);
            }

            level.playSound(null, this.blockPosition(),
                    SoundEvents.WARDEN_ROAR,
                    SoundSource.HOSTILE,
                    1.4F,
                    0.75F);

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY() + 1.8D,
                    this.getZ(),
                    120,
                    0.85D,
                    1.0D,
                    0.85D,
                    0.07D
            );
        }

        if (phase == 3) {
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);

            this.axeSlamCooldown = 20 * 2;
            this.summonCooldown = 20 * 3;
            this.roarPulseCooldown = 20 * 4;

            if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30D);
            }

            if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(22.0D);
            }

            level.playSound(null, this.blockPosition(),
                    SoundEvents.WARDEN_ANGRY,
                    SoundSource.HOSTILE,
                    1.6F,
                    0.65F);

            level.playSound(null, this.blockPosition(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE,
                    1.2F,
                    0.45F);

            level.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    this.getX(),
                    this.getY() + 1.5D,
                    this.getZ(),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY() + 1.8D,
                    this.getZ(),
                    180,
                    1.1D,
                    1.2D,
                    1.1D,
                    0.09D
            );
        }
    }

    private boolean isValidBossTarget(LivingEntity entity) {
        return entity.isAlive()
                && entity != this
                && !SculkFactionHelper.isWildSculkAlly(entity);
    }

    private void tickKingAbilities(ServerLevel level, int phase) {
        LivingEntity target = this.getTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        if (this.axeSlamCooldown > 0) {
            this.axeSlamCooldown--;
        }

        if (this.summonCooldown > 0) {
            this.summonCooldown--;
        }

        if (this.roarPulseCooldown > 0) {
            this.roarPulseCooldown--;
        }

        if (this.axeSlamCooldown <= 0) {
            this.kingAxeSlam(level, phase);

            this.axeSlamCooldown = switch (phase) {
                case 3 -> 20 * 5;
                case 2 -> 20 * 7;
                default -> 20 * 10;
            };
        }

        if (phase >= 2 && this.summonCooldown <= 0) {
            this.summonRoyalMinions(level, phase);

            this.summonCooldown = phase == 3 ? 20 * 13 : 20 * 18;
        }

        if (phase >= 3 && this.roarPulseCooldown <= 0) {
            this.kingRoarPulse(level);

            this.roarPulseCooldown = 20 * 12;
        }
    }

    private void kingAxeSlam(ServerLevel level, int phase) {
        this.triggerAttackAnimation();

        double radius = phase == 3 ? 5.5D : phase == 2 ? 4.75D : 4.0D;
        float damage = phase == 3 ? 12.0F : phase == 2 ? 9.0F : 6.0F;
        double knockback = phase == 3 ? 2.1D : phase == 2 ? 1.65D : 1.25D;

        AABB area = this.getBoundingBox().inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                this::isValidBossTarget
        )) {
            entity.hurt(this.damageSources().mobAttack(this), damage);

            entity.knockback(
                    knockback,
                    this.getX() - entity.getX(),
                    this.getZ() - entity.getZ()
            );

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    entity.getX(),
                    entity.getY() + 0.9D,
                    entity.getZ(),
                    10,
                    0.28D,
                    0.28D,
                    0.28D,
                    0.035D
            );
        }

        level.playSound(null, this.blockPosition(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                SoundSource.HOSTILE,
                1.25F,
                0.62F);

        level.playSound(null, this.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.HOSTILE,
                0.55F,
                0.75F);

        level.sendParticles(
                ParticleTypes.SONIC_BOOM,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private void kingRoarPulse(ServerLevel level) {
        this.triggerRoarAnimation();

        double radius = 9.0D;
        AABB area = this.getBoundingBox().inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                this::isValidBossTarget
        )) {
            entity.hurt(this.damageSources().mobAttack(this), 7.0F);

            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 4, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 4, 0));

            entity.knockback(
                    1.65D,
                    this.getX() - entity.getX(),
                    this.getZ() - entity.getZ()
            );
        }

        level.playSound(null, this.blockPosition(),
                SoundEvents.WARDEN_ROAR,
                SoundSource.HOSTILE,
                1.5F,
                0.7F);

        level.playSound(null, this.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE,
                0.9F,
                0.45F);

        level.sendParticles(
                ParticleTypes.SONIC_BOOM,
                this.getX(),
                this.getY() + 1.5D,
                this.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.6D,
                this.getZ(),
                140,
                1.2D,
                0.9D,
                1.2D,
                0.08D
        );
    }

    private void summonRoyalMinions(ServerLevel level, int phase) {
        this.triggerRoarAnimation();

        int crownedSculkmites = phase == 3 ? 3 : 2;

        for (int i = 0; i < crownedSculkmites; i++) {
            BlockPos spawnPos = this.findNearbySummonPos();
            this.spawnMinion(level, ModEntities.CROWNED_SCULKMITE.get(), spawnPos);
        }

        if (phase == 3 && this.random.nextFloat() < 0.55F) {
            BlockPos spawnPos = this.findNearbySummonPos();
            this.spawnMinion(level, ModEntities.ROYAL_SCULK_KNIGHT.get(), spawnPos);
        }

        level.playSound(null, this.blockPosition(),
                SoundEvents.EVOKER_PREPARE_SUMMON,
                SoundSource.HOSTILE,
                1.1F,
                0.6F);

        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.5D,
                this.getZ(),
                80,
                0.9D,
                0.7D,
                0.9D,
                0.06D
        );
    }

    private BlockPos findNearbySummonPos() {
        return this.blockPosition().offset(
                this.random.nextInt(7) - 3,
                1,
                this.random.nextInt(7) - 3
        );
    }

    private void spawnMinion(ServerLevel level, EntityType<? extends Mob> entityType, BlockPos pos) {
        Mob minion = entityType.create(level);

        if (minion == null) {
            return;
        }

        minion.moveTo(
                pos.getX() + 0.5D,
                pos.getY() + 0.1D,
                pos.getZ() + 0.5D,
                this.random.nextFloat() * 360.0F,
                0.0F
        );

        minion.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
        level.addFreshEntity(minion);

        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                pos.getX() + 0.5D,
                pos.getY() + 0.7D,
                pos.getZ() + 0.5D,
                18,
                0.25D,
                0.35D,
                0.25D,
                0.04D
        );
    }



    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        this.triggerAttackAnimation();

        boolean hurt = super.doHurtTarget(entity);

        if (hurt && entity instanceof LivingEntity target) {
            target.knockback(
                    1.75D,
                    this.getX() - target.getX(),
                    this.getZ() - target.getZ()
            );

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        null,
                        this.blockPosition(),
                        SoundEvents.WARDEN_ATTACK_IMPACT,
                        SoundSource.HOSTILE,
                        0.85F,
                        0.75F
                );

                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        target.getX(),
                        target.getY() + 1.0D,
                        target.getZ(),
                        14,
                        0.35D,
                        0.35D,
                        0.35D,
                        0.03D
                );
            }
        }

        return hurt;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (!this.level().isClientSide && source.getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();

            if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                if (!weapon.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(weapon.getItem());

                    if (!itemId.getNamespace().equals("minecraft")) {
                        amount *= MODDED_WEAPON_DAMAGE_MULTIPLIER;
                    }
                }

                amount = Math.min(amount, MAX_PLAYER_DAMAGE_PER_HIT);
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void onAddedToLevel() {
        this.triggerRoarAnimation();
        super.onAddedToLevel();

        if (!this.level().isClientSide) {
            this.setPersistenceRequired();
            this.playSpawnPolish();
        }
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (!this.level().isClientSide) {
            this.playDeathPolish();
        }

        super.die(damageSource);
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level,
                                       @NotNull DamageSource damageSource,
                                       boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        this.spawnAtLocation(new ItemStack(ModItems.KINGS_AXE.get()));
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entity) {
        return super.isAlliedTo(entity) || SculkFactionHelper.isWildSculkAlly(entity);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        if (SculkFactionHelper.isWildSculkAlly(target)) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && SculkFactionHelper.isWildSculkAlly(target)) {
            super.setTarget(null);
            return;
        }

        super.setTarget(target);
    }

    private void playSpawnPolish() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE,
                1.6F,
                0.45F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_EMERGE,
                SoundSource.HOSTILE,
                1.2F,
                0.65F
        );

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.6D,
                this.getZ(),
                120,
                0.85D,
                1.1D,
                0.85D,
                0.06D
        );

        serverLevel.sendParticles(
                ParticleTypes.SONIC_BOOM,
                this.getX(),
                this.getY() + 1.4D,
                this.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private void playDeathPolish() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_DEATH,
                SoundSource.HOSTILE,
                1.2F,
                0.8F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE,
                1.2F,
                0.35F
        );

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.8D,
                this.getZ(),
                160,
                1.0D,
                1.0D,
                1.0D,
                0.075D
        );

        serverLevel.sendParticles(
                ParticleTypes.SONIC_BOOM,
                this.getX(),
                this.getY() + 1.6D,
                this.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }
}