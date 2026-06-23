package net.ronm19.sculky.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.ronm19.sculky.SculkyMod;

public class ModAdvancementHelper {
    public static void grant(ServerPlayer player, String advancementId, String criterionName) {
        AdvancementHolder advancement = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, advancementId));

        if (advancement == null) {
            return;
        }

        if (!player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            player.getAdvancements().award(advancement, criterionName);
        }
    }
}