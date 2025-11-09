package net.ronm19.sculky.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider( PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes( @NotNull RecipeOutput pRecipeOutput) {
        List<ItemLike> SCULK_SMELTABLES = List.of(ModItems.RAW_SCULK_SHARD,
                ModBlocks.SCULK_ORE, ModBlocks.DEEPSLATE_SCULK_ORE);

        // ------------------------------ SHAPED RECIPES ------------------------------- //


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.INFESTED_SCULK_BLOCK.get())
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModItems.SCULK_SHARD.get())
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.SCULK_PASTRY.get())
                .pattern("SSS")
                .pattern("SCS")
                .pattern("SSS")
                .define('C', Items.CAKE)
                .define('S', ModItems.SCULK_SHARD.get())
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.INFESTED_SCULK_SWORD.get())
                .pattern(" S ")
                .pattern(" S ")
                .pattern(" T ")
                .define('S', ModItems.SCULK_SHARD.get())
                .define('T', Items.STICK)
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.INFESTED_SCULK_PICKAXE.get())
                .pattern("SSS")
                .pattern(" T ")
                .pattern(" T ")
                .define('S', ModItems.SCULK_SHARD.get())
                .define('T', Items.STICK)
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.INFESTED_SCULK_AXE.get())
                .pattern("SS ")
                .pattern("ST ")
                .pattern(" T ")
                .define('S', ModItems.SCULK_SHARD.get())
                .define('T', Items.STICK)
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.INFESTED_SCULK_SHOVEL.get())
                .pattern(" S ")
                .pattern(" T ")
                .pattern(" T ")
                .define('S', ModItems.SCULK_SHARD.get())
                .define('T', Items.STICK)
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.INFESTED_SCULK_HOE.get())
                .pattern(" SS")
                .pattern(" T ")
                .pattern(" T ")
                .define('S', ModItems.SCULK_SHARD.get())
                .define('T', Items.STICK)
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);





        // ------------------------------ SHAPELESS RECIPES ------------------------------- //

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SCULK_SHARD.get(), 9)
                .requires(ModBlocks.INFESTED_SCULK_BLOCK.get())
                .unlockedBy("has_infested_sculk_block", has(ModBlocks.INFESTED_SCULK_BLOCK.get())).save(pRecipeOutput);

        stairBuilder(ModBlocks.INFESTED_SCULK_STAIRS.get(), Ingredient.of(ModItems.SCULK_SHARD.get())).group("sculk_shard")
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);
        slab(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFESTED_SCULK_SLAB.get(), ModItems.SCULK_SHARD.get());

        pressurePlate(pRecipeOutput, ModBlocks.INFESTED_SCULK_PRESSURE_PLATE.get(), ModItems.SCULK_SHARD.get());
        buttonBuilder(ModBlocks.INFESTED_SCULK_BUTTON.get(), Ingredient.of(ModItems.SCULK_SHARD.get())).group("sculk_shard")
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);

        fenceBuilder(ModBlocks.INFESTED_SCULK_FENCE.get(), Ingredient.of(ModItems.SCULK_SHARD.get())).group("sculk_shard")
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);
        fenceGateBuilder(ModBlocks.INFESTED_SCULK_FENCE_GATE.get(), Ingredient.of(ModItems.SCULK_SHARD.get())).group("sculk_shard")
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);
        wall(pRecipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.INFESTED_SCULK_WALL.get(), ModItems.SCULK_SHARD.get());

        doorBuilder(ModBlocks.INFESTED_SCULK_DOOR.get(), Ingredient.of(ModItems.SCULK_SHARD.get())).group("sculk_shard")
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);
        trapdoorBuilder(ModBlocks.INFESTED_SCULK_TRAPDOOR.get(), Ingredient.of(ModItems.SCULK_SHARD.get())).group("sculk_shard")
                .unlockedBy("has_sculk_shard", has(ModItems.SCULK_SHARD.get())).save(pRecipeOutput);


        // ---------------------- COOKING RECIPES ------------------------------------------- //


        oreSmelting(pRecipeOutput, SCULK_SMELTABLES, RecipeCategory.MISC, ModItems.SCULK_SHARD.get(), 0.25f, 200, "sculk_shard");
        oreBlasting(pRecipeOutput, SCULK_SMELTABLES, RecipeCategory.MISC, ModItems.SCULK_SHARD.get(), 0.25f, 100, "sculk_shard");


    }

    // ---------------------- METHODS ------------------------------------------- //

    protected static void oreSmelting( @NotNull RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult,
                                       float pExperience, int pCookingTIme, @NotNull String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe ::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting( @NotNull RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult,
                                       float pExperience, int pCookingTime, @NotNull String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe ::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking( @NotNull RecipeOutput pRecipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.@NotNull Factory<T> factory,
                                                                        List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult, float pExperience, int pCookingTime, @NotNull String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, SculkyMod.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
