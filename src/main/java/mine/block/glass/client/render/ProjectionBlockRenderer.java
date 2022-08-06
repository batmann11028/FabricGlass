package mine.block.glass.client.render;

import mine.block.glass.blocks.ProjectorBlock;
import mine.block.glass.blocks.entity.ProjectionBlockBase;
import mine.block.glass.blocks.entity.ProjectorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.BlockView;

public class ProjectionBlockRenderer<T extends ProjectionBlockBase> implements BlockEntityRenderer<T> {
    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(entity.active) {
            // do portal shit
        }

        drawPlanes(matrices, entity, 1F, 1F, 1F, -1F, 0.501D, tickDelta);
    }

    private void drawPlanes(MatrixStack matrices, T entity, float r, float g, float b, float alpha, double pushback, float partialTick) {
        boolean calcAlpha = alpha == -1;
        if(calcAlpha) //calculate the alpha. not drawing planes.
        {
            alpha = (float)Math.pow(MathHelper.clamp((entity.fadeoutTime - partialTick) / (float)ProjectionBlockBase.FADEOUT_TIME, 0F, 1F), 0.5D);
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for(Direction face : entity.activeFaces)
        {
            if(shouldSideBeRendered(entity, entity.getWorld().getBlockState(entity.getPos()), entity.getWorld(), entity.getPos(), face)) {
                continue;
            }

            matrices.push();
            int horiOrient = (face.getAxis() == Direction.Axis.Y ? Direction.UP : face).getOpposite().getHorizontal();
            matrices.multiply(new Quaternion(0F, (face.getId() > 0 ? 180F : 0F) + -horiOrient * 90F, 0F, true));
            if(face.getAxis() == Direction.Axis.Y)
            {
                matrices.multiply(new Quaternion(face == Direction.UP ? -90F : 90F, 0F, 0F, true));
            }

            matrices.translate(0F, 0F, pushback);

            float halfSize = 0.501F;
            bufferbuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            bufferbuilder.vertex(-halfSize,  halfSize, 0F).color(r, g, b, alpha).next();
            bufferbuilder.vertex(-halfSize, -halfSize, 0F).color(r, g, b, alpha).next();
            bufferbuilder.vertex( halfSize, -halfSize, 0F).color(r, g, b, alpha).next();
            bufferbuilder.vertex( halfSize,  halfSize, 0F).color(r, g, b, alpha).next();
            tessellator.draw();

            matrices.pop();
        }
    }

    public boolean shouldSideBeRendered(T entity, BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side)
    {
        BlockState lockstate = blockAccess.getBlockState(pos.offset(side));

        if (blockState != lockstate)
        {
            return true;
        }

        if (entity.getPos() == pos)
        {
            return false;
        }

        return GlassBlock.shouldDrawSide(entity.getWorld().getBlockState(entity.getPos()), entity.getWorld(), entity.getPos(), side, pos.offset(side));
    }
}
