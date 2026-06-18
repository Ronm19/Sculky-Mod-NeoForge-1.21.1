package net.ronm19.sculky.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.ronm19.sculky.entity.ModEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class SculkFangsEntity extends Entity implements TraceableEntity {
    public static final int ATTACK_DURATION = 20;
    public static final int LIFE_OFFSET = 2;
    public static final int ATTACK_TRIGGER_TICKS = 14;

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 22;
    private boolean clientSideAttackStarted;

    @Nullable
    private LivingEntity owner;

    @Nullable
    private UUID ownerUUID;

    public SculkFangsEntity(EntityType<? extends SculkFangsEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SculkFangsEntity(Level level, double x, double y, double z, float yRot, int warmupDelay, LivingEntity owner) {
        this(ModEntities.SCULK_FANGS.get(), level);
        this.warmupDelayTicks = warmupDelay;
        this.setOwner(owner);
        this.setYRot(yRot * (180.0F / (float) Math.PI));
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUUID();
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.ownerUUID);

            if (entity instanceof LivingEntity livingEntity) {
                this.owner = livingEntity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.warmupDelayTicks = tag.getInt("Warmup");

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Warmup", this.warmupDelayTicks);

        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                this.lifeTicks--;

                if (this.lifeTicks == 14) {
                    for (int i = 0; i < 12; i++) {
                        double x = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * this.getBbWidth() * 0.5D;
                        double y = this.getY() + 0.05D + this.random.nextDouble();
                        double z = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * this.getBbWidth() * 0.5D;

                        double xSpeed = (this.random.nextDouble() * 2.0D - 1.0D) * 0.25D;
                        double ySpeed = 0.2D + this.random.nextDouble() * 0.25D;
                        double zSpeed = (this.random.nextDouble() * 2.0D - 1.0D) * 0.25D;

                        this.level().addParticle(
                                ParticleTypes.SCULK_SOUL,
                                x,
                                y + 1.0D,
                                z,
                                xSpeed,
                                ySpeed,
                                zSpeed
                        );
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                for (LivingEntity livingEntity : this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D)
                )) {
                    this.dealDamageTo(livingEntity);
                }
            }

            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    private void dealDamageTo(LivingEntity target) {
        LivingEntity owner = this.getOwner();

        if (target.isAlive() && !target.isInvulnerable() && target != owner) {
            if (owner == null) {
                target.hurt(this.damageSources().magic(), 7.0F);
            } else {
                if (owner.isAlliedTo(target)) {
                    return;
                }

                DamageSource damageSource = this.damageSources().indirectMagic(this, owner);

                if (target.hurt(damageSource, 7.0F) && this.level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);

                    if (this.random.nextFloat() < 0.35F) {
                        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0), owner);
                    }

                    serverLevel.sendParticles(
                            ParticleTypes.SCULK_SOUL,
                            target.getX(),
                            target.getY() + 0.5D,
                            target.getZ(),
                            8,
                            0.2D,
                            0.3D,
                            0.2D,
                            0.02D
                    );
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);

        if (id == 4) {
            this.clientSideAttackStarted = true;

            if (!this.isSilent()) {
                this.level().playLocalSound(
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SoundEvents.EVOKER_FANGS_ATTACK,
                        this.getSoundSource(),
                        1.0F,
                        this.random.nextFloat() * 0.2F + 0.75F,
                        false
                );
            }
        }
    }

    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        }

        int ticks = this.lifeTicks - 2;
        return ticks <= 0 ? 1.0F : 1.0F - ((float) ticks - partialTicks) / 20.0F;
    }
}