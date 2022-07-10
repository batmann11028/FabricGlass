package mine.block.glass.blocks;

import mine.block.glass.blocks.entity.TerminalBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TerminalBlock extends BlockWithEntity {
    protected TerminalBlock(Settings settings) {
        super(settings);
    }

    private Direction _tempFacing = Direction.UP;

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        var entity = new TerminalBlockEntity(pos, state);
        entity.facing = _tempFacing;
        return entity;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // You need a Block.createScreenHandlerFactory implementation that delegates to the block entity,
        // such as the one from BlockWithEntity

        if(world.isClient) return ActionResult.PASS;

        ExtendedScreenHandlerFactory handlerFactory = (ExtendedScreenHandlerFactory) state.createScreenHandlerFactory(world, pos);

        player.openHandledScreen(handlerFactory);
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        _tempFacing = Direction.getLookDirectionForAxis(Objects.requireNonNull(ctx.getPlayer()), ctx.getPlayerLookDirection().getAxis());
        return super.getPlacementState(ctx);
    }
}
