package net.ronm19.sculky.datagen;

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
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.ronm19.sculky.block.ModBlocks;
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
