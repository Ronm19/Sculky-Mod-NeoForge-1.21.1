package net.ronm19.sculky.block.custom;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class TomatoSculkCropBlock extends CropBlock {
        public static final int MAX_AGE = 5;
        public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 5);

        public TomatoSculkCropBlock(Properties properties) {
            super(properties);
        }

        @Override
        protected @NotNull ItemLike getBaseSeedId() {
            return ModItems.TOMATO_SCULK_SEEDS;
        }

        @Override
        public @NotNull IntegerProperty getAgeProperty() {
            return AGE;
        }

        @Override
        public int getMaxAge() {
            return MAX_AGE;
        }

        @Override
        protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> pBuilder) {
            pBuilder.add(AGE);
        }
    }
