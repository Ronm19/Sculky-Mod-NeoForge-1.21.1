package net.ronm19.sculky.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.item.ModItems;

import java.util.LinkedHashMap;

public class ModItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }



    public ModItemModelProvider( PackOutput output, ExistingFileHelper existingFileHelper ) {
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
        basicItem(ModItems.SCULK_APPLE.get());
        basicItem(ModItems.SCULK_BONE.get());
        basicItem(ModItems.SCULK_RESONANCE.get());
        basicItem(ModItems.SCULK_CORE.get());
        basicItem(ModItems.ECHO_DUST.get());
        basicItem(ModItems.SCULK_FANG.get());
        basicItem(ModItems.SCULK_CHITIN.get());
        basicItem(ModItems.SCULK_LANTERN.get());
        basicItem(ModItems.SCULK_JAR.get());
        basicItem(ModItems.SCULK_BOMB.get());

        basicItem(ModItems.CROWN_FRAGMENT.get());
        basicItem(ModItems.ROYAL_SCULK_FRAGMENT.get());
        basicItem(ModItems.ANCIENT_RESONANCE_CORE.get());
        basicItem(ModItems.KING_RELIC.get());
        basicItem(ModItems.THRONE_SHARD.get());


        handheldItem(ModItems.INFESTED_SCULK_SWORD);
        handheldItem(ModItems.INFESTED_SCULK_PICKAXE);
        handheldItem(ModItems.INFESTED_SCULK_SHOVEL);
        handheldItem(ModItems.INFESTED_SCULK_AXE);
        handheldItem(ModItems.INFESTED_SCULK_HOE);
        handheldItem(ModItems.INFESTED_SCULK_HAMMER);
        handheldItem(ModItems.ECHO_CONDUIT);
        handheldItem(ModItems.ECHO_DAGGER);
        handheldItem(ModItems.SCULK_RAT_STAFF);

        handheldItem(ModItems.TOTEM_ECHO_RECALL);
        handheldItem(ModItems.TOTEM_SWARM);
        handheldItem(ModItems.TOTEM_SCULK_VEIL);


        basicItem(ModItems.TOMATO_SCULK_SEEDS.get());

        flowerItem(ModBlocks.SCULKBLOOM);
        flowerItem(ModBlocks.ECHOBLOOM);


        buttonItem(ModBlocks.INFESTED_SCULK_BUTTON, ModBlocks.INFESTED_SCULK_BLOCK);
        fenceItem(ModBlocks.INFESTED_SCULK_FENCE, ModBlocks.INFESTED_SCULK_BLOCK);
        wallItem(ModBlocks.INFESTED_SCULK_WALL, ModBlocks.INFESTED_SCULK_BLOCK);
        basicItem(ModBlocks.INFESTED_SCULK_DOOR.asItem());
        saplingItem(ModBlocks.INFESTED_SCULK_SAPLING);

        buttonItem(ModBlocks.INFESTED_SCULK_BRICK_BUTTON, ModBlocks.INFESTED_SCULK_BRICKS);
        fenceItem(ModBlocks.INFESTED_SCULK_BRICK_FENCE, ModBlocks.INFESTED_SCULK_BRICKS);
        wallItem(ModBlocks.INFESTED_SCULK_BRICK_WALL, ModBlocks.INFESTED_SCULK_BRICKS);

        buttonItem(ModBlocks.SCULK_JUNGLE_BUTTON, ModBlocks.SCULK_JUNGLE_PLANKS);
        fenceItem(ModBlocks.SCULK_JUNGLE_FENCE, ModBlocks.SCULK_JUNGLE_PLANKS);
        wallItem(ModBlocks.SCULK_JUNGLE_WALL, ModBlocks.SCULK_JUNGLE_PLANKS);
        basicItem(ModBlocks.SCULK_JUNGLE_DOOR.asItem());
        saplingItem(ModBlocks.SCULK_JUNGLE_SAPLING);

        trimmedArmorItem(ModItems.INFESTED_SCULK_HELMET);
        trimmedArmorItem(ModItems.INFESTED_SCULK_CHESTPLATE);
        trimmedArmorItem(ModItems.INFESTED_SCULK_LEGGINGS);
        trimmedArmorItem(ModItems.INFESTED_SCULK_BOOTS);

        trimmedArmorItem(ModItems.SCULK_BOOTS);

        basicItem(ModItems.INFESTED_SCULK_HORSE_ARMOR.get());



        withExistingParent(ModItems.SCULK_PARASITE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_SENTINEL_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_STALKER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_SHADE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_HORROR_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_ZOMBIE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_HUSK_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_SKELETON_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_CREEPER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_SPIDER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_ENDERMAN_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULKMITE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_SANDSNARE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SALVATORE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_PHANTOM_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

        withExistingParent(ModItems.SCULK_WOLF_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_WOLF_ALPHA_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_HORSE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_FOX_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_TAIL_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_BAT_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_BEETLE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_RAT_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HOLLOW_HORN_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.INFESTED_EYE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCULK_HUNTER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }

    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "block/" + item.getId().getPath()));

    }

    private ItemModelBuilder horizontalBlockItem(DeferredBlock<Block> block) {
        return getBuilder(block.getId().getPath()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                "block/" + block.getId().getPath())));
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

    public void flowerItem(DeferredBlock<Block> block) {
        this.withExistingParent(block.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0",  ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                        "block/" + block.getId().getPath()));
    }

    private void trimmedArmorItem(DeferredItem<Item> itemDeferredItem) {
        final String MOD_ID = SculkyMod.MOD_ID; // Change this to your mod id

        if(itemDeferredItem.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {
                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = armorItem.toString();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemDeferredItem.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace()  + ":item/" + trimNameResLoc.getPath()))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "item/" + itemDeferredItem.getId().getPath()));
            });
        }
    }
}
