package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.core.particles.ParticleTypes;

public class InfestedCakeBlock extends CakeBlock {

    public InfestedCakeBlock(BlockBehaviour.Properties props) {
        super(props);
        // DO NOT call registerDefaultState manually here;
        // the parent constructor already does it safely.
    }

    // no BITES property re-declaration needed — CakeBlock already defines it

    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand,
                                 net.minecraft.world.phys.BlockHitResult hit) {
        if (!player.canEat(false)) return InteractionResult.PASS;

        if (!level.isClientSide) {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(2, 0.3f);
            level.playSound(null, pos, SoundEvents.SCULK_BLOCK_BREAK,
                    SoundSource.PLAYERS, 0.6f, 1.0f);
            level.gameEvent(player, GameEvent.EAT, pos);
            int bites = state.getValue(BITES);
            if (bites < 6)
                level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
            else
                level.removeBlock(pos, false);
        }
        return InteractionResult.SUCCESS;
    }
}
