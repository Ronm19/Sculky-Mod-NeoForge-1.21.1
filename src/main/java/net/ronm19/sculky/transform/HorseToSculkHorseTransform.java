package net.ronm19.sculky.transform;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.api.interfaces.LightningTransform;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.custom.SculkHorseEntity;
import net.ronm19.sculky.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.ronm19.sculky.entity.ModEntities;

public class HorseToSculkHorseTransform implements LightningTransform {

    @Override
    public boolean canTransform(Entity entity, ServerLevel level) {
        if (!(entity instanceof Horse)) return false;

        BlockPos pos = entity.blockPosition();
        return level.getBlockState(pos.below()).is(Blocks.SCULK);
    }

    @Override
    public void transform(Entity entity, ServerLevel level, LightningBolt lightning) {
        if (!(entity instanceof Horse horse)) return;

        var sculkHorse = ModEntities.SCULK_HORSE.get().create(level);
        if (sculkHorse == null) return;

        sculkHorse.moveTo(
                horse.getX(),
                horse.getY(),
                horse.getZ(),
                horse.getYRot(),
                horse.getXRot()
        );

        if (horse.isTamed() && horse.getOwner() instanceof Player owner) {
            sculkHorse.tame(owner);
        }

        sculkHorse.setHealth(horse.getHealth());
        if (horse.hasCustomName()) {
            sculkHorse.setCustomName(horse.getCustomName());
        }

        level.addFreshEntity(sculkHorse);
        horse.discard();
    }
}
