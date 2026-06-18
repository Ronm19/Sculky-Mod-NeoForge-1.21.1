package net.ronm19.sculky.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class SculkyItemProperties {

    public static void register() {
        registerSculkBow();
    }

    private static void registerSculkBow() {
        ItemProperties.register(
                ModItems.SCULK_BOW.get(),
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) -> entity != null
                        && entity.isUsingItem()
                        && entity.getUseItem() == stack ? 1.0F : 0.0F
        );

        ItemProperties.register(
                ModItems.SCULK_BOW.get(),
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }

                    if (entity.getUseItem() != stack) {
                        return 0.0F;
                    }

                    return (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
                }
        );
    }
}