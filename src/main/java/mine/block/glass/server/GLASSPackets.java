package mine.block.glass.server;

import mine.block.glass.GLASS;
import mine.block.glass.blocks.entity.TerminalBlockEntity;
import mine.block.glass.components.GLASSComponents;
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
import net.minecraft.util.Pair;
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

                var arr = GLASSComponents.LINKED_CHANNELS.get(Objects.requireNonNull(entity.getWorld())).getValue();

                int index = -1;
                for (Pair<String, BlockPos> pair : arr) {
                    if(pair.getRight() == pos) {
                        index = arr.indexOf(pair);
                    }
                }

                if(index != -1) {
                    arr.remove(index);
                }



                arr.add(new Pair<>(channel, pos));

                GLASSComponents.LINKED_CHANNELS.get(entity.getWorld()).setValue(arr);

                GLASSComponents.LINKED_CHANNELS.sync(Objects.requireNonNull(entity.getWorld()));
            }
        });
    }

    private static void onRemoveLinkedChannel(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        BlockPos pos = packetByteBuf.readBlockPos();

        minecraftServer.executeSync(() -> {
            var arr = GLASSComponents.LINKED_CHANNELS.get(Objects.requireNonNull(serverPlayerEntity.getWorld())).getValue();

            int index = -1;
            for (Pair<String, BlockPos> pair : arr) {
                if(pair.getRight() == pos) {
                    index = arr.indexOf(pair);
                }
            }

            arr.remove(index);

            GLASSComponents.LINKED_CHANNELS.get(serverPlayerEntity.getWorld()).setValue(arr);
            GLASSComponents.LINKED_CHANNELS.sync(serverPlayerEntity.getWorld());
        });
    }

    private static void onPopulateDefaultChannel(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var channels = GLASSComponents.CHANNELS.get(serverPlayerEntity.getWorld()).getValue();

        if(channels.contains("Default")) return;

        channels.add("Default");
        GLASSComponents.CHANNELS.get(serverPlayerEntity.getWorld()).setValue(channels);
        GLASSComponents.CHANNELS.sync(serverPlayerEntity.getWorld());
    }
}
