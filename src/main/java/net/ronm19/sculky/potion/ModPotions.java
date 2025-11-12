package net.ronm19.sculky.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.effect.ModEffects;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, SculkyMod.MOD_ID);

    public static final Holder<Potion> SCULK_INFECTION_POTION = POTIONS.register("sculk_infection_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.SCULK_INFECTION_EFFECT, 700, 1)));

    public static void register( IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
