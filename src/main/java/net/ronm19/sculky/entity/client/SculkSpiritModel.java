package net.ronm19.sculky.entity.client;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelPart;
import net.ronm19.sculky.entity.custom.SculkSpiritEntity;
import org.jetbrains.annotations.NotNull;

public class SculkSpiritModel extends HierarchicalModel<SculkSpiritEntity> {
    private final VexModel vexModel;

    public SculkSpiritModel(ModelPart root) {
        this.vexModel = new VexModel(root);
    }

    @Override
    public void setupAnim(SculkSpiritEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.vexModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.vexModel.root();
    }
}