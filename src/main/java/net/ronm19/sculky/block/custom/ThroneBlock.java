package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ThroneBlock extends Block {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    private static final int COOLDOWN_TICKS = 20 * 30; // 30 seconds

    public ThroneBlock( Properties properties ) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        builder.add(ACTIVATED);
    }

    // 🔥 Ambient ticking (particles + subtle life)
    @Override
    public void animateTick( BlockState state, Level level, BlockPos pos, RandomSource random ) {
        if (random.nextFloat() < 0.3F) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5);
            double y = pos.getY() + 1.1;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5);

            level.addParticle(ParticleTypes.SCULK_SOUL, x, y, z, 0, 0.02, 0);
        }
    }

    // 🔊 Random ambient pulse (server-side)
    @Override
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        if (random.nextFloat() < 0.15F) {
            level.playSound(
                    null,
                    pos,
                    SoundEvents.SCULK_SENSOR_STEP,
                    SoundSource.BLOCKS,
                    0.6F,
                    0.6F
            );
        }

        // schedule next tick
        level.scheduleTick(pos, this, 40 + random.nextInt(40));
    }

    // 🧠 Start ticking when placed/loaded
    @Override
    public void onPlace( BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston ) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 40);
        }
    }

    // 👑 Interaction (King’s Relic trigger)
    @Override
    protected InteractionResult useWithoutItem( BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit ) {

        ItemStack stack = player.getMainHandItem(); // ✅ FIXED

        if (stack.getItem() == net.ronm19.sculky.item.ModItems.KING_RELIC.get()) {

            // ❌ Already activated
            if (state.getValue(ACTIVATED)) {
                if (!level.isClientSide) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("The throne is dormant..."),
                            true
                    );
                }
                return InteractionResult.SUCCESS;
            }

            // ❌ Cooldown
            if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                if (!level.isClientSide) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("The relic is still resonating..."),
                            true
                    );
                }
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide) {

                level.setBlock(pos, state.setValue(ACTIVATED, true), 3);

                level.playSound(null, pos, SoundEvents.WARDEN_NEARBY_CLOSE, SoundSource.BLOCKS, 1.0F, 0.7F);
                level.playSound(null, pos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 1.0F, 0.7F);

                // 🌍 Screen shake
                for (Player nearby : level.getEntitiesOfClass(Player.class,
                        player.getBoundingBox().inflate(8))) {

                    double dx = (level.random.nextDouble() - 0.5) * 0.3;
                    double dz = (level.random.nextDouble() - 0.5) * 0.3;

                    nearby.push(dx, 0.1, dz);
                }

                // 🌑 Sculk spread
                int radius = 4;

                for (BlockPos targetPos : BlockPos.betweenClosed(
                        pos.offset(-radius, -1, -radius),
                        pos.offset(radius, 1, radius))) {

                    if (level.random.nextFloat() < 0.3F) {

                        BlockState targetState = level.getBlockState(targetPos);

                        if (targetState.is(Blocks.STONE)
                                || targetState.is(Blocks.DIRT)
                                || targetState.is(Blocks.GRASS_BLOCK)) {

                            level.setBlock(targetPos, ModBlocks.SCULK_SANCTUM_GRASS_BLOCK.get().defaultBlockState(), 3);
                        }
                    }
                }

                // ✨ Particles
                ((ServerLevel) level).sendParticles(
                        ParticleTypes.SCULK_CHARGE_POP,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        40,
                        1.5, 1.0, 1.5,
                        0.0
                );

                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "The throne trembles... something ancient awakens."
                        ),
                        true
                );

                player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}