package mine.block.glass.blocks.entity;

import mine.block.glass.blocks.GLASSBlocks;
import mine.block.glass.blocks.TerminalBlock;
import mine.block.glass.client.gui.TerminalBlockGUI;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

public class TerminalBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public static BlockEntityType<TerminalBlockEntity> BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder.create(TerminalBlockEntity::new, GLASSBlocks.TERMINAL).build();

    public Direction facing = Direction.UP;
    public String channel = "";

    public TerminalBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        tag.putInt("facing", facing.getId());
        tag.putString("channel", channel);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        facing = Direction.byId(tag.getInt("facing"));
        channel = tag.getString("channel");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }


    @Override
    public BlockEntityType<?> getType() {
        return BLOCK_ENTITY_TYPE;
    }

    @Override
    public Text getDisplayName() {
        // Using the block name as the screen title
        return Text.literal("G.L.A.S.S Terminal");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new TerminalBlockGUI(syncId, inventory, PacketByteBufs.create().writeBlockPos(getPos()));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
    }
}
