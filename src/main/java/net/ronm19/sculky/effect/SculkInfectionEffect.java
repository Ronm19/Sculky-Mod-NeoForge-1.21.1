package net.ronm19.sculky.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SculkInfectionEffect extends MobEffect {


    protected SculkInfectionEffect( MobEffectCategory category, int color ) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick( LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (level.isClientSide) return false;

        // Damage every second based on amplifier
        int interval = Math.max(10, 20 - (amplifier * 5)); // faster at higher amp
        if (entity.getEffect(ModEffects.SCULK_INFECTION_EFFECT).getDuration() % interval == 0) {
            entity.hurt(level.damageSources().magic(), 1.0F + (amplifier * 0.5F));

            // Spread infection
            if (level.random.nextFloat() < 0.25F) {
                List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class,
                        new AABB(entity.blockPosition()).inflate(2.5D),
                        e -> e != entity && !e.hasEffect(ModEffects.SCULK_INFECTION_EFFECT));

                if (!nearby.isEmpty()) {
                    LivingEntity target = nearby.get(level.random.nextInt(nearby.size()));
                    target.addEffect(new MobEffectInstance(ModEffects.SCULK_INFECTION_EFFECT, 160, 0)); // 8s infection
                }
            }

            // Sound feedback
            if (level.random.nextFloat() < 0.3F) {
                level.playSound(null, entity.blockPosition(),
                        SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.HOSTILE,
                        0.8F, 0.8F + level.random.nextFloat() * 0.4F);
            }
        }

        // Sculk particle visuals
        for (int i = 0; i < 4; i++) {
            double dx = entity.getX() + (level.random.nextDouble() - 0.5D) * 0.6D;
            double dy = entity.getY() + level.random.nextDouble() * entity.getBbHeight();
            double dz = entity.getZ() + (level.random.nextDouble() - 0.5D) * 0.6D;
            level.addParticle(ParticleTypes.SCULK_SOUL, dx, dy, dz, 0, 0.02, 0);
        }
        return false;
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // run every tick
    }
}