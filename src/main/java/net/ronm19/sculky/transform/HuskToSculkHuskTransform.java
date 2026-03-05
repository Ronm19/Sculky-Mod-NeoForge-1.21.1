package net.ronm19.sculky.transform;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.ronm19.sculky.api.interfaces.LightningTransform;
import net.ronm19.sculky.entity.ModEntities;

public class HuskToSculkHuskTransform  implements LightningTransform {

    @Override
    public boolean canTransform(Entity entity, ServerLevel level) {
        if (!(entity instanceof Husk)) return false;

        BlockPos pos = entity.blockPosition();
        return level.getBlockState(pos.below()).is(Blocks.SCULK);
    }

    @Override
    public void transform(Entity entity, ServerLevel level, LightningBolt lightning) {
        if (!(entity instanceof Husk husk)) return;

        var sculkHusk = ModEntities.SCULK_HUSK.get().create(level);
        if (sculkHusk == null) return;

        sculkHusk.moveTo(
                husk.getX(),
                husk.getY(),
                husk.getZ(),
                husk.getYRot(),
                husk.getXRot()
        );

        sculkHusk.setHealth(husk.getHealth());
        if (husk.hasCustomName()) {
            sculkHusk.setCustomName(husk.getCustomName());
        }

        level.addFreshEntity(sculkHusk);
        husk.discard();
    }
}
