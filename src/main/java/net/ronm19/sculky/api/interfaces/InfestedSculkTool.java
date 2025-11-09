package net.ronm19.sculky.api.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface InfestedSculkTool {

    // === 🔮 Base Sculk Utility Abilities ===

    /**
     * Darkness aura: applied to entities hit.
     */
    default void applyDarkness( LivingEntity target ) {
        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
    }

    /**
     * Slowness debuff on hit.
     */
    default void applySlowness( LivingEntity target ) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
    }

    /**
     * Emits pulse effect to nearby entities.
     */
    default void emitPulse( Level level, LivingEntity source, int radius ) {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(radius));
        for (LivingEntity e : entities) {
            if (e != source) e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
    }

    /**
     * Regeneration blessing when interacting with nature.
     */
    default void healUser( Player player ) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
    }

    /**
     * Night vision while mining dark stones.
     */
    default void grantNightVision( Player player ) {
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 120, 0));
    }

    /**
     * Haste when digging loose blocks.
     */
    default void grantHaste( Player player ) {
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 100, 0));
    }

    /**
     * Plays sculk particle pulse.
     */
    default void emitParticles( Level level, BlockPos pos ) {
        level.levelEvent(2005, pos, 0);
    }

    /**
     * Detects “Sculk Resonance”: amplifies near Sculk blocks.
     */
    default void sculkResonance( Level level, Player player ) {
        BlockPos pos = player.blockPosition();
        boolean nearSculk = BlockPos.betweenClosedStream(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))
                .map(level :: getBlockState)
                .anyMatch(state -> state.is(Blocks.SCULK) || state.is(Blocks.SCULK_SENSOR));
        if (nearSculk) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 0));
        }
    }

    default void tryCorruptGround(Level level, net.minecraft.core.BlockPos pos, Player player) {
        if (level.isClientSide()) return;

        // Small chance for infested sculk
        if (level.random.nextFloat() < 0.10f) { // 10% chance
            level.setBlock(pos, net.ronm19.sculky.block.ModBlocks.INFESTED_SCULK_BLOCK.get().defaultBlockState(), 3);
        } else {
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.SCULK.defaultBlockState(), 3);
        }

        // Particles + small regeneration effect for the player
        level.levelEvent(2005, pos, 0);
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.REGENERATION, 60, 0
        ));
    }

    default void onMineBlock( Level level, BlockState state, net.minecraft.core.BlockPos pos, Player player ) {
        if (!level.isClientSide() && (state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE))) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 120, 0));
        }
    }

    boolean mineBlock( ItemStack stack, Level level, BlockState state, BlockPos pos, Player player );
}
