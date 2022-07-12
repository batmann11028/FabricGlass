package mine.block.glass.blocks.entity;

import mine.block.glass.blocks.GLASSBlocks;
import mine.block.glass.blocks.ProjectorBlock;
import mine.block.glass.client.gui.ProjectorBlockGUI;
import mine.block.glass.client.gui.TerminalBlockGUI;
import mine.block.glass.persistence.ChannelManagerPersistence;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ProjectorBlockEntity extends ProjectionBlockBase implements ExtendedScreenHandlerFactory {
    public static BlockEntityType<ProjectorBlockEntity> BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder.create(ProjectorBlockEntity::new, GLASSBlocks.PROJECTOR).build();

    public Direction facing = Direction.UP;

    public float rotationBeacon, rotationBeaconPrev;
    public int wirelessTime;

    public ProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.putInt("facing", facing.getId());
        tag.putFloat("rotationBeacon", rotationBeacon);
        tag.putFloat("rotationBeaconPrev", rotationBeaconPrev);
        tag.putString("channel", channel);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        facing = Direction.byId(tag.getInt("facing"));
        channel = tag.getString("channel");
        rotationBeacon = tag.getFloat("rotationBeacon");
        rotationBeaconPrev = tag.getFloat("rotationBeaconPrev");

        super.readNbt(tag);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("G.L.A.S.S Projector");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {

        PacketByteBuf buf = PacketByteBufs.create();

        ChannelManagerPersistence channelManager = ChannelManagerPersistence.get(player.getWorld());

        buf.writeString(channel);
        buf.writeBlockPos(pos);
        buf.writeNbt(channelManager.writeNbt(new NbtCompound()));

        return new ProjectorBlockGUI(syncId, inventory, buf);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        ChannelManagerPersistence channelManager = ChannelManagerPersistence.get(player.getWorld());
        buf.writeString(channel);
        buf.writeBlockPos(pos);
        buf.writeNbt(channelManager.writeNbt(new NbtCompound()));
    }



    public static void tick(World world1, BlockPos pos, BlockState state1, ProjectorBlockEntity be) {
        be.tick();

        boolean old = be.active;

        be.active = world1.isReceivingRedstonePower(pos);

        if(old != be.active) {
            if(be.active) {

            }
        }

        float rotationFactor = be.active ? (1.0F - ((float) be.fadeoutTime / FADEOUT_TIME)) : ((float) be.fadeoutTime / FADEOUT_TIME);
        be.rotationBeacon += 20F * rotationFactor;
        be.rotationBeaconPrev = be.rotationBeacon;

        // TODO: Wireless in future update?

//        if(be.active && !be.wirelessPos.isEmpty())
//        {
//            wirelessTime++;
//        }
//        else
//        {
//            wirelessTime = 0;
//        }
    }
}
