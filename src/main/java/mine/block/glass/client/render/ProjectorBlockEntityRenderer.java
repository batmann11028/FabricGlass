package mine.block.glass.client.render;

import mine.block.glass.blocks.ProjectorBlock;
import mine.block.glass.blocks.entity.ProjectorBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

import java.util.Objects;

public class ProjectorBlockEntityRenderer extends ProjectionBlockRenderer<ProjectorBlockEntity> {

    public ProjectorBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    private static float interpolateRotation(float prevRotation, float nextRotation, float partialTick)
    {
        float f3;

        f3 = nextRotation - prevRotation;
        while (f3 < -180.0F) {
            f3 += 360.0F;
        }

        while(f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return prevRotation + partialTick * f3;
    }


    @Override
    public void render(ProjectorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5D, 0.5D, 0.5D);

        float scale = 0.5f;

        Direction direction = entity.facing;
        if(direction == Direction.DOWN) {
            matrices.multiply(new Quaternion(180.0f, 0, 0, true));
        } else if(direction.getHorizontal() >= 0) {
            int horizontalIndex = direction.getHorizontal();
            matrices.multiply(new Quaternion(0, -horizontalIndex * 90f, 0f, true));
            matrices.multiply(new Quaternion(90f, 0f, 0f, true));
        }

        matrices.multiply(new Quaternion(0f, interpolateRotation(entity.rotationBeacon, entity.rotationBeaconPrev, tickDelta), 0f, true));
        matrices.translate(-0.25D, -0.25D, -0.25D);
        matrices.scale(scale, scale, scale);

        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        BlockModelRenderer blockModelRenderer = blockRenderManager.getModelRenderer();

        int lightAbove = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(entity.getWorld()), entity.getPos().up());

        blockModelRenderer.render(matrices.peek(),
                vertexConsumers.getBuffer(RenderLayers.getBlockLayer(Blocks.BEACON.getDefaultState())),
                Blocks.BEACON.getDefaultState(),
                blockRenderManager.getModel(Blocks.BEACON.getDefaultState()),
                1f,
                1f,
                1f,
                lightAbove,
                OverlayTexture.DEFAULT_UV);

        matrices.pop();

        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
    }
}
