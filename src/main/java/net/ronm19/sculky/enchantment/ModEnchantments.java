package net.ronm19.sculky.enchantment;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.enchantment.custom.InfectionEnchantmentEffect;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> INFECTION = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "infection"));

    public static void bootstrap( BootstrapContext<Enchantment> context ) {
        var enchantment = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        register(context, INFECTION, Enchantment.enchantment(
                        Enchantment.definition(
                                items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),              // applies to melee weapons
                                items.getOrThrow(ItemTags.SWORD_ENCHANTABLE),              // swords included
                                5,                                                         // rarity (adjust higher for rarer enchant)
                                2,                                                         // weight
                                Enchantment.dynamicCost(10, 8),                            // min cost formula
                                Enchantment.dynamicCost(25, 8),                            // max cost formula
                                3,                                                         // max level (Infection I–III)
                                EquipmentSlotGroup.MAINHAND))                              // only for main hand
                .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,               // triggers when you hit an enemy
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new InfectionEnchantmentEffect(1))                         // applies infection effect logic
        );

    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}
