package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.projectile.SculkFangsEntity;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SculkEvokerEntity extends Evoker {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.sculky.sculk_evoker"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private static final int MAX_ACTIVE_SCULK_SPIRITS = 2;
    private static final int SCULK_SPIRIT_LIFETIME = 20 * 26;

    private static final float MAX_PLAYER_DAMAGE_PER_HIT = 8.0F;
    private static final float MODDED_WEAPON_DAMAGE_MULTIPLIER = 0.45F;

    public SculkEvokerEntity(EntityType<? extends Evoker> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 45;
        this.bossEvent.setDarkenScreen(false);
        this.bossEvent.setCreateWorldFog(false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Keeps spell arm pose while casting.
        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());

        // Main mini-boss spells.
        this.goalSelector.addGoal(2, new SculkSummonSpiritSpellGoal());
        this.goalSelector.addGoal(3, new SculkFangsSpellGoal());

        // Basic movement / looking.
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        // Targets.
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createSculkEvokerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.FOLLOW_RANGE, 36.0D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.60D);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.bossEvent.setProgress(Mth.clamp(this.getHealth() / this.getMaxHealth(), 0.0F, 1.0F));
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (!this.level().isClientSide && source.getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();

            // Do not mess with /kill, void, or other bypass damage.
            if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {

                // If the player is using a non-vanilla item, reduce the damage first.
                if (!weapon.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(weapon.getItem());

                    if (!itemId.getNamespace().equals("minecraft")) {
                        amount *= MODDED_WEAPON_DAMAGE_MULTIPLIER;
                    }
                }

                // Hard cap so huge weapons cannot melt the boss.
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
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        }

        return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    private void spawnSculkFangsLine() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity target = this.getTarget();

        Vec3 direction;

        if (target != null) {
            direction = target.position().subtract(this.position());
        } else {
            direction = this.getLookAngle();
        }

        direction = new Vec3(direction.x, 0.0D, direction.z);

        if (direction.lengthSqr() < 0.0001D) {
            direction = this.getLookAngle();
            direction = new Vec3(direction.x, 0.0D, direction.z);
        }

        direction = direction.normalize();

        float fangRotation = (float) Mth.atan2(direction.z, direction.x);

        boolean enraged = this.getHealth() <= this.getMaxHealth() * 0.5F;

        int fangCount = enraged ? 9 : 7;
        double spacing = enraged ? 1.15D : 1.25D;
        double startDistance = 1.25D;

        for (int i = 0; i < fangCount; i++) {
            double distance = startDistance + i * spacing;

            double x = this.getX() + direction.x * distance;
            double z = this.getZ() + direction.z * distance;
            double y = findFangY(serverLevel, x, z, this.getY());

            SculkFangsEntity fangs = new SculkFangsEntity(
                    serverLevel,
                    x,
                    y,
                    z,
                    fangRotation,
                    i * 2,
                    this
            );

            serverLevel.addFreshEntity(fangs);
        }
    }


    private double findFangY(ServerLevel level, double x, double z, double startY) {
        for (int i = 0; i < 8; i++) {
            BlockPos pos = BlockPos.containing(x, startY - i, z);
            BlockPos below = pos.below();

            if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                return pos.getY();
            }
        }

        for (int i = 1; i <= 4; i++) {
            BlockPos pos = BlockPos.containing(x, startY + i, z);
            BlockPos below = pos.below();

            if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                return pos.getY();
            }
        }

        return startY;
    }

    private void summonSculkSpirits() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int nearbySpirits = this.level()
                .getEntitiesOfClass(
                        SculkSpiritEntity.class,
                        this.getBoundingBox().inflate(18.0D)
                )
                .size();

        int availableSlots = MAX_ACTIVE_SCULK_SPIRITS - nearbySpirits;

        if (availableSlots <= 0) {
            return;
        }

        int count = Math.min(1, availableSlots);

        for (int i = 0; i < count; i++) {
            SculkSpiritEntity spirit = ModEntities.SCULK_SPIRIT.get().create(serverLevel);

            if (spirit == null) {
                continue;
            }

            BlockPos spawnPos = this.blockPosition().offset(
                    this.random.nextInt(5) - 2,
                    1 + this.random.nextInt(2),
                    this.random.nextInt(5) - 2
            );

            spirit.moveTo(
                    spawnPos.getX() + 0.5D,
                    spawnPos.getY() + 0.5D,
                    spawnPos.getZ() + 0.5D,
                    this.random.nextFloat() * 360.0F,
                    0.0F
            );

            spirit.setOwner(this);
            spirit.setBoundOrigin(this.blockPosition());
            spirit.setLimitedLife(SCULK_SPIRIT_LIFETIME);

            serverLevel.addFreshEntity(spirit);
        }
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
                1.2F,
                0.65F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.EVOKER_PREPARE_SUMMON,
                SoundSource.HOSTILE,
                1.0F,
                0.7F
        );

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                60,
                0.65D,
                0.75D,
                0.65D,
                0.04D
        );

        serverLevel.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                this.getX(),
                this.getY() + 0.6D,
                this.getZ(),
                24,
                0.45D,
                0.35D,
                0.45D,
                0.025D
        );
    }

    private void playFangCastPolish() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.15D,
                this.getZ(),
                18,
                0.35D,
                0.45D,
                0.35D,
                0.025D
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_SONIC_CHARGE,
                SoundSource.HOSTILE,
                0.65F,
                1.35F
        );
    }

    private void playSummonCastPolish() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(
                ParticleTypes.SOUL,
                this.getX(),
                this.getY() + 1.2D,
                this.getZ(),
                30,
                0.55D,
                0.5D,
                0.55D,
                0.03D
        );

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                24,
                0.45D,
                0.45D,
                0.45D,
                0.035D
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
                0.75F,
                1.45F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE,
                0.9F,
                0.55F
        );

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                90,
                0.85D,
                0.7D,
                0.85D,
                0.06D
        );

        serverLevel.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                this.getX(),
                this.getY() + 0.8D,
                this.getZ(),
                35,
                0.6D,
                0.45D,
                0.6D,
                0.04D
        );

        serverLevel.sendParticles(
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

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        this.spawnAtLocation(new ItemStack(ModItems.SCULK_FANG_SCEPTER.get()));
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();

        if (!this.level().isClientSide) {
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

    class SculkFangsSpellGoal extends SpellcasterUseSpellGoal {
        @Override
        public boolean canUse() {
            return super.canUse() && SculkEvokerEntity.this.getTarget() != null;
        }

        @Override
        protected void performSpellCasting() {
            SculkEvokerEntity.this.playFangCastPolish();
            SculkEvokerEntity.this.spawnSculkFangsLine();
        }

        @Override
        protected int getCastWarmupTime() {
            return 20;
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return SculkEvokerEntity.this.getHealth() <= SculkEvokerEntity.this.getMaxHealth() * 0.5F ? 55 : 70;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.FANGS;
        }
    }

    class SculkSummonSpiritSpellGoal extends SpellcasterUseSpellGoal {
        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }

            if (SculkEvokerEntity.this.getTarget() == null) {
                return false;
            }

            int nearbySpirits = SculkEvokerEntity.this.level()
                    .getEntitiesOfClass(
                            SculkSpiritEntity.class,
                            SculkEvokerEntity.this.getBoundingBox().inflate(18.0D)
                    )
                    .size();

            return nearbySpirits < MAX_ACTIVE_SCULK_SPIRITS;
        }

        @Override
        protected void performSpellCasting() {
            SculkEvokerEntity.this.summonSculkSpirits();
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 20 * 30;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
        }
    }
}