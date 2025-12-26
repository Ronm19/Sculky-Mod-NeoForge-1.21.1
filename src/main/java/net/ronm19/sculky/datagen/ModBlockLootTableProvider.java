package net.ronm19.sculky.datagen;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.block.custom.TomatoSculkCropBlock;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider( HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.INFESTED_SCULK_BLOCK.get());
        dropSelf(ModBlocks.RAW_INFESTED_SCULK_BLOCK.get());

        dropSelf(ModBlocks.INFESTED_SCULK_BRICKS.get());

        this.add(ModBlocks.SCULK_ORE.get(),
                block -> createOreDrop(ModBlocks.SCULK_ORE.get(), ModItems.RAW_SCULK_SHARD.get()));
        this.add(ModBlocks.DEEPSLATE_SCULK_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.DEEPSLATE_SCULK_ORE.get(), ModItems.RAW_SCULK_SHARD.get(), 2, 5));

        dropSelf(ModBlocks.INFESTED_SCULK_STAIRS.get());
        this.add(ModBlocks.INFESTED_SCULK_SLAB.get(), block -> createSlabItemTable(ModBlocks.INFESTED_SCULK_SLAB.get()));

        dropSelf(ModBlocks.INFESTED_SCULK_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.INFESTED_SCULK_BUTTON.get());

        dropSelf(ModBlocks.INFESTED_SCULK_FENCE.get());
        dropSelf(ModBlocks.INFESTED_SCULK_FENCE_GATE.get());
        dropSelf(ModBlocks.INFESTED_SCULK_WALL.get());

        dropSelf(ModBlocks.INFESTED_SCULK_TRAPDOOR.get());
        this.add(ModBlocks.INFESTED_SCULK_DOOR.get(), block -> createDoorTable(ModBlocks.INFESTED_SCULK_DOOR.get()));


        dropSelf(ModBlocks.INFESTED_SCULK_BRICK_STAIRS.get());
        this.add(ModBlocks.INFESTED_SCULK_BRICK_SLAB.get(), block -> createSlabItemTable(ModBlocks.INFESTED_SCULK_BRICK_SLAB.get()));

        dropSelf(ModBlocks.INFESTED_SCULK_BRICK_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.INFESTED_SCULK_BRICK_BUTTON.get());

        dropSelf(ModBlocks.INFESTED_SCULK_BRICK_FENCE.get());
        dropSelf(ModBlocks.INFESTED_SCULK_BRICK_FENCE_GATE.get());
        dropSelf(ModBlocks.INFESTED_SCULK_BRICK_WALL.get());

        LootItemCondition.Builder lootItemConditionBuilder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.TOMATO_SCULK_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TomatoSculkCropBlock.AGE, 5));
        this.add(ModBlocks.TOMATO_SCULK_CROP.get(), this.createCropDrops(ModBlocks.TOMATO_SCULK_CROP.get(),
                ModItems.TOMATO_SCULK.get(), ModItems.TOMATO_SCULK_SEEDS.asItem(), lootItemConditionBuilder));

        this.dropSelf(ModBlocks.SCULKBLOOM.get());
        this.add(ModBlocks.POTTED_SCULKBLOOM.get(), createPotFlowerItemTable(ModBlocks.SCULKBLOOM));

        this.dropSelf(ModBlocks.INFESTED_SCULK_LOG.get());
        this.dropSelf(ModBlocks.INFESTED_SCULK_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_INFESTED_SCULK_WOOD.get());

        this.dropSelf(ModBlocks.INFESTED_SCULK_PLANKS.get());
        this.dropSelf(ModBlocks.INFESTED_SCULK_SAPLING.get());

        this.add(ModBlocks.INFESTED_SCULK_LEAVES.get(), block ->
                createLeavesDrops(block, ModBlocks.INFESTED_SCULK_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

        dropSelf(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get());
        dropSelf(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get());
        dropSelf(ModBlocks.INFESTED_SCULK_ROOTED_DIRT_BLOCK.get());
        dropSelf(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK.get());

    }

    protected LootTable.Builder createMultipleOreDrops( Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder ::value)::iterator;
    }
}
