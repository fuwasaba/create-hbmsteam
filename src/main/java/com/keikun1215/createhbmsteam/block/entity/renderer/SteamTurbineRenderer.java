package com.keikun1215.createhbmsteam.block.entity.renderer;

import com.keikun1215.createhbmsteam.block.SteamTurbineBlock;
import com.keikun1215.createhbmsteam.block.entity.SteamTurbineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SteamTurbineRenderer extends KineticBlockEntityRenderer<SteamTurbineBlockEntity> {
    public SteamTurbineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    protected void renderSafe(SteamTurbineBlockEntity turbine, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(turbine, partialTicks, ms, buffer, light, overlay);
        kineticRotationTransform(
                turbine.getBlades(),
                turbine,
                turbine.getBlockState().getValue(SteamTurbineBlock.FACING).getAxis(),
                getAngleForTe(turbine, turbine.getBlockPos(),
                        turbine.getBlockState().getValue(SteamTurbineBlock.FACING).getAxis()
                ),
                light
        ).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
    public static float getAngleForTe(SteamTurbineBlockEntity be, final BlockPos pos, Direction.Axis axis) {
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float offset = getRotationOffsetForPosition(be, pos, axis);
        float angle = ((time * be.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
        return angle;
    }
}
