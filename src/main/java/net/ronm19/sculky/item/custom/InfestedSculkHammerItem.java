package net.ronm19.sculky.item.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.ronm19.sculky.api.interfaces.InfestedSculkTool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class InfestedSculkHammerItem extends DiggerItem implements InfestedSculkTool {

    // tuning values (change these to balance)
    private static final float KNOCKBACK_FORCE = 6.0f;         // how far targets are pushed
    private static final int INFECTION_DURATION = 140;        // ticks (7s)
    private static final float INFECTED_CHANCE = 0.10f;       // chance to place infested sculk
    private static final int HAMMER_RANGE = 1;                // radius; 1 => 3x3 plane

    public InfestedSculkHammerItem(Tier pTier, Properties pProperties) {
        super(pTier, BlockTags.MINEABLE_WITH_PICKAXE, pProperties);
    }


    // Utility: compute AOE target blocks based on trace direction & range (3x3 plane)
    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initialBlockPos, Player player) {
        List<BlockPos> positions = new ArrayList<>();

        ClipContext ctx = new ClipContext(
                player.getEyePosition(1.0f),
                player.getEyePosition(1.0f).add(player.getViewVector(1.0f).scale(6.0f)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        );

        BlockHitResult traceResult = player.level().clip(ctx);
        if (traceResult.getType() == HitResult.Type.MISS) {
            return positions;
        }

        Direction face = traceResult.getDirection();

        // plane selection: if face is up/down -> XZ plane; north/south -> X/Y; east/west -> Z/Y
        if (face == Direction.UP || face == Direction.DOWN) {
            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    positions.add(new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY(), initialBlockPos.getZ() + z));
                }
            }
        } else if (face == Direction.NORTH || face == Direction.SOUTH) {
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    positions.add(new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY() + y, initialBlockPos.getZ()));
                }
            }
        } else { // EAST or WEST
            for (int z = -range; z <= range; z++) {
                for (int y = -range; y <= range; y++) {
                    positions.add(new BlockPos(initialBlockPos.getX(), initialBlockPos.getY() + y, initialBlockPos.getZ() + z));
                }
            }
        }

        return positions;
    }

    // Called when hammer mines a single block - we'll allow the default behavior but you could add small perks here
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        // give tiny sculk feedback when mining certain materials (optional)
        if (!level.isClientSide() && entity instanceof Player) {
            if (state.is(Blocks.DEEPSLATE) || state.is(Blocks.STONE)) {
                ((Player) entity).addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 80, 0));
            }
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    // Right-click (useOn) to perform AOE smash on targeted block (server-side only)
    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return net.minecraft.world.InteractionResult.PASS;

        Player player = context.getPlayer();
        if (!(player instanceof Player)) return super.useOn(context);

        ItemStack stack = context.getItemInHand();
        BlockPos clicked = context.getClickedPos();

        // compute AOE list using trace direction relative to player + clicked pos
        List<BlockPos> toDestroy = getBlocksToBeDestroyed(HAMMER_RANGE, clicked, player);

        // break blocks in the list (server side) - respect hardness & drop rules
        int broken = 0;
        for (BlockPos p : toDestroy) {
            BlockState s = level.getBlockState(p);
            if (s.isAir()) continue;
            // avoid breaking bedrock etc.
            if (s.getDestroySpeed(level, p) < 0) continue;

            // check harvestability (player capability) — very basic check here
            // you can improve by checking s.requiresCorrectToolForDrops() and player's tool tier
            // For now: break blocks and spawn drops
            level.destroyBlock(p, true, player);
            broken++;
        }

            // play anvil landing sound for smash
            level.playSound(null, clicked, SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 1.0F);

            // particle burst
            if (level instanceof ServerLevel slevel) {
                slevel.sendParticles(ParticleTypes.POOF, clicked.getX() + 0.5, clicked.getY() + 0.5, clicked.getZ() + 0.5, 12, 0.4, 0.4, 0.4, 0.02);
            }

        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    // When you hit an entity with the hammer: knockback far, apply infection and anvil sound
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        if (!level.isClientSide()) {
            // 1) Huge knockback: push the target away from attacker
            Vec3 diff = target.position().subtract(attacker.position()).normalize();
            // scale by force & give slight vertical lift
            target.push(diff.x * KNOCKBACK_FORCE, 0.8d, diff.z * KNOCKBACK_FORCE);

            // 2) Infection effect: apply poison + slowness + darkness (tune as you want)
            target.addEffect(new MobEffectInstance(MobEffects.POISON, INFECTION_DURATION, 1));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, INFECTION_DURATION / 2, 1));
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, INFECTION_DURATION / 3, 0));

            // 3) Play anvil landing sound at target
            level.playSound(null, target.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.95F);

            // 4) particles around target
            if (level instanceof ServerLevel slevel) {
                slevel.sendParticles(ParticleTypes.POOF, target.getX(), target.getY() + 1.0, target.getZ(), 10, 0.5, 0.5, 0.5, 0.01);
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean mineBlock( ItemStack stack, Level level, BlockState state, BlockPos pos, Player player ) {
        return true;
    }

    // Optional: when landing on ground after knockback, play extra sound or spawn sculk. You can attach this to an event listener if you want.
}
