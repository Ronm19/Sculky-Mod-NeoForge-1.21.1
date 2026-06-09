package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

public class CrownedSculkmiteEntity extends SculkmiteEntity {

    private static final int ROYAL_PULSE_INTERVAL = 100; // 5 seconds
    private static final double ROYAL_PULSE_RANGE = 7.0D;

    public CrownedSculkmiteEntity(EntityType<? extends SculkmiteEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createCrownedSculkmiteAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.tickCount % ROYAL_PULSE_INTERVAL == 0 && this.getTarget() != null) {
                this.royalCommandPulse(serverLevel);
            }
        }
    }

    private void royalCommandPulse(ServerLevel serverLevel) {
        List<SculkmiteEntity> nearbySculkmites = this.level().getEntitiesOfClass(
                SculkmiteEntity.class,
                this.getBoundingBox().inflate(ROYAL_PULSE_RANGE),
                sculkmite -> sculkmite.isAlive()
                        && sculkmite != this
                        && !(sculkmite instanceof CrownedSculkmiteEntity)
        );

        if (nearbySculkmites.isEmpty()) {
            return;
        }

        for (SculkmiteEntity sculkmite : nearbySculkmites) {
            sculkmite.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    100,
                    0
            ));
        }

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 0.45D,
                this.getZ(),
                8,
                0.35D,
                0.25D,
                0.35D,
                0.015D
        );
    }

    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit && target instanceof net.minecraft.world.entity.LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(
                    MobEffects.DARKNESS,
                    60,
                    0
            ));
        }

        return hit;
    }

    public static boolean canSpawn(
            EntityType<CrownedSculkmiteEntity> entityType,
            ServerLevelAccessor level,
            net.minecraft.world.entity.MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random)
                && random.nextInt(5) == 0;
    }
}