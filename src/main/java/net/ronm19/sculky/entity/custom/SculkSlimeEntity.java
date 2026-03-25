package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SculkSlimeEntity extends Slime {

    public SculkSlimeEntity(EntityType<? extends Slime> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createSculkSlimeAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D);
    }

    private boolean wasOnGround = false;

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            boolean isOnGround = this.onGround();

            if (!wasOnGround && isOnGround) {
                onSculkLand();
            }

            wasOnGround = isOnGround;
        }

        if (this.level().isClientSide && this.random.nextFloat() < 0.01F) {
            double offsetX = (this.random.nextDouble() - 0.5D) * 0.18D;
            double offsetY = this.random.nextDouble() * 0.10D;
            double offsetZ = (this.random.nextDouble() - 0.5D) * 0.18D;

            this.level().addParticle(
                    ParticleTypes.SCULK_SOUL,
                    this.getX() + offsetX,
                    this.getY() + 0.08D + offsetY,
                    this.getZ() + offsetZ,
                    0.0D, 0.004D, 0.0D
            );
        }

        if (!this.level().isClientSide && this.random.nextFloat() < 0.01F) {
            this.level().playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.SCULK_CLICKING,
                    SoundSource.HOSTILE,
                    0.35F,
                    0.95F + this.random.nextFloat() * 0.10F
            );
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide && this.onGround() && this.random.nextFloat() < 0.08F) {
            this.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY(0.1D),
                    this.getZ(),
                    0.0D, 0.01D, 0.0D
            );
        }
    }

    private void onSculkLand() {
        int size = this.getSize();

        // 🔊 Sound (scaled by size)
        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_CATALYST_BLOOM,
                net.minecraft.sounds.SoundSource.HOSTILE,
                0.6F + (size * 0.1F),
                0.7F
        );

        // ✨ Particle burst
        if (this.level() instanceof net.minecraft.server.level.ServerLevel server) {
            server.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    6 + size * 2,
                    0.4D, 0.1D, 0.4D,
                    0.02D
            );
        }

        // 🌑 Tiny darkness pulse (ONLY big slimes)
        if (size >= 3) {
            for (var player : this.level().getEntitiesOfClass(
                    net.minecraft.world.entity.player.Player.class,
                    this.getBoundingBox().inflate(3.0D)
            )) {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DARKNESS,
                        20, // 1 second
                        0,
                        false,
                        false
                ));
            }
        }
    }

    @Override
    protected @NotNull ParticleOptions getParticleType() {
        return ParticleTypes.SCULK_SOUL;
    }

    @Override
    protected void dealDamage(LivingEntity target) {
        super.dealDamage(target);

        if (this.isAlive()) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0));

            if (this.random.nextFloat() < 0.20F) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
            }
        }
    }

    @Override
    public boolean isDealsDamage() {
        return this.getSize() > 1;
    }

    @Override
    protected int getJumpDelay() {
        return super.getJumpDelay() + 6; // slower than normal slime
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getJumpSound() {
        return SoundEvents.SCULK_SENSOR_HIT;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getSquishSound() {
        return SoundEvents.SCULK_BLOCK_PLACE;
    }
}