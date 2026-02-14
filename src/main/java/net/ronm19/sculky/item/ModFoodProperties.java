package net.ronm19.sculky.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {

    public static final FoodProperties SCULK_HEARTFRUIT = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.8f)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 600), 1.0f)
            .build();

    public static final FoodProperties TOMATO_SCULK = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 600), 0.25f)
            .build();

    public static final FoodProperties ECHO_JELLY = new FoodProperties.Builder()
            .nutrition(5)
            .saturationModifier(0.7f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600), 1.0f)
            .build();

    public static final FoodProperties SCULK_PASTRY  = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 600), 1.0f)
            .build();

    public static final FoodProperties SOULBITE_COOKIE = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600), 1.0f)
            .build();

    public static final FoodProperties SCULK_APPLE = new FoodProperties.Builder()
            .nutrition(5)              // hunger bars (5 = 2.5 icons)
            .saturationModifier(0.6f)        // saturation
            // effects: (effect, probability)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 5 * 20, 0), 1.0f) // 5s darkness always
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 20, 0), 1.0f) // 20s night vision
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 0), 0.35f) // 35% regen 5s
            .alwaysEdible() // optional: allow eating even if full
            .build();
}
