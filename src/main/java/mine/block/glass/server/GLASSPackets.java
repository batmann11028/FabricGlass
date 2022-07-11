package mine.block.glass.server;

import mine.block.glass.GLASS;
import mine.block.glass.blocks.entity.TerminalBlockEntity;
import mine.block.glass.persistence.Channel;
import mine.block.glass.persistence.ChannelManagerPersistence;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;

public enum GLASSPackets {
    TERMINAL_CHANNEL_CHANGED(null, GLASSPackets::onTerminalChannelChanged, null),
    REMOVE_LINKED_CHANNEL(null, GLASSPackets::onRemoveLinkedChannel, null),
    POPULATE_DEFAULT_CHANNEL(null, GLASSPackets::onPopulateDefaultChannel, null)

    ;

    public final Identifier ID;
    private final EnvType env;
    private final ServerPlayNetworking.PlayChannelHandler serverAction;
    private final ClientPlayNetworking.PlayChannelHandler clientAction;

    GLASSPackets(@Nullable EnvType envType, @Nullable ServerPlayNetworking.PlayChannelHandler serverAction, @Nullable ClientPlayNetworking.PlayChannelHandler clientAction) {
        this.ID = new Identifier("glass", this.name().toLowerCase());
        this.env = envType;
        this.serverAction = serverAction;
        this.clientAction = clientAction;
    }

    public void register() {
        if(this.env == null) {
            // Both SERVER + CLIENT

            if(serverAction != null) {
                ServerPlayNetworking.registerGlobalReceiver(ID, serverAction);
            }

            if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && clientAction != null) {
                ClientPlayNetworking.registerGlobalReceiver(ID, clientAction);
            }
        } else if (this.env == EnvType.CLIENT && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && clientAction != null) {
            ClientPlayNetworking.registerGlobalReceiver(ID, clientAction);
        } else if (this.env == EnvType.SERVER && FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER && serverAction != null) {
            ServerPlayNetworking.registerGlobalReceiver(ID, serverAction);
        }

        GLASS.Log("Registered Packet: " + this.ID);
    }


    private static void onTerminalChannelChanged(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        String channel = buf.readString();

        server.executeSync(() -> {
            var entity = player.getWorld().getBlockEntity(pos);

            if(entity instanceof TerminalBlockEntity terminal) {
                terminal.channel = channel;
                terminal.markDirty();

                var channelManager = ChannelManagerPersistence.MANAGERS.get(player.getWorld());

                channelManager.removeIf(channels -> channels.linkedBlock() == pos);

                channelManager.add(new Channel(channel, pos));
            }
        });
    }

    private static void onRemoveLinkedChannel(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();

        server.executeSync(() -> {
            var channelManager = ChannelManagerPersistence.MANAGERS.get(player.getWorld());

            var entity = player.getWorld().getBlockEntity(pos);

            String cachedChannel = "";

            if(entity instanceof TerminalBlockEntity terminal) {
                cachedChannel = terminal.channel;
                terminal.channel = "";
                terminal.markDirty();
            }

            channelManager.removeIf(channels -> channels.linkedBlock() == pos);
            channelManager.add(new Channel(cachedChannel, null));

        });
    }

    private static void onPopulateDefaultChannel(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var channelManager = ChannelManagerPersistence.MANAGERS.get(player.getWorld());

        if(!channelManager.stream().filter(channel -> Objects.equals(channel.name(), "Default")).toList().isEmpty()) return;

        channelManager.add(new Channel("Default", null));
    }
}
