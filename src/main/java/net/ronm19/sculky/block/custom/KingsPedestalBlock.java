package net.ronm19.sculky.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.util.ModAdvancementHelper;
import net.ronm19.sculky.util.SculkKingRitualHelper;

public class KingsPedestalBlock extends Block {

    public static final MapCodec<KingsPedestalBlock> CODEC = simpleCodec(KingsPedestalBlock::new);
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public KingsPedestalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    public MapCodec<KingsPedestalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemInteractionResult.SUCCESS;
        }

        // Step 1: Sculk Core awakens the pedestal.
        if (stack.is(ModItems.SCULK_CORE.get())) {
            if (state.getValue(ACTIVATED)) {
                pulsePedestal(level, pos);
                player.displayClientMessage(Component.literal("The King's Pedestal is already awake."), true);
                return ItemInteractionResult.SUCCESS;
            }

            activatePedestal(level, pos, state);
            player.displayClientMessage(Component.literal("The King's Pedestal awakens."), true);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return ItemInteractionResult.SUCCESS;
        }

        // Step 2: Royal Sculk Fragment summons the King.
        if (stack.is(ModItems.ROYAL_SCULK_FRAGMENT.get())) {
            if (!state.getValue(ACTIVATED)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    ModAdvancementHelper.grant(serverPlayer, "before_the_king", "throne_not_ready");
                }

                player.displayClientMessage(Component.literal("The pedestal remains dormant."), true);
                pulsePedestal(level, pos);
                return ItemInteractionResult.SUCCESS;
            }

            if (!SculkKingRitualHelper.isInsideBuriedThrone(serverLevel, pos)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    ModAdvancementHelper.grant(serverPlayer, "before_the_king", "throne_not_ready");
                }

                player.displayClientMessage(Component.literal("The throne does not hear you here."), true);
                pulsePedestal(level, pos);
                return ItemInteractionResult.SUCCESS;
            }

            if (SculkKingRitualHelper.hasNearbySculkKing(serverLevel, pos)) {
                player.displayClientMessage(Component.literal("The King already walks these ruins."), true);
                pulsePedestal(level, pos);
                return ItemInteractionResult.SUCCESS;
            }

            SculkKingRitualHelper.playThroneAnswerEffects(serverLevel, pos);

            boolean summoned = SculkKingRitualHelper.summonSculkKing(serverLevel, pos);

            if (!summoned) {
                player.displayClientMessage(Component.literal("The throne refuses to answer."), true);
                pulsePedestal(level, pos);
                return ItemInteractionResult.SUCCESS;
            }

            if (player instanceof ServerPlayer serverPlayer) {
                ModAdvancementHelper.grant(serverPlayer, "summon_sculk_king", "summon_sculk_king");
            }

            player.displayClientMessage(Component.literal("The Sculk King rises."), true);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            // Reset pedestal after successful summon.
            level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!state.getValue(ACTIVATED)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                deactivatePedestal(level, pos, state);
            } else {
                pulsePedestal(level, pos);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVATED)) {
            return;
        }

        if (random.nextInt(4) == 0) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;
            double y = pos.getY() + 1.02D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;

            level.addParticle(ParticleTypes.SCULK_CHARGE_POP, x, y, z, 0.0D, 0.02D, 0.0D);
        }

        if (random.nextInt(7) == 0) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.45D;
            double y = pos.getY() + 1.04D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.45D;

            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.01D, 0.0D);
        }
    }

    private void activatePedestal(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(ACTIVATED, true), Block.UPDATE_ALL);

        level.playSound(null, pos,
                SoundEvents.RESPAWN_ANCHOR_CHARGE,
                SoundSource.BLOCKS,
                0.9F,
                0.8F);

        level.playSound(null, pos,
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.BLOCKS,
                0.45F,
                1.35F);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                    pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D,
                    16, 0.2D, 0.08D, 0.2D, 0.01D);

            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                    8, 0.18D, 0.05D, 0.18D, 0.005D);
        }
    }

    private void deactivatePedestal(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);

        level.playSound(null, pos,
                SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(),
                SoundSource.BLOCKS,
                0.85F,
                0.9F);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                    10, 0.18D, 0.06D, 0.18D, 0.01D);
        }
    }

    private void pulsePedestal(Level level, BlockPos pos) {
        level.playSound(null, pos,
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.BLOCKS,
                0.25F,
                1.6F);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                    pos.getX() + 0.5D, pos.getY() + 1.02D, pos.getZ() + 0.5D,
                    10, 0.15D, 0.04D, 0.15D, 0.01D);
        }
    }
}