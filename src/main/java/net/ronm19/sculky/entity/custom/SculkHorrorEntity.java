package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.worldgen.biome.ModBiomes;
import org.jetbrains.annotations.NotNull;

public class SculkHorrorEntity extends Monster implements Enemy {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;

    // -----------------------------
    // STATE FLAGS
    // -----------------------------
    private static final int FLAG_STEALTH = 1;
    private static final int FLAG_BURROWED = 2;
    private static final int FLAG_CEILING = 4;

    private int sonicCooldown = 0;
    private int burrowCooldown = 0;
    private int ambushCooldown = 0;

    public SculkHorrorEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 25;
    }

    // -----------------------------
    // ATTRIBUTES
    // -----------------------------
    public static AttributeSupplier.Builder createSculkHorrorAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 70D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 40D)
                .add(Attributes.ARMOR, 6D)
                .add(Attributes.ATTACK_DAMAGE, 10D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    // -----------------------------
    // TICK
    // -----------------------------
    @Override
    public void tick() {
        super.tick();

        if (sonicCooldown > 0) sonicCooldown--;
        if (burrowCooldown > 0) burrowCooldown--;
        if (ambushCooldown > 0) ambushCooldown--;

        // Ceiling gravity cancel
        if (isCeilingCrawling()) {
            this.setDeltaMovement(getDeltaMovement().multiply(1.0, 0.0, 1.0));
        }
    }

    // -----------------------------
    // GOALS
    // -----------------------------
    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Burrow ambush
        this.goalSelector.addGoal(1, new BurrowGoal(this));

        // Ceiling crawl
        this.goalSelector.addGoal(2, new CeilingCrawlGoal(this));

        // Sonic snap attack
        this.goalSelector.addGoal(3, new SonicSnapGoal(this));

        // Melee fallback
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, true));

        // Wander
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9));

        // Look
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ==================================================
    //   GOAL: BURROW
    // ==================================================
    private static class BurrowGoal extends Goal {
        private final SculkHorrorEntity horror;
        private int burrowTime = 0;

        public BurrowGoal(SculkHorrorEntity horror) {
            this.horror = horror;
        }

        @Override
        public boolean canUse() {
            return horror.burrowCooldown == 0
                    && horror.onGround()
                    && horror.getTarget() != null
                    && horror.distanceToSqr(horror.getTarget()) > 10;
        }

        @Override
        public void start() {
            horror.setBurrowed(true);
            burrowTime = 0;
            horror.burrowCooldown = 140; // 7 sec
            horror.level().playSound(null, horror,
                    SoundEvents.SCULK_BLOCK_BREAK, horror.getSoundSource(), 1F, 0.5F);
        }

        @Override
        public void tick() {
            burrowTime++;

            horror.setDeltaMovement(0, -0.3F, 0);

            if (burrowTime > 20) {
                LivingEntity target = horror.getTarget();
                if (target != null) {
                    Vec3 teleport = target.position().add(
                            horror.random.nextGaussian() * 1,
                            -1,
                            horror.random.nextGaussian() * 1
                    );
                    horror.teleportTo(teleport.x, teleport.y, teleport.z);
                }
                horror.setBurrowed(false);
            }
        }
    }

    // ==================================================
    //   GOAL: CEILING CRAWL
    // ==================================================
    private static class CeilingCrawlGoal extends Goal {

        private final SculkHorrorEntity horror;

        public CeilingCrawlGoal(SculkHorrorEntity horror) {
            this.horror = horror;
        }

        @Override
        public boolean canUse() {
            return horror.getTarget() != null
                    && horror.ambushCooldown == 0
                    && horror.level().getBlockState(horror.blockPosition().above()).isSolid();
        }

        @Override
        public void start() {
            horror.setCeilingCrawling(true);
            horror.ambushCooldown = 180; // 9 sec
        }

        @Override
        public void stop() {
            horror.setCeilingCrawling(false);
        }

        @Override
        public void tick() {
            if (!horror.level().getBlockState(horror.blockPosition().above()).isSolid()) {
                horror.setCeilingCrawling(false);
            }

            LivingEntity t = horror.getTarget();
            if (t != null && horror.distanceTo(t) < 2.5F) {
                // Drop-ambush
                horror.setCeilingCrawling(false);
                t.hurt(horror.damageSources().mobAttack(horror), 12F);
                horror.level().playSound(null, horror,
                        SoundEvents.SCULK_SHRIEKER_SHRIEK, horror.getSoundSource(), 1F, 0.8F);
            }
        }
    }

    // ==================================================
    //   GOAL: SONIC SNAP (SHORT RANGE)
    // ==================================================
    private static class SonicSnapGoal extends Goal {

        private final SculkHorrorEntity horror;
        private int charge = 0;

        public SonicSnapGoal(SculkHorrorEntity horror) {
            this.horror = horror;
        }

        @Override
        public boolean canUse() {
            return horror.getTarget() != null
                    && horror.sonicCooldown == 0
                    && horror.distanceToSqr(horror.getTarget()) <= 16;
        }

        @Override
        public void start() {
            charge = 0;
        }

        @Override
        public void tick() {
            charge++;

            if (charge == 10) {
                horror.level().playSound(
                        null, horror,
                        SoundEvents.WARDEN_SONIC_CHARGE,
                        horror.getSoundSource(), 1F, 1.2F
                );
            }

            if (charge >= 20) {
                fireSnap();
                horror.sonicCooldown = 80;
            }
        }

        private void fireSnap() {
            LivingEntity t = horror.getTarget();
            if (t == null) return;

            horror.level().playSound(null, horror,
                    SoundEvents.WARDEN_SONIC_BOOM, horror.getSoundSource(), 1F, 1F);

            t.hurt(horror.damageSources().sonicBoom(horror), 9F);
            t.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
        }
    }

    // -----------------------------
    // FLAGS
    // -----------------------------
    private void setFlag(int flag, boolean enabled) {
        byte current = this.entityData.get(DATA_FLAGS_ID);
        if (enabled) current |= (byte) flag;
        else current &= (byte) ~flag;
        this.entityData.set(DATA_FLAGS_ID, current);
    }

    public boolean isBurrowed() {
        return (this.entityData.get(DATA_FLAGS_ID) & FLAG_BURROWED) != 0;
    }

    public void setBurrowed(boolean b) {
        setFlag(FLAG_BURROWED, b);
    }

    public boolean isCeilingCrawling() {
        return (this.entityData.get(DATA_FLAGS_ID) & FLAG_CEILING) != 0;
    }

    public void setCeilingCrawling(boolean b) {
        setFlag(FLAG_CEILING, b);
    }

    // -----------------------------
    // SOUNDS
    // -----------------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_CLICKING;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_CATALYST_BLOOM;
    }

    // -----------------------------
    // SPAWN RULES
    // -----------------------------
    public static boolean canSpawn(
            EntityType<SculkHorrorEntity> type,
            LevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random
    ) {

        boolean isDeepDark = level.getBiome(pos).is(Biomes.DEEP_DARK);

        boolean isSculkForest =
                level.getBiome(pos).is(ModBiomes.SCULK_FOREST);

        boolean darkEnough = level.getRawBrightness(pos, 0) < 8;

        return (isDeepDark || isSculkForest) && darkEnough;
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(SculkHorrorEntity.class, EntityDataSerializers.BYTE);
    }
}
