package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.effect.ModEffects;
import org.jetbrains.annotations.Nullable;

public class ModFlammableRotatedPillarBlock extends RotatedPillarBlock {
    public ModFlammableRotatedPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFlammable( BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public @Nullable BlockState getToolModifiedState( BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if(context.getItemInHand().getItem() instanceof AxeItem) {
            if(state.is(ModBlocks.INFESTED_SCULK_LOG.get())) {
                return ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }

            if(state.is(ModBlocks.INFESTED_SCULK_WOOD.get())) {
                return ModBlocks.STRIPPED_INFESTED_SCULK_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }

        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    public void stepOn( Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide) return;

        // Only react to living entities
        if (!(entity instanceof LivingEntity)) return;

        // Very small chance to react
        if (level.random.nextFloat() < 0.15f) {
            level.addParticle(
                    ParticleTypes.SCULK_SOUL,
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    0.0, 0.02, 0.0
            );
        }

        if (entity instanceof LivingEntity living) {
            if (living.hasEffect(ModEffects.SCULK_INFECTION_EFFECT.getDelegate())) {
                // stronger particle or sound
            }
        }

        level.playLocalSound(
                pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.SCULK_BLOCK_STEP,
                SoundSource.BLOCKS,
                0.3f,
                0.8f + level.random.nextFloat() * 0.2f,
                false
        );

        super.stepOn(level, pos, state, entity);
    }
}