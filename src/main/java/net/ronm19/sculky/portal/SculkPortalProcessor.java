package net.ronm19.sculky.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.portal.DimensionTransition;

import javax.annotation.Nullable;

public class SculkPortalProcessor extends PortalProcessor {
    private Portal portal;
    private BlockPos entryPosition;
    private int portalTime;
    private boolean insidePortalThisTick;

    public SculkPortalProcessor( Portal portal, BlockPos entryPosition) {
        super(portal,  entryPosition);
        this.portal = portal;
        this.entryPosition = entryPosition;
        this.insidePortalThisTick = true;
    }

    public boolean processPortalTeleportation( ServerLevel level, Entity entity, boolean canChangeDimensions) {
        if (!this.insidePortalThisTick) {
            this.decayTick();
            return false;
        } else {
            this.insidePortalThisTick = false;
            return canChangeDimensions && this.portalTime++ >= this.portal.getPortalTransitionTime(level, entity);
        }
    }

    @Nullable
    public DimensionTransition getPortalDestination( ServerLevel level, Entity entity) {
        return this.portal.getPortalDestination(level, entity, this.entryPosition);
    }

    public Portal.Transition getPortalLocalTransition() {
        return this.portal.getLocalTransition();
    }

    private void decayTick() {
        this.portalTime = Math.max(this.portalTime - 4, 0);
    }

    public boolean hasExpired() {
        return this.portalTime <= 0;
    }

    public BlockPos getEntryPosition() {
        return this.entryPosition;
    }

    public void updateEntryPosition(BlockPos entryPosition) {
        this.entryPosition = entryPosition;
    }

    public int getPortalTime() {
        return this.portalTime;
    }

    public boolean isInsidePortalThisTick() {
        return this.insidePortalThisTick;
    }

    public void setAsInsidePortalThisTick(boolean insidePortalThisTick) {
        this.insidePortalThisTick = insidePortalThisTick;
    }

    public boolean isSamePortal(Portal portal) {
        return this.portal == portal;
    }
}
