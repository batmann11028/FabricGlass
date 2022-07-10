package mine.block.glass.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mine.block.glass.blocks.entity.TerminalBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Objects;

public class TerminalBlockEntityRenderer implements BlockEntityRenderer<TerminalBlockEntity> {

    public TerminalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(TerminalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

//        matrices.translate(0.25f, 0.5f-(0.25f/2f), 0.80f);

        float scale = 0.25f;

        matrices.translate(0.5, 0.5, 0.5);

        var e = new float[] {45, entity.facing.getOffsetX(), entity.facing.getOffsetY(), entity.facing.getOffsetZ() };

        boolean needBreak = false;
        for (float v : e) {
            if(needBreak) break;
            if(v == e[0]) continue;
            if(v != 0) {
                int axis = ArrayUtils.indexOf(e, v) - 1;
                switch (axis) {
                    case 0 -> {
                        matrices.multiply(new Quaternion(45, 0, 0, true));
                        needBreak = true;
                    }
                    case 1 -> {
                        matrices.multiply(new Quaternion(0, 45, 0, true));
                        needBreak = true;
                    }
                    case 2 -> {
                        matrices.multiply(new Quaternion(0, 0, 45, true));
                        needBreak = true;
                    }
                }
            }
        }

        matrices.translate(-0.5, -0.5, -0.5);
        matrices.translate(0.375, 0.375, 0.375);
        matrices.translate(entity.facing.getOffsetX() * -0.4D, entity.facing.getOffsetY() * -0.4D, entity.facing.getOffsetZ() * -0.4D);

        matrices.scale(scale, scale, scale);

        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        BlockModelRenderer blockModelRenderer = blockRenderManager.getModelRenderer();

        int lightAbove = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(entity.getWorld()), entity.getPos().up());

        blockModelRenderer.render(matrices.peek(),
                vertexConsumers.getBuffer(RenderLayers.getBlockLayer(Blocks.GLASS.getDefaultState())),
                Blocks.GLASS.getDefaultState(),
                blockRenderManager.getModel(Blocks.GLASS.getDefaultState()),
                1f,
                1f,
                1f,
                lightAbove,
                OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}
