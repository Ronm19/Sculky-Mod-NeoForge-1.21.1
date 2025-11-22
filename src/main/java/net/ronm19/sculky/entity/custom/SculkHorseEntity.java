package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.ticks.ContainerSingleItem;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SculkHorseEntity extends AbstractHorse {
    private boolean isCharging = false;
    private int chargeCooldown = 0;
    private static final int MAX_CHARGE_TIME = 40; // 2 seconds of charging
    private static final int CHARGE_COOLDOWN_TIME = 100; // 5 seconds cooldown



    private final Container bodyArmorAccess = new ContainerSingleItem() {
        public @NotNull ItemStack getTheItem() {
            return SculkHorseEntity.this.getBodyArmorItem();

        }

        public void setTheItem( @NotNull ItemStack itemStack) {
            SculkHorseEntity.this.setBodyArmorItem(itemStack);
        }

        public void setChanged() {
        }

        public boolean stillValid(Player player) {
            return player.getVehicle() == SculkHorseEntity.this || player.canInteractWithEntity(SculkHorseEntity.this, (double)4.0F);
        }
    };


    private static final EntityDataAccessor<Boolean> TAMED =
            SynchedEntityData.defineId(SculkHorseEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ANGRY =
            SynchedEntityData.defineId(SculkHorseEntity.class, EntityDataSerializers.BOOLEAN);


    @Override
    protected void defineSynchedData( SynchedEntityData.@NotNull Builder builder ) {
        super.defineSynchedData(builder);

        builder.define(TAMED, false);
        builder.define(ANGRY, false);
    }


    // ------------------------------
    // COOLDOWNS
    // ------------------------------
    private int dashCooldown = 0;
    private int slamCooldown = 0;
    private int echoPulseTimer = 0;

    // Charge ability settings

    private static final int CHARGE_DURATION = 20; // 1 second
    private static final int CHARGE_COOLDOWN = 80; // 4 seconds


    // Ability settings
    private static final float DASH_FORCE = 2.2F;     // Very strong dash
    private static final int DASH_COOLDOWN_TICKS = 120; // 6 seconds
    private static final int SLAM_COOLDOWN_TICKS = 160; // 8 seconds

    // ------------------------------
    // CONSTRUCTOR
    // ------------------------------
    public SculkHorseEntity(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    // ------------------------------
    // ATTRIBUTES
    // ------------------------------
    public static AttributeSupplier.Builder createSculkHorseAttributes() {
        return Horse.createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)         // same as a good horse
                .add(Attributes.MOVEMENT_SPEED, 0.28D)     // normal horse speed
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.JUMP_STRENGTH, 0.8D);      // good jump
    }

    // ------------------------------
    // NATURAL SPAWNING
    // ------------------------------
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

        // Only transform if NOT already a sculk horse
        if (!(this instanceof SculkHorseEntity)) return;

        BlockPos pos = this.blockPosition();

        // Must be standing on Sculk
        if (!level.getBlockState(pos.below()).is(Blocks.SCULK)) return;

        // Transform
        SculkHorseEntity newHorse = ModEntities.SCULK_HORSE.get().create(level);

        if (newHorse != null) {
            newHorse.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

            if (this.isTamed()) {
                newHorse.tame((Player) this.getOwner());
                newHorse.setTamed(true);
            }

            // Copy health, name, attributes
            newHorse.setHealth(this.getHealth());
            if (this.hasCustomName()) newHorse.setCustomName(this.getCustomName());

            level.addFreshEntity(newHorse);

            this.discard(); // remove original horse

            // Sound + particles
            level.playSound(null, pos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0F, 0.8F);
            level.sendParticles(ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    40, 0.5, 0.5, 0.5, 0.1);
        }
    }


    // ------------------------------
    // GOALS
    // ------------------------------
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.15D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(2, new BreedGoal(this, (double)1.0F, SculkHorseEntity.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, (double)1.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if (this.canPerformRearing()) {
            this.goalSelector.addGoal(9, new RandomStandGoal(this));
        }

        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, (double)1.25F, (p_335269_) -> p_335269_.is(ItemTags.HORSE_TEMPT_ITEMS), false));
    }

    // ------------------------------
    // ABILITY: SCULK DASH
    // ------------------------------
    public void tryDash() {
        if (dashCooldown > 0 || !this.hasPassenger(Objects.requireNonNull(this.getControllingPassenger()))) return;

        dashCooldown = DASH_COOLDOWN_TICKS;

        Vec3 dir = Vec3.directionFromRotation(this.getXRot(), this.getYRot())
                .scale(DASH_FORCE);

        this.setDeltaMovement(dir);

        this.level().playSound(null, this.blockPosition(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.2F, 1.0F);
    }

    // ------------------------------
    // ABILITY: GROUND SLAM
    // ------------------------------
    public void tryGroundSlam() {
        if (slamCooldown > 0) return;
        if (this.onGround()) return;

        slamCooldown = SLAM_COOLDOWN_TICKS;

        // fast drop
        this.setDeltaMovement(this.getDeltaMovement().x, -2.5F, this.getDeltaMovement().z);
    }

    @Override
    public void tick() {
        super.tick();

        if (dashCooldown > 0) dashCooldown--;
        if (slamCooldown > 0) slamCooldown--;
        if (chargeCooldown > 0) chargeCooldown--;

        // Ground slam impact detection
        if (slamCooldown > SLAM_COOLDOWN_TICKS - 10 && this.onGround()) {
            this.doSlamImpact();
        }

        // Echo sense heartbeat
        echoPulseTimer++;
        if (echoPulseTimer % 40 == 0) {
            detectEnemies();
        }

        if (isCharging) {
            // Push forward
            Vec3 forward = this.getLookAngle().scale(0.5);
            this.setDeltaMovement(forward.x, this.getDeltaMovement().y, forward.z);

            // Blue sculk particles
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY() + 1.2, this.getZ(),
                        4, 0.2, 0.2, 0.2, 0.01);
            }

            // End charging
            if (this.tickCount % CHARGE_DURATION == 0) {
                isCharging = false;
                chargeCooldown = CHARGE_COOLDOWN;
            }
            if (isCharging) applyChargeKnockback();
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
    public @NotNull InteractionResult mobInteract( Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // ------------------------------------------
        // 1. ATTEMPT TAMING
        // ------------------------------------------
        if (!this.isTamed() && this.isTamingItem(stack)) {
            return this.tryTame(player, stack);
        }

        // ------------------------------------------
        // 2. FEEDING (heal food)
        // ------------------------------------------
        if (this.isFood(stack)) {
            return this.fedFood(player, stack);
        }

        // ------------------------------------------
        // 3. Tamed & adult → allow mounting
        // ------------------------------------------
        if (this.isTamed() && !this.isBaby()) {

            // Shift-right-click = open inventory (if you add one later)
            if (player.isSecondaryUseActive()) {
                return InteractionResult.PASS;
            }

            // Not already mounted → mount it
            if (!this.isVehicle()) {
                player.startRiding(this);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        // ------------------------------------------
        // 4. UNTAMED & wrong item → anger it
        // ------------------------------------------
        if (!this.isTamed() && !stack.isEmpty()) {
            this.makeMad();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // ------------------------------------------
        // 5. DEFAULT BEHAVIOR
        // ------------------------------------------
        return super.mobInteract(player, hand);
    }


    // ------------------------------
    // TAMING
    // ------------------------------

    private boolean isTamingItem(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH)
                || stack.is(Items.ECHO_SHARD)
                || stack.is(Items.SCULK)
                || stack.is(Items.BONE);
    }

    private InteractionResult tryTame(Player player, ItemStack stack) {

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

    public void tame(Player player) {
        this.entityData.set(TAMED, true);
        this.setOwnerUUID(player.getUUID());
    }


    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, angry);
    }

    // ------------------------------
    // BREEDING
    // ------------------------------

    @Nullable
    @Override
    public AgeableMob getBreedOffspring( @NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent) {
        return ModEntities.SCULK_HORSE.get().create(pLevel);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(ModItems.TOMATO_SCULK.get());
    }



    // ------------------------------
    // ECHO SENSE
    // ------------------------------
    private void detectEnemies() {
        boolean found = !this.level().getEntitiesOfClass(
                Monster.class,
                this.getBoundingBox().inflate(18)
        ).isEmpty();

        if (found) {
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.SCULK_SENSOR_HIT, SoundSource.HOSTILE, 0.5F, 1.8F);
        }
    }

    // ------------------------------
    // PLAYER INPUT HANDLING FROM KEYBINDS
    // ------------------------------
    public void handleAbilityKeys(Player player, boolean dashKey, boolean slamKey) {
        if (dashKey) tryDash();
        if (slamKey) tryGroundSlam();
    }
    @Override
    public void handleStartJump(int power) {
        super.handleStartJump(power);

        if (!this.isTamed() || this.level().isClientSide) return;

        // Convert vanilla 0–90 range into 0.0–1.0
        float charge = Math.min(power / 90.0F, 1.0F);

        // Activate ability when fully charged jump happens
        if (charge >= 1.0F && chargeCooldown == 0) {
            startSculkCharge();
        }
    }


    private void startSculkCharge() {
        this.isCharging = true;
        this.chargeCooldown = CHARGE_COOLDOWN_TIME;

        this.playSound(SoundEvents.WARDEN_ROAR, 1.2F, 1.0F);

        // initial speed burst
        Vec3 forward = this.getLookAngle().scale(1.8);
        this.setDeltaMovement(forward);

        // cosmetic effect
        if (level() instanceof ServerLevel server) {
            for (int i = 0; i < 30; i++) {
                server.sendParticles(ParticleTypes.SCULK_SOUL,
                        getX(), getY() + 1, getZ(),
                        1,
                        (random.nextDouble() - 0.5) * 0.4,
                        0.1,
                        (random.nextDouble() - 0.5) * 0.4,
                        0.2);
            }
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
    protected boolean canAddPassenger( @NotNull Entity passenger) {
        return this.getPassengers().isEmpty() && passenger instanceof LivingEntity;
    }

    // Player mounts the horse (correct Mojang mappings)
    private void setRiding(Player player) {
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
    public boolean canUseSlot( @NotNull EquipmentSlot slot) {
        return true;
    }

    @Override
    public void createInventory(){
        super.createInventory();
    }


    public boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof AnimalArmorItem;
    }

    public boolean isBodyArmorItem(ItemStack stack) {
        Item var3 = stack.getItem();
        if (var3 instanceof AnimalArmorItem animalarmoritem) {
            if (animalarmoritem.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Vec3 getPassengerAttachmentPoint( Entity passenger, @NotNull EntityDimensions dims, float partialTick) {

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
    protected void positionRider(Entity passenger, MoveFunction move) {

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

        LivingEntity ctrl = this.getControllingPassenger();
        if (ctrl instanceof Player rider) {

            // ---- CHARGE ACTIVATION ----
            if (rider.isSprinting() && !isCharging && chargeCooldown == 0) {
                isCharging = true;
                this.playSound(SoundEvents.WARDEN_ROAR, 1.0F, 1.2F);
            }

            if (isCharging) {

                // strong forward push
                Vec3 v = this.getLookAngle().scale(0.5);
                this.setDeltaMovement(this.getDeltaMovement().add(v));

                // knockback enemies
                level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(1.5),
                        e -> e != this && e != this.getControllingPassenger()
                ).forEach(e -> {
                    Vec3 kb = e.position().subtract(this.position()).normalize().scale(1.3);
                    e.push(kb.x, 0.4, kb.z);
                });

                // decay + stop charging
                if (this.tickCount % MAX_CHARGE_TIME == 0) {
                    isCharging = false;
                }
            }

            // Normal movement
            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();

            float strafe = rider.xxa * 0.5F;
            float forward = rider.zza;

            if (forward <= 0.0F) forward *= 0.3F;

            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(strafe, input.y, forward));
            return;
        }

        super.travel(input);
    }


// -----------------------------------------------------
//  DISMOUNT LOGIC
// -----------------------------------------------------


    private Vec3 getSafeDismountPos(LivingEntity passenger) {
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
    public @NotNull Vec3 getDismountLocationForPassenger( @NotNull LivingEntity passenger) {
        return getSafeDismountPos(passenger);
    }

    @Override
    public void addAdditionalSaveData( @NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Tamed", this.isTamed());
        tag.putBoolean("Angry", this.isAngry());
    }

    @Override
    public void readAdditionalSaveData( @NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(TAMED, tag.getBoolean("Tamed"));
        this.entityData.set(ANGRY, tag.getBoolean("Angry"));
    }

    private void applyChargeKnockback() {
        if (!isCharging) return;

        List<LivingEntity> hit = level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(1.2),
                e -> e != this && e != getControllingPassenger()
        );

        for (LivingEntity target : hit) {
            target.hurt(level().damageSources().mobAttack(this), 6.0F);
            target.knockback(1.5F, this.getX() - target.getX(), this.getZ() - target.getZ());
        }
    }
}
