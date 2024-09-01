package alexthw.starbunclemania.client;

import com.hollingsworth.arsnouveau.client.renderer.entity.StarbuncleRenderer;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;

public class ResizedStarbRender extends StarbuncleRenderer {
    public ResizedStarbRender(EntityRendererProvider.Context manager) {
        super(manager);
    }


    @Override
    public void defaultRender(PoseStack poseStack, Starbuncle animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer,
                              float yaw, float partialTick, int packedLight){
        poseStack.pushPose();
        poseStack.scale(3, 3, 3);
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        poseStack.popPose();
    }
}
