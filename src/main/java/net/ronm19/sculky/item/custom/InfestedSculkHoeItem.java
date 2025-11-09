package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.ronm19.sculky.api.interfaces.InfestedSculkTool;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InfestedSculkHoeItem extends HoeItem implements InfestedSculkTool {

    public InfestedSculkHoeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public @NotNull InteractionResult useOn( UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!level.isClientSide() && level.getBlockState(pos).is(Blocks.DIRT)) {
            healUser(Objects.requireNonNull(context.getPlayer()));
            emitParticles(level, pos);
            tryCorruptGround(level, pos.above(), context.getPlayer());
        }

        return super.useOn(context);
    }
}
