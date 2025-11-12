package net.ronm19.sculky.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SculkyMod.MOD_ID);

    public static final Holder<MobEffect> SCULK_INFECTION_EFFECT = MOB_EFFECTS.register("sculk_infection",
            () -> new SculkInfectionEffect(MobEffectCategory.HARMFUL, 0x1a1e2b).addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "sculk_infection_speed"),
                            -0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_SPEED,
                            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "sculk_infection_attack_speed"),
                            -0.10F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));



    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
