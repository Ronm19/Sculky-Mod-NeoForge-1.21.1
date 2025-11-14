package net.ronm19.sculky.worldgen.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.worldgen.ModConfiguredFeatures;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower INFESTED_SCULK = new TreeGrower(SculkyMod.MOD_ID + ":sculk",
            Optional.empty(), Optional.of(ModConfiguredFeatures.INFESTED_SCULK_KEY), Optional.empty());
}
