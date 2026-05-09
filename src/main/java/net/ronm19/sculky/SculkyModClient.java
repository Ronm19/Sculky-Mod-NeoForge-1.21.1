package net.ronm19.sculky;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.client.*;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = SculkyMod.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent

@EventBusSubscriber(modid = SculkyMod.MOD_ID, value = Dist.CLIENT)
public class SculkyModClient {
    public SculkyModClient( ModContainer container ) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen :: new);
    }

    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {

        // ----------------------------- MONSTERS --------------------------------------- //

        EntityRenderers.register(ModEntities.SCULK_PARASITE.get(), SculkParasiteRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_SENTINEL.get(), SculkSentinelRenderer ::new);
        EntityRenderers.register(ModEntities.SCULK_STALKER.get(), SculkStalkerRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_SHADE.get(), SculkShadeRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_HORROR.get(), SculkHorrorRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_ZOMBIE.get(), SculkZombieRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_HUSK.get(), SculkHuskRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_SKELETON.get(), SculkSkeletonRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_CREEPER.get(), SculkCreeperRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_SPIDER.get(), SculkSpiderRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_ENDERMAN.get(), SculkEndermanRenderer::new);
        EntityRenderers.register(ModEntities.SCULKMITE.get(), SculkmiteRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_SANDSNARE.get(), SculkSandsnareRenderer::new);
        EntityRenderers.register(ModEntities.SALVATORE.get(), SalvatoreRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_PHANTOM.get(), SculkPhantomRenderer::new);
        EntityRenderers.register(ModEntities.SANCTUM_WATCHER.get(), SanctumWatcherRenderer::new);
        EntityRenderers.register(ModEntities.ROYAL_SCULK_KNIGHT.get(), RoyalSculkKnightRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_NECROMANCER.get(), SculkNecromancerRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_BRUTE.get(), SculkBruteRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_BURROWER.get(), SculkBurrowerRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_SLIME.get(), SculkSlimeRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_HERALD.get(), SculkHeraldRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_SNAPPER.get(), SculkSnapperRenderer::new);


        // ----------------------------- PASSIVE --------------------------------------- //

        EntityRenderers.register(ModEntities.SCULK_WOLF.get(), SculkWolfRender::new);
        EntityRenderers.register(ModEntities.SCULK_WOLF_ALPHA.get(), SculkWolfAlphaRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_HORSE.get(), SculkHorseRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_FOX.get(), SculkFoxRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_BAT.get(), SculkBatRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_BEETLE.get(), SculkBeetleRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_TAIL.get(), SculkTailRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_RAT.get(), SculkRatRenderer::new);
        EntityRenderers.register(ModEntities.HOLLOW_HORN.get(), HollowhornRenderer::new);
        EntityRenderers.register(ModEntities.INFESTED_EYE.get(), InfestedEyeRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_HUNTER.get(), SculkHunterRenderer::new);
        EntityRenderers.register(ModEntities.SHADOW_PANTHER.get(), ShadowPantherRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_DOLPHIN.get(), SculkDolphinRenderer::new);
        EntityRenderers.register(ModEntities.SHADOW_PANTHER.get(), ShadowPantherRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_GOLEM.get(), SculkGolemRenderer::new);


        // ----------------------------- MISC --------------------------------------- //

        EntityRenderers.register(ModEntities.SCULK_JAR_PROJECTILE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.SCULK_BOMB_PROJECTILE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.SHADOW_BOLT.get(), ShadowBoltRenderer::new);

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SCULK_PORTAL.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void onClientExtensions( RegisterClientExtensionsEvent event ) {
    }
}