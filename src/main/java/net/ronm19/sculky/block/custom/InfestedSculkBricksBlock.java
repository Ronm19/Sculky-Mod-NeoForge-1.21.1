package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.ronm19.sculky.SculkyMod;

public class InfestedSculkBricksBlock extends Block {

    // Tag: data/sculky/tags/entity_types/sculk_mobs.json
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> SCULK_MOBS =
            TagKey.create(
                    Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "sculk_mobs")
            );

    public InfestedSculkBricksBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) return;

        if (!(entity instanceof LivingEntity living)) return;

        // Sculk mobs are immune
        if (living.getType().is(SCULK_MOBS)) return;

        // Non-sculk mobs
        if (living instanceof Mob) {
            living.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    60, // 1 second
                    0,
                    true,
                    false
            ));
            return;
        }

        // Players (optional – comment out if you don't want this)
        if (living instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    10, // 0.5 seconds
                    0,
                    true,
                    false
            ));
        }
    }
}
