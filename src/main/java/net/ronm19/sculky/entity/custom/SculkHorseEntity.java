package net.ronm19.sculky.entity.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.api.interfaces.AbilityUser;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SculkHorseEntity extends AbstractHorse implements AbilityUser {

    /* ============================= */
    /*        STATE FLAGS            */
    /* ============================= */

    private boolean isCharging = false;
    private boolean pendingSlam = false;


    /* ============================= */
    /*        COOLDOWNS (TICKS)      */
    /* ============================= */

    private int dashCooldown = 0;
    private int slamCooldown = 0;
    private int chargeCooldown = 0;
    private int chargeTicks = 0;

    // Non-ability timer (echo sense heartbeat)
    private int echoPulseTimer = 0;

    /* ============================= */
    /*        CONSTANTS              */
    /* ============================= */

    // Dash & Slam
    private static final float DASH_FORCE = 2.2F;

    private static final int SLAM_COOLDOWN_TICKS = 90; // 4.5 seconds
    private static final int DASH_COOLDOWN_TICKS = 90; // 4.5 seconds

    // Charge
    private static final int CHARGE_DURATION_TICKS = 30;   // 1.5 seconds
    private static final int CHARGE_COOLDOWN_TICKS = 80;   // 4 seconds


    /* ============================= */
    /*        SYNCED DATA            */
    /* ============================= */

    private static final EntityDataAccessor<Boolean> TAMED =
            SynchedEntityData.defineId(SculkHorseEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ANGRY =
            SynchedEntityData.defineId(SculkHorseEntity.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TAMED, false);
        builder.define(ANGRY, false);
    }

    /* ============================= */
    /*        CONSTRUCTOR            */
    /* ============================= */

    public SculkHorseEntity(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    /* ============================= */
    /*        ATTRIBUTES             */
    /* ============================= */

    public static AttributeSupplier.Builder createSculkHorseAttributes() {
        return Horse.createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.JUMP_STRENGTH, 1.0D);
    }

    /* ============================= */
    /*        SPAWNING               */
    /* ============================= */

    public static boolean checkSculkHorseSpawnRules(
            EntityType<SculkHorseEntity> type,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getBlockState(pos.below()).is(BlockTags.SCULK_REPLACEABLE)
                && pos.getY() > 0
                && level.getMaxLocalRawBrightness(pos) < 8;
    }


    // ------------------------------
    // CONVERSION FROM LIGHTNING
    // ------------------------------

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        super.thunderHit(level, lightning);

        BlockPos pos = this.blockPosition();

        if (!level.getBlockState(pos.below()).is(Blocks.SCULK)) return;

        SculkHorseEntity newHorse = ModEntities.SCULK_HORSE.get().create(level);
        if (newHorse == null) return;

        newHorse.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());

        if (this.isTamed() && this.getOwner() instanceof Player owner) {
            newHorse.tame(owner);
        }

        newHorse.setHealth(this.getHealth());
        if (this.hasCustomName()) newHorse.setCustomName(this.getCustomName());

        level.addFreshEntity(newHorse);
        this.discard();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    // ------------------------------
    // GOALS
    // ------------------------------
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.15D));
        goalSelector.addGoal(3, new BreedGoal(this, 1.0D, SculkHorseEntity.class));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        if (this.canPerformRearing()) {
            goalSelector.addGoal(9, new RandomStandGoal(this));
        }

        goalSelector.addGoal(3,
                new TemptGoal(this, 1.25D,
                        stack -> stack.is(ItemTags.HORSE_TEMPT_ITEMS),
                        false)
        );
    }


    // ------------------------------
    // ABILITY: SCULK DASH
    // ------------------------------
    public void tryDash() {
        if (!(getControllingPassenger() instanceof Player)) return;
        if (dashCooldown > 0) return;

        dashCooldown = DASH_COOLDOWN_TICKS;

        Vec3 dir = this.getLookAngle().scale(DASH_FORCE);
        this.setDeltaMovement(dir);
        this.hasImpulse = true;

        level().playSound(null, blockPosition(),
                SoundEvents.WARDEN_SONIC_BOOM,
                SoundSource.HOSTILE, 1.2F, 1.0F);
    }

    // ------------------------------
    // ABILITY: GROUND SLAM
    // ------------------------------
    public void tryGroundSlam() {
        if (slamCooldown > 0 || onGround()) return;

        slamCooldown = SLAM_COOLDOWN_TICKS;
        pendingSlam = true;

        setDeltaMovement(getDeltaMovement().x, -2.5F, getDeltaMovement().z);
    }

    @Override
    public void tick() {
        super.tick();

        if (dashCooldown > 0) dashCooldown--;
        if (slamCooldown > 0) slamCooldown--;
        if (chargeCooldown > 0) chargeCooldown--;

        // Slam impact
        if (pendingSlam && onGround()) {
            pendingSlam = false;
            doSlamImpact();
        }

        // Echo sense
        if (++echoPulseTimer % 40 == 0) {
            detectEnemies();
        }

        // Charging logic
        if (isCharging) {
            chargeTicks++;

            // light sustain instead of constant shove
            Vec3 sustain = getLookAngle().scale(0.15);
            setDeltaMovement(
                    getDeltaMovement().x * 0.9 + sustain.x,
                    getDeltaMovement().y,
                    getDeltaMovement().z * 0.9 + sustain.z
            );

            applyChargeKnockback();

            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        getX(), getY() + 1.2, getZ(),
                        4, 0.2, 0.2, 0.2, 0.01
                );
            }

            if (chargeTicks >= CHARGE_DURATION_TICKS) {
                isCharging = false;
                chargeTicks = 0;
                chargeCooldown = CHARGE_COOLDOWN_TICKS;
            }
        }
    }

    // ------------------------------
    // SLAM DAMAGE + PARTICLES
    // ------------------------------
    private void doSlamImpact() {
        this.level().playSound(null, this.blockPosition(),
                SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 1.0F, 0.9F);

        for (LivingEntity e : this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(3.0F),
                ent -> ent != this && ent != this.getControllingPassenger())) {

            e.hurt(this.damageSources().mobAttack(this), 6.0F);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
        }
    }

    // ------------------------------
    // MOB INTERACT
    // ------------------------------

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // CLIENT: always return SUCCESS/CONSUME so the hand animates,
        // but never actually mount/tame on client.
        if (level().isClientSide) {
            // Let food/taming items still animate nicely
            if (!isTamed() && isTamingItem(stack)) return InteractionResult.SUCCESS;
            if (isFood(stack)) return InteractionResult.SUCCESS;

            // If untamed, don't allow "mount" prediction
            if (!isTamed()) return InteractionResult.SUCCESS;

            // If tamed, allow normal predicted mounting
            return InteractionResult.SUCCESS;
        }

        // ------------------------------
        // 1) TAMING (server)
        // ------------------------------
        if (!isTamed() && isTamingItem(stack)) {
            return tryTame(player, stack);
        }

        // ------------------------------
        // 2) FEEDING (server)
        // ------------------------------
        if (isFood(stack)) {
            return fedFood(player, stack);
        }

        // ------------------------------
        // 3) UNTAMED: BLOCK MOUNTING HARD
        // ------------------------------
        if (!isTamed()) {
            // Optional: if they try to interact empty hand, still reject
            // and make the horse angry so it's clear you can't ride it yet.
            makeMad();

            // Prevent any vanilla/other interaction from mounting it.
            return InteractionResult.CONSUME;
        }

        // ------------------------------
        // 4) TAMED: MOUNT (server)
        // ------------------------------
        if (!isBaby()) {
            // Shift-right-click: reserved for inventory later
            if (player.isSecondaryUseActive()) {
                return InteractionResult.PASS;
            }

            // Already has a rider -> don't allow
            if (isVehicle()) {
                return InteractionResult.PASS;
            }

            player.startRiding(this, true);
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }


    // ------------------------------
    // TAMING
    // ------------------------------

    private boolean isTamingItem( ItemStack stack ) {
        return stack.is(Items.ROTTEN_FLESH)
                || stack.is(Items.ECHO_SHARD)
                || stack.is(Items.SCULK)
                || stack.is(Items.BONE);
    }

    private InteractionResult tryTame( Player player, ItemStack stack ) {

        if (!this.isTamed()) {
            this.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
            this.heal(4.0F);

            if (!player.getAbilities().instabuild)
                stack.shrink(1);

            if (this.random.nextInt(4) == 0) {
                this.tame(player);
                this.level().broadcastEntityEvent(this, (byte) 7); // hearts
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6); // smoke
                this.makeMad();
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return InteractionResult.PASS;
    }


    public void makeMad() {
        this.setAngry(true);
        this.playSound(SoundEvents.WARDEN_ANGRY, 1.0F, 0.6F);
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public void tame( Player player ) {
        this.entityData.set(TAMED, true);
        this.setOwnerUUID(player.getUUID());
    }


    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry( boolean angry ) {
        this.entityData.set(ANGRY, angry);
    }

    // ------------------------------
    // BREEDING
    // ------------------------------

    @Nullable
    @Override
    public AgeableMob getBreedOffspring( @NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent ) {
        return ModEntities.SCULK_HORSE.get().create(pLevel);
    }

    @Override
    public boolean isFood( ItemStack pStack ) {
        return pStack.is(ModItems.TOMATO_SCULK.get());
    }


    // ------------------------------
    // ECHO SENSE
    // ------------------------------
    private void detectEnemies() {
        if (!level().getEntitiesOfClass(
                Monster.class,
                getBoundingBox().inflate(18)
        ).isEmpty()) {
            level().playSound(null, blockPosition(),
                    SoundEvents.SCULK_SENSOR_HIT,
                    SoundSource.HOSTILE, 0.5F, 1.8F);
        }
    }

    // ------------------------------
    // SOUNDS
    // ------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_HORSE_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( DamageSource source) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_CATALYST_BLOOM;
    }



    // ------------------------------
    // PLAYER INPUT HANDLING FROM KEYBINDS
    // ------------------------------
    public void handleAbilityKeys( Player player, boolean dashKey, boolean slamKey ) {
        if (dashKey) tryDash();
        if (slamKey) tryGroundSlam();
    }

    private void startSculkCharge() {
        if (chargeCooldown > 0 || isCharging) return;

        isCharging = true;
        chargeTicks = 0;

        playSound(SoundEvents.WARDEN_ROAR, 1.2F, 1.0F);

        // STRONG initial burst
        Vec3 boost = getLookAngle().scale(2.2);
        setDeltaMovement(boost);
        hasImpulse = true;

        if (level() instanceof ServerLevel server) {
            server.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    getX(), getY() + 1, getZ(),
                    30, 0.4, 0.1, 0.4, 0.2
            );
        }
}


    // -----------------------------------------------------
