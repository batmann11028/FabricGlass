package mine.block.glass.client.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import mine.block.glass.GLASS;
import mine.block.glass.blocks.entity.TerminalBlockEntity;
import mine.block.glass.components.GLASSComponents;
import mine.block.glass.server.GLASSPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;

public class TerminalBlockGUI extends SyncedGuiDescription {
    public static final ScreenHandlerType<TerminalBlockGUI> SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("glass", "terminal_screen"), (syncId, inventory, buf) -> new TerminalBlockGUI(syncId, inventory, buf));

    public BlockPos pos;

    private static final int WIDTH = 7;
    private static final int HEIGHT = 10;

    private WListPanel<String, WButton> channelList;
    private WButton removeChannelButton;

    public TerminalBlockGUI(int syncId, PlayerInventory playerInventory, PacketByteBuf context) {
        super(SCREEN_HANDLER_TYPE, syncId, playerInventory);

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        root.setSize(7*18*2, 6*18*2);
        root.setInsets(Insets.ROOT_PANEL);

        pos = context.readBlockPos();

        var channels = GLASSComponents.CHANNELS.get(world).getValue();

        if(channels.isEmpty()) {
            ClientPlayNetworking.send(GLASSPackets.POPULATE_DEFAULT_CHANNEL.ID, PacketByteBufs.empty());
            GLASSComponents.CHANNELS.sync(world);
        }

        GLASS.LOGGER.info("[GUI] " + GLASSComponents.CHANNELS.get(world).getValue() + " [WORLD] " + world);

        channelList = new WListPanel<>(GLASSComponents.CHANNELS.get(world).getValue(), WButton::new, (String str, WButton btn) -> {

            btn.setOnClick(() -> {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                buf.writeString(btn.getLabel().getString());

                ClientPlayNetworking.send(new Identifier("glass", "terminal_channel_changed"), buf);

                btn.setEnabled(false);
                removeChannelButton.setEnabled(true);
            });

            if(GLASSComponents.LINKED_CHANNELS.get(world).contains(str)) {
                if(Objects.requireNonNull(GLASSComponents.LINKED_CHANNELS.get(world).get(str)).getRight() == pos) {
                    btn.setEnabled(false);
                }
            }

            btn.setLabel(Text.literal(str));
        });

        channelList.setListItemHeight(18);
        root.add(channelList, 0, 1, WIDTH, HEIGHT - 1);

        removeChannelButton = new WButton();

        removeChannelButton.setOnClick(() -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            buf.writeString("");

            channelList.layout();

            ClientPlayNetworking.send(GLASSPackets.TERMINAL_CHANNEL_CHANGED.ID, buf);
            ClientPlayNetworking.send(GLASSPackets.REMOVE_LINKED_CHANNEL.ID, buf);
        });

        removeChannelButton.setLabel(Text.literal("Unlink From Channel"));

        if(!GLASSComponents.LINKED_CHANNELS.get(world).contains(pos)) {
            removeChannelButton.setEnabled(false);
        }

        removeChannelButton.setSize(8, 10);

        root.add(removeChannelButton, 0, 1);

        root.validate(this);
    }
}
