package net.ronm19.sculky.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider( PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SculkyMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SCULK_SHARD.get());
        basicItem(ModItems.RAW_SCULK_SHARD.get());
        basicItem(ModItems.SCULK_HEARTFRUIT.get());
        basicItem(ModItems.TOMATO_SCULK.get());
        basicItem(ModItems.ECHO_JELLY.get());
        basicItem(ModItems.SCULK_PASTRY.get());
        basicItem(ModItems.SOULBITE_COOKIE.get());

        handheldItem(ModItems.INFESTED_SCULK_SWORD);
        handheldItem(ModItems.INFESTED_SCULK_PICKAXE);
        handheldItem(ModItems.INFESTED_SCULK_SHOVEL);
        handheldItem(ModItems.INFESTED_SCULK_AXE);
        handheldItem(ModItems.INFESTED_SCULK_HOE);
        handheldItem(ModItems.INFESTED_SCULK_HAMMER);


        buttonItem(ModBlocks.INFESTED_SCULK_BUTTON, ModBlocks.INFESTED_SCULK_BLOCK);
        fenceItem(ModBlocks.INFESTED_SCULK_FENCE, ModBlocks.INFESTED_SCULK_BLOCK);
        wallItem(ModBlocks.INFESTED_SCULK_WALL, ModBlocks.INFESTED_SCULK_BLOCK);

        basicItem(ModBlocks.INFESTED_SCULK_DOOR.asItem());

    }

    public void buttonItem( DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    private ItemModelBuilder handheldItem( DeferredItem<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,"item/" + item.getId().getPath()));
    }
}