//  RIDING METHODS — NeoForge 1.21.1 (Mojang mappings)
// -----------------------------------------------------

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity living ? living : null;
    }

    @Override
    protected boolean canAddPassenger( @NotNull Entity passenger ) {
        return this.getPassengers().isEmpty() && passenger instanceof LivingEntity;
    }

    // Player mounts the horse (correct Mojang mappings)
    private void setRiding( Player player ) {
        player.setYRot(this.getYRot());
        player.setXRot(this.getXRot());
        player.startRiding(this);
    }

// -----------------------------------------------------
//  SEAT OFFSET (Modern — uses new attachment system)
// -----------------------------------------------------

    private static final double SEAT_SIDE = -0.03;
    private static final double SEAT_BACK = 0.70;
    private static final double SEAT_HEIGHT = 0.40;

    @Override
    public boolean canUseSlot( @NotNull EquipmentSlot slot ) {
        return true;
    }

    @Override
    public void createInventory() {
        super.createInventory();
    }


    public boolean isArmor( ItemStack stack ) {
        return stack.getItem() instanceof AnimalArmorItem;
    }

    public boolean isBodyArmorItem( ItemStack stack ) {
        Item var3 = stack.getItem();
        if (var3 instanceof AnimalArmorItem animalarmoritem) {
            if (animalarmoritem.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Vec3 getPassengerAttachmentPoint( Entity passenger, @NotNull EntityDimensions dims, float partialTick ) {

        double localX = SEAT_SIDE;
        double localY = this.getPassengersRidingOffset() + passenger.getVehicleAttachmentPoint(this).y;
        double localZ = -SEAT_BACK;

        float yawRad = (float) Math.toRadians(this.yBodyRot);

        double x = localX * Math.cos(yawRad) - localZ * Math.sin(yawRad);
        double z = localX * Math.sin(yawRad) + localZ * Math.cos(yawRad);

        return new Vec3(x, localY, z);
    }

    public double getPassengersRidingOffset() {
        return this.getBbHeight() * SEAT_HEIGHT;
    }

// -----------------------------------------------------
//  POSITION PASSENGER CORRECTLY
// -----------------------------------------------------

    @Override
    protected void positionRider( Entity passenger, MoveFunction move ) {

        Vec3 attach = passenger.getVehicleAttachmentPoint(this);

        double x = this.getX() + attach.x;
        double y = this.getY() + this.getPassengersRidingOffset() + attach.y;
        double z = this.getZ() + attach.z;

        move.accept(passenger, x, y, z);

        if (passenger instanceof LivingEntity living) {
            living.yBodyRot = this.yBodyRot;
            living.setYRot(this.getYRot());
        }
    }

// -----------------------------------------------------
//  RIDER-CONTROLLED MOVEMENT
// -----------------------------------------------------

    @Override
    public void travel(Vec3 input) {
        LivingEntity ctrl = getControllingPassenger();

        if (ctrl instanceof Player rider) {
            setYRot(rider.getYRot());
            yRotO = getYRot();
            xRotO = getXRot();

            float strafe = rider.xxa * 0.5F;
            float forward = rider.zza;

            if (forward <= 0.0F) forward *= 0.3F;

            setSpeed((float) getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(strafe, input.y, forward));
            return;
        }

        super.travel(input);
    }



// -----------------------------------------------------
//  DISMOUNT LOGIC
// -----------------------------------------------------


    private Vec3 getSafeDismountPos( LivingEntity passenger ) {
        // Directions to test around the horse
        Vec3[] checks = new Vec3[]{
                new Vec3(1, 0, 0),   // east
                new Vec3(-1, 0, 0),  // west
                new Vec3(0, 0, 1),   // south
                new Vec3(0, 0, -1),  // north
                new Vec3(1, 0, 1),   // diagonal
                new Vec3(-1, 0, 1),
                new Vec3(1, 0, -1),
                new Vec3(-1, 0, -1)
        };

        Level level = this.level();

        for (Vec3 offset : checks) {
            double x = this.getX() + offset.x;
            double z = this.getZ() + offset.z;

            // Find ground height beneath that position
            BlockPos pos = BlockPos.containing(x, this.getY(), z);
            BlockPos down = pos.below();

            // Make sure the block below is solid ground
            if (!level.getBlockState(down).isSolid()) continue;

            // Check height for dismount
            double y = down.getY() + 1.0;

            // AABB check to prevent suffocation
            AABB hitbox = passenger.getBoundingBox().move(x - passenger.getX(), y - passenger.getY(), z - passenger.getZ());
            if (!level.noCollision(passenger, hitbox)) continue;

            return new Vec3(x, y, z);
        }

        // Fallback: dismount on top of the horse
        return new Vec3(this.getX(), this.getY() + 1.0, this.getZ());
    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger( @NotNull LivingEntity passenger ) {
        return getSafeDismountPos(passenger);
    }

    @Override
    public void addAdditionalSaveData( @NotNull CompoundTag tag ) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Tamed", this.isTamed());
        tag.putBoolean("Angry", this.isAngry());
    }

    @Override
    public void readAdditionalSaveData( @NotNull CompoundTag tag ) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(TAMED, tag.getBoolean("Tamed"));
        this.entityData.set(ANGRY, tag.getBoolean("Angry"));
    }

    private void applyChargeKnockback() {
        if (!isCharging) return;

        List<LivingEntity> hit = level().getEntitiesOfClass(
                LivingEntity.class,
                getBoundingBox().inflate(1.6),
                e -> e != this && e != getControllingPassenger()
        );

        for (LivingEntity target : hit) {

            // BIG damage (scales well vs tanky mobs)
            float damage = 15.0F + (float) getAttributeValue(Attributes.MOVEMENT_SPEED) * 10.0F;

            target.hurt(level().damageSources().mobAttack(this), damage);

            // Heavy knockback
            Vec3 kb = target.position()
                    .subtract(position())
                    .normalize()
                    .scale(1.8);

            target.push(kb.x, 0.5, kb.z);

            // Optional: brief stun
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    20, // 1 sec
                    3
            ));
        }
    }

    @Override
    public void useAbility(Player player) {

        // Must be riding THIS horse
        if (getControllingPassenger() != player) return;

        // Already charging → ignore
        if (isCharging) return;

        // --------------------
        // AIR → SLAM
        // --------------------
        if (!onGround()) {
            if (slamCooldown > 0) {
                sendCooldownMessage(player, slamCooldown);
                return;
            }

            tryGroundSlam();
            return;
        }

        // --------------------
        // GROUND → CHARGE
        // REQUIRE FORWARD INPUT
        // --------------------
        if (player.zza <= 0.05F) {
            player.displayClientMessage(
                    Component.translatable("ability.sculky.need_momentum")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return;
        }

        if (chargeCooldown > 0) {
            sendCooldownMessage(player, chargeCooldown);
            return;
        }

        startSculkCharge();
    }


    private void sendCooldownMessage(Player player, int ticksLeft) {
        int seconds = ticksLeft / 10;

        player.displayClientMessage(
                Component.translatable("ability.sculky.cooldown", seconds)
                        .withStyle(ChatFormatting.DARK_AQUA),
                true
        );
    }

}