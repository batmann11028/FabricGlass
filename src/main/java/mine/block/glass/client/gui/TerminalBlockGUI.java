package mine.block.glass.client.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import mine.block.glass.GLASS;
import mine.block.glass.persistence.Channel;
import mine.block.glass.server.GLASSPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TerminalBlockGUI extends SyncedGuiDescription {
    public static final ScreenHandlerType<TerminalBlockGUI> SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("glass", "terminal_screen"), TerminalBlockGUI::new);

    public BlockPos pos;

    private static final int WIDTH = 7;
    private static final int HEIGHT = 10;

    private final WListPanel<Channel, WButton> channelList;
    private WButton removeChannelButton;

    public TerminalBlockGUI(int syncId, PlayerInventory playerInventory, PacketByteBuf context) {
        super(SCREEN_HANDLER_TYPE, syncId, playerInventory);

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.setSize(7*18*2, 6*18*2);
        root.setInsets(Insets.ROOT_PANEL);

        pos = context.readBlockPos();

        NbtCompound nbt = context.readNbt();

        ArrayList<Channel> channels = new ArrayList<>();

        assert nbt != null;
        NbtList _channels = nbt.getList("channels", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < _channels.size(); i++) {
            NbtCompound channel = _channels.getCompound(i);
            @Nullable BlockPos bpos = (channel.contains("linked_pos")) ? null : BlockPos.fromLong(channel.getLong("linked_pos"));
            Channel channel1 = new Channel(channel.getString("name"), bpos);
            channels.add(channel1);
        }

        if(channels.size() == 0) {
            ClientPlayNetworking.send(GLASSPackets.POPULATE_DEFAULT_CHANNEL.ID, PacketByteBufs.empty());

            channels.add(new Channel("Default", null));
        }

        GLASS.LOGGER.info("[GUI-CHANNELS] " + channels + " [WORLD] " + world);

        channelList = new WListPanel<>(channels, WButton::new, (Channel channel, WButton btn) -> {

            btn.setOnClick(() -> {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                buf.writeString(btn.getLabel().getString());

                ClientPlayNetworking.send(GLASSPackets.TERMINAL_CHANNEL_CHANGED.ID, buf);

                btn.setEnabled(false);
                removeChannelButton.setEnabled(true);
            });

            if(channel.linkedBlock() == pos) {
                btn.setEnabled(false);
            }

            btn.setLabel(Text.literal(channel.name()));
        });

        channelList.setListItemHeight(18);
        root.add(channelList, 0, 1, WIDTH, HEIGHT - 1);

        removeChannelButton = new WButton();

        removeChannelButton.setOnClick(() -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            buf.writeString("");

            channelList.layout();

            ClientPlayNetworking.send(GLASSPackets.REMOVE_LINKED_CHANNEL.ID, buf);
        });

        removeChannelButton.setLabel(Text.literal("Unlink From Channel"));

        if(channels.stream().filter(channel -> channel.linkedBlock() == pos).toList().isEmpty()) {
            removeChannelButton.setEnabled(false);
        }

        removeChannelButton.setSize(8, 10);

        root.add(removeChannelButton, 0, 1);

        root.validate(this);
    }
}
