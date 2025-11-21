package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SculkStalkerEntity extends Spider {

    private int stealthTimer = 0;
    private boolean inStealth = false;

    public SculkStalkerEntity(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.xpReward = 18;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        // Ambush attack → runs fast when close
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, true));

        // Wander
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));

        // Look Around
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // Light level check (go invisible in darkness)
        if (this.level().getMaxLocalRawBrightness(this.blockPosition()) <= 4) {
            enterStealth();
        } else {
            exitStealth();
        }

        // Backstab bonus damage logic
        if (this.getTarget() != null) {
            tryBackstab(this.getTarget());
        }
    }

    private void enterStealth() {
        if (!inStealth) {
            inStealth = true;
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 999999, 0, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 999999, 1, false, false));
        }
    }

    private void exitStealth() {
        if (inStealth) {
            inStealth = false;
            this.removeEffect(MobEffects.INVISIBILITY);
            this.removeEffect(MobEffects.MOVEMENT_SPEED);
        }
    }

    // Backstab if behind player
    private void tryBackstab(LivingEntity target) {
        Vec3 stalkerDir = this.getLookAngle().normalize();
        Vec3 targetDir = target.getViewVector(1.0F).normalize();

        double dot = stalkerDir.dot(targetDir);

        // -1 = opposite direction -> behind target
        if (dot < -0.7) {
            // Apply bonus once per hit
            if (this.distanceTo(target) < 1.8F) {
                target.hurt(this.damageSources().mobAttack(this), (float)(getAttackDamage() * 2.0F));
            }
        }
    }

    private double getAttackDamage() {
        return this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public static AttributeSupplier.Builder createSculkStalkerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public int getStealthTimer() {
        return stealthTimer;
    }

    public void setStealthTimer( int stealthTimer ) {
        this.stealthTimer = stealthTimer;
    }
}
