package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.api.interfaces.InfestedSculkTool;

public class InfestedSculkShovelItem extends ShovelItem implements InfestedSculkTool {

    public InfestedSculkShovelItem(Tier tier, Properties properties) {
        super(tier, properties);
    }


    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, Player player) {
        if (!level.isClientSide() && (state.is(Blocks.DIRT) || state.is(Blocks.SAND))) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 100, 0));
        }
        return super.mineBlock(stack, level, state, pos, player);
    }

    @Override
    public InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player != null && !level.isClientSide()) {
            tryCorruptGround(level, pos.above(), player); // corrupt above block
            player.swing(context.getHand(), true);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
