package net.ronm19.sculky;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.effect.ModEffects;
import net.ronm19.sculky.enchantment.ModEnchantmentEffects;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.ModArmorMaterials;
import net.ronm19.sculky.item.ModCreativeModeTabs;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.network.ModNetworking;
import net.ronm19.sculky.potion.ModPotions;
import net.ronm19.sculky.setup.ModSetup;
import net.ronm19.sculky.sounds.ModSounds;
import net.ronm19.sculky.util.ModTags;
import net.ronm19.sculky.worldgen.biome.ModBiomes;
import net.ronm19.sculky.worldgen.biome.ModSurfaceRules;
import net.ronm19.sculky.worldgen.ore.ModPlacementModifierTypes;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import terrablender.api.SurfaceRuleManager;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SculkyMod.MOD_ID)
public class SculkyMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "sculky";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.

    public SculkyMod( IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModEnchantmentEffects.register(modEventBus);
        ModPlacementModifierTypes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSetup.register(modEventBus);


        NeoForge.EVENT_BUS.register(this);


        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.SCULKBLOOM.getId(), ModBlocks.POTTED_SCULKBLOOM);

            ModBiomes.registerBiomes();

            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeOverworldRules());

        });

        event.enqueueWork(() -> {
                    ItemBlockRenderTypes.setRenderLayer(
                            ModBlocks.INFESTED_SCULK_LEAVES.get(),
                            RenderType.cutoutMipped());
        });
    }


    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
