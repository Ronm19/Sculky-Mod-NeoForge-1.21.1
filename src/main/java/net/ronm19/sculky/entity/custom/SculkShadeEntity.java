package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SculkShadeEntity extends Vex implements Enemy {

    private int flickerTimer = 0;
    private boolean isFlickering = false;
    private int darknessCooldown = 0;
    private int dashCooldown = 0;
    private BlockPos boundOrigin;
    private static final int FLAG_IS_CHARGING = 1;
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;

    public SculkShadeEntity( EntityType<? extends Vex> type, Level level ) {
        super(type, level);
        this.xpReward = 18;
        this.moveControl = new SculkShadeMoveControl(this);
        this.navigation = new FlyingPathNavigation(this, level);
    }

    // -------------------------------
    // AI
    // -------------------------------
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        // Charge Melee
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.35D, true) {
            @Override
            public void start() {
                super.start();
                setIsCharging(true);
            }

            @Override
            public void stop() {
                super.stop();
                setIsCharging(false);
            }
        });

        // Floating wander
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new SculkShadeRandomMoveGoal());


        // Looking
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // -------------------------------
    // TICK LOGIC
    // -------------------------------
    @Override
    public void tick() {
        super.tick();

        // flicker → invisibility + teleport
        handleFlicker();

        // Darkness stare
        if (this.getTarget() instanceof Player player) {
            handleDarknessGaze(player);
        }

        // Avoid bright light
        avoidLight();

        // Jump-scare ghost dash
        if (this.getTarget() != null) {
            handleDashAttack(this.getTarget());
        }

        // Move slightly forward when charging
        if (isCharging()) {
            Vec3 look = this.getLookAngle();
            this.setDeltaMovement(this.getDeltaMovement().add(
                    look.x * 0.02,
                    look.y * 0.01,
                    look.z * 0.02
            ));
        }
    }

    // -------------------------------
    // Flicker System (Invis + Teleport)
    // -------------------------------
    private void handleFlicker() {
        flickerTimer++;

        // Chance to flicker every 60–100 ticks
        if (!isFlickering && flickerTimer > 60 && this.random.nextFloat() < 0.15f) {
            startFlicker();
        }

        // End flicker after ~60 ticks
        if (isFlickering && flickerTimer > 60) {
            endFlicker();
        }
    }

    private void startFlicker() {
        isFlickering = true;
        flickerTimer = 0;

        // invisibility
        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false));

        // random teleport 3-6 blocks
        double dx = (random.nextDouble() - 0.5) * 8;
        double dy = (random.nextDouble() - 0.3) * 4;
        double dz = (random.nextDouble() - 0.5) * 8;
        this.teleportTo(this.getX() + dx, this.getY() + dy, this.getZ() + dz);

        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.4f, 1.5f);
    }

    private void endFlicker() {
        isFlickering = false;
        flickerTimer = 0;
        this.removeEffect(MobEffects.INVISIBILITY);
    }

    // -------------------------------
    // Darkness Gaze
    // -------------------------------
    private void handleDarknessGaze( Player player ) {
        darknessCooldown++;

        if (darknessCooldown > 40) {
            double dist = this.distanceTo(player);

            if (dist <= 14) {
                double dot = this.getLookAngle().normalize().dot(player.getLookAngle().normalize());

                if (dot > 0.4) {
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false));

                    this.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 0.6f);
                }
            }
            darknessCooldown = 0;
        }
    }

    // -------------------------------
    // Jumpscare Dash Attack
    // -------------------------------
    private void handleDashAttack( LivingEntity target ) {
        dashCooldown++;

        // Occasional dash every 80–120 ticks
        if (dashCooldown > 80 && random.nextFloat() < 0.10f) {
            dashCooldown = 0;

            Vec3 behind = target.position().add(
                    -target.getLookAngle().x * 1.8,
                    0.2,
                    -target.getLookAngle().z * 1.8
            );

            // Teleport behind the player
            this.teleportTo(behind.x, behind.y, behind.z);

            // Ambience
            this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 0.8f, 1.2f);

            // Follow-up damage
            target.hurt(this.damageSources().mobAttack(this), 6.0F);
        }
    }

    // -------------------------------
    // Avoidance of Light
    // -------------------------------
    private void avoidLight() {
        BlockPos pos = this.blockPosition();
        if (this.level().getMaxLocalRawBrightness(pos) >= 12) {

            // flee movement
            Vec3 away = this.getDeltaMovement().add(
                    (random.nextDouble() - 0.5) * 0.4,
                    0.15,
                    (random.nextDouble() - 0.5) * 0.4
            );
            this.setDeltaMovement(away);

            this.playSound(SoundEvents.PHANTOM_SWOOP, 0.4f, 1.8f);
        }
    }

    // -------------------------------
    // Attributes
    // -------------------------------
    public static AttributeSupplier.Builder createSculkShadeAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 34.0D);
    }

    @Override
    protected @NotNull MovementEmission getMovementEmission() {
        return MovementEmission.NONE; // silent
    }

    @Override
    public boolean causeFallDamage( float distance, float damageMultiplier, DamageSource source ) {
        return false;
    }

    // -------------------------------
    // Charge Flag
    // -------------------------------
    public boolean isCharging() {
        return getShadeFlag(FLAG_IS_CHARGING);
    }

    public void setIsCharging( boolean charging ) {
        setShadeFlag(FLAG_IS_CHARGING, charging);
    }

    private boolean getShadeFlag( int mask ) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setShadeFlag( int mask, boolean value ) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) i |= mask;
        else i &= ~mask;
        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    @Override
    protected void defineSynchedData( SynchedEntityData.Builder builder ) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte) 0);
    }

    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos boundOrigin) {
        this.boundOrigin = boundOrigin;
    }

    // -------------------------------
    // Spawn Equipment
    // -------------------------------
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn( ServerLevelAccessor level, @NotNull DifficultyInstance difficulty,
                                         @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnData ) {

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.INFESTED_SCULK_SWORD.asItem()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);

        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    // -------------------------------
    // Sounds
    // -------------------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SENSOR_HIT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_ROAR;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( @NotNull DamageSource damageSource ) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(SculkShadeEntity.class, EntityDataSerializers.BYTE);
    }

    class SculkShadeMoveControl extends MoveControl {
        public SculkShadeMoveControl( SculkShadeEntity vex ) {
            super(vex);
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                Vec3 vec3 = new Vec3(this.wantedX - SculkShadeEntity.this.getX(), this.wantedY - SculkShadeEntity.this.getY(), this.wantedZ - SculkShadeEntity.this.getZ());
                double d0 = vec3.length();
                if (d0 < SculkShadeEntity.this.getBoundingBox().getSize()) {
                    this.operation = Operation.WAIT;
                    SculkShadeEntity.this.setDeltaMovement(SculkShadeEntity.this.getDeltaMovement().scale((double) 0.5F));
                } else {
                    SculkShadeEntity.this.setDeltaMovement(SculkShadeEntity.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05 / d0)));
                    if (SculkShadeEntity.this.getTarget() == null) {
                        Vec3 vec31 = SculkShadeEntity.this.getDeltaMovement();
                        SculkShadeEntity.this.setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float) Math.PI));
                        SculkShadeEntity.this.yBodyRot = SculkShadeEntity.this.getYRot();
                    } else {
                        double d2 = SculkShadeEntity.this.getTarget().getX() - SculkShadeEntity.this.getX();
                        double d1 = SculkShadeEntity.this.getTarget().getZ() - SculkShadeEntity.this.getZ();
                        SculkShadeEntity.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                        SculkShadeEntity.this.yBodyRot = SculkShadeEntity.this.getYRot();
                    }
                }
            }

        }
    }

    class SculkShadeRandomMoveGoal extends Goal {
        public SculkShadeRandomMoveGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !SculkShadeEntity.this.getMoveControl().hasWanted() && SculkShadeEntity.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = SculkShadeEntity.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = SculkShadeEntity.this.blockPosition();
            }

            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(SculkShadeEntity.this.random.nextInt(15) - 7, SculkShadeEntity.this.random.nextInt(11) - 5, SculkShadeEntity.this.random.nextInt(15) - 7);
                if (SculkShadeEntity.this.level().isEmptyBlock(blockpos1)) {
                    SculkShadeEntity.this.moveControl.setWantedPosition((double) blockpos1.getX() + (double) 0.5F, (double) blockpos1.getY() + (double) 0.5F, (double) blockpos1.getZ() + (double) 0.5F, (double) 0.25F);
                    if (SculkShadeEntity.this.getTarget() == null) {
                        SculkShadeEntity.this.getLookControl().setLookAt((double) blockpos1.getX() + (double) 0.5F, (double) blockpos1.getY() + (double) 0.5F, (double) blockpos1.getZ() + (double) 0.5F, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }
}

