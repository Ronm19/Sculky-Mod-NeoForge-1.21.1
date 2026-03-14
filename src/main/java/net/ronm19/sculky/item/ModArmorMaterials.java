package net.ronm19.sculky.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;

import java.util.EnumMap;
import java.util.List;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, SculkyMod.MOD_ID);

    public static final Holder<ArmorMaterial> INFESTED_SCULK =
            ARMOR_MATERIALS.register("infested_sculk", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 50);
                        map.put(ArmorItem.Type.LEGGINGS, 50);
                        map.put(ArmorItem.Type.CHESTPLATE, 50);
                        map.put(ArmorItem.Type.HELMET, 50);
                        map.put(ArmorItem.Type.BODY, 59);
                    }), 49, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(ModItems.SCULK_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "infested_sculk"))),
                    0.2f, 0.3f));


    public static void register( IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}