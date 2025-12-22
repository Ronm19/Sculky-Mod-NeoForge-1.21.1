package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EchoConduitItem extends Item {

    /* ============================= */
    /*            CONFIG             */
    /* ============================= */

    private static final int MAX_CHARGE_TICKS = 15;
    private static final int MIN_CHARGE_TICKS = 12;

    private static final float TAP_DAMAGE = 20.0F;
    private static final float MAX_DAMAGE = 50.0F;
    private static final float WARDEN_BONUS_DAMAGE = 30.0F;

    private static final double RANGE = 80.0D;
    private static final double BASE_KNOCKBACK = 4.5D;

    private static final int TAP_COOLDOWN = 40;
    private static final int CHARGED_COOLDOWN = 240;

    // Beam step (smooth particles)
    private static final double BEAM_STEP = 0.5D;

    /**
     * Items in this tag form the "Infested Sculk armor set".
     * FULL SET => IMMUNE to Echo Conduit.
     */
    private static final TagKey<Item> INFESTED_SCULK_ARMOR =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("sculky", "infested_sculk_armor"));

    public EchoConduitItem(Properties props) {
        super(props);
    }

    /* ============================= */
    /*        RIGHT CLICK            */
    /* ============================= */

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        // TAP SHOT
        if (!player.isUsingItem()) {
            playShootSound(level, player);

            if (!level.isClientSide) {
                fireShot((ServerLevel) level, player, TAP_DAMAGE, BASE_KNOCKBACK, false);
                player.getCooldowns().addCooldown(this, TAP_COOLDOWN);
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }

            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    /* ============================= */
    /*        CHARGE HANDLING        */
    /* ============================= */


    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide) return;

        int charge = getUseDuration(stack) - remaining;

        if (charge == MIN_CHARGE_TICKS) {
            level.playSound(null,
                    entity.blockPosition(),
                    SoundEvents.SCULK_SENSOR_HIT,
                    SoundSource.PLAYERS,
                    0.8F, 1.2F);
        }
    }

    /* ============================= */
    /*        RELEASE (CHARGED)      */
    /* ============================= */

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {

        if (!(entity instanceof Player player)) return;

        int chargeTicks = getUseDuration(stack) - timeLeft;
        if (chargeTicks < MIN_CHARGE_TICKS) return;

        float chargePercent = Math.min(chargeTicks / (float) MAX_CHARGE_TICKS, 1.0F);
        float damage = TAP_DAMAGE + (MAX_DAMAGE - TAP_DAMAGE) * chargePercent;
        double knockback = BASE_KNOCKBACK + chargePercent;

        playShootSound(level, player);

        if (!level.isClientSide) {
            fireShot((ServerLevel) level, player, damage, knockback, chargePercent >= 1.0F);

            player.getCooldowns().addCooldown(
                    this,
                    (int) (CHARGED_COOLDOWN * (0.8F + chargePercent))
            );

            stack.hurtAndBreak(
                    1 + (int) (chargePercent * 2),
                    player,
                    EquipmentSlot.MAINHAND
            );
        }
    }

    /* ============================= */
    /*        CORE FIRE LOGIC        */
    /* ============================= */

    private void fireShot(ServerLevel level, Player player, float damage, double knockback, boolean fullyCharged) {

        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = start.add(look.scale(RANGE));

        // smoother beam: every 0.5 blocks
        spawnBeam(level, start, look);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                level,
                player,
                start,
                end,
                player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0),
                e -> e instanceof LivingEntity && e != player && player.hasLineOfSight(e)
        );

        if (hit == null) {
            // server broadcast sound anyway (so other players hear it consistently)
            level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
            return;
        }

        LivingEntity target = (LivingEntity) hit.getEntity();

        // ✅ FULL infested set => IMMUNE
        if (isWearingFullInfestedSet(target)) {
            // Impact burst still looks cool, but NO damage/KB/chain
            spawnImpactBurst(level, target);
            level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
            return;
        }

        float finalDamage = damage;

        // Warden bonus + aggro
        if (target instanceof net.minecraft.world.entity.monster.warden.Warden warden) {
            finalDamage += WARDEN_BONUS_DAMAGE;
            warden.setAttackTarget(player);
        }

        // Damage
        target.hurt(level.damageSources().sonicBoom(player), finalDamage);

        // Knockback
        Vec3 push = target.position().subtract(player.position()).normalize().scale(knockback);
        target.push(push.x, 0.4, push.z);

        // Hit particles
        spawnImpactBurst(level, target);

        // Full charge chain
        if (fullyCharged) {
            propagateThroughSculk(level, player, target.position(), look, finalDamage * 0.75F);
        }

        // Broadcast sound
        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    /* ============================= */
    /*     SCULK CHAIN PROPAGATION   */
    /* ============================= */

    private void propagateThroughSculk(ServerLevel level, Player player, Vec3 start, Vec3 dir, float damage) {

        Vec3 pos = start;

        for (int i = 0; i < 10; i++) {
            pos = pos.add(dir.scale(1.5));
            BlockPos bp = BlockPos.containing(pos);

            if (!level.getBlockState(bp).is(net.minecraft.world.level.block.Blocks.SCULK)) break;

            List<LivingEntity> entities = level.getEntitiesOfClass(
                    LivingEntity.class,
                    new AABB(bp).inflate(2.0)
            );

            for (LivingEntity e : entities) {
                if (e == player) continue;

                // ✅ immunity applies to chain too
                if (isWearingFullInfestedSet(e)) continue;

                // Pull inward
                Vec3 pull = pos.subtract(e.position()).normalize().scale(0.4);
                e.push(pull.x, 0.1, pull.z);

                // Damage
                e.hurt(level.damageSources().sonicBoom(player), damage);

                // Knockback blast outward
                Vec3 blast = e.position().subtract(pos).normalize().scale(1.5);
                e.push(blast.x, 0.4, blast.z);
            }

            level.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    pos.x, pos.y + 0.8, pos.z,
                    1, 0, 0, 0, 0
            );
        }
    }

    /* ============================= */
    /*        BEAM VISUAL            */
    /* ============================= */

    private void spawnBeam(ServerLevel level, Vec3 start, Vec3 dir) {
        int steps = (int) Math.ceil(RANGE / BEAM_STEP);

        for (int i = 0; i <= steps; i++) {
            Vec3 p = start.add(dir.scale(i * BEAM_STEP));
            level.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    p.x, p.y, p.z,
                    1, 0, 0, 0, 0
            );
        }
    }

    private void spawnImpactBurst(ServerLevel level, LivingEntity target) {
        double x = target.getX();
        double y = target.getEyeY();
        double z = target.getZ();

        // Main impact marker
        level.sendParticles(ParticleTypes.SONIC_BOOM, x, y, z, 1, 0, 0, 0, 0);

        // Sculk-ish burst around head
        level.sendParticles(ParticleTypes.SCULK_SOUL, x, y, z, 12, 0.25, 0.25, 0.25, 0.02);
    }

    /* ============================= */
    /*      ARMOR IMMUNITY CHECK     */
    /* ============================= */

    private boolean isWearingFullInfestedSet(LivingEntity entity) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);

        return !head.isEmpty() && head.is(INFESTED_SCULK_ARMOR)
                && !chest.isEmpty() && chest.is(INFESTED_SCULK_ARMOR)
                && !legs.isEmpty() && legs.is(INFESTED_SCULK_ARMOR)
                && !feet.isEmpty() && feet.is(INFESTED_SCULK_ARMOR);
    }

    /* ============================= */
    /*      SOUND SYNC (FIX)         */
    /* ============================= */

    private void playShootSound(Level level, Player player) {
        // Shooter hears instantly
        if (level.isClientSide) {
            level.playLocalSound(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.WARDEN_SONIC_BOOM,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F,
                    false
            );
        }
    }
}
