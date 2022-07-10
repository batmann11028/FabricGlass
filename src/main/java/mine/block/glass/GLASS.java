package mine.block.glass;

import mine.block.glass.blocks.GLASSBlocks;
import mine.block.glass.blocks.TerminalBlock;
import mine.block.glass.blocks.entity.TerminalBlockEntity;
import mine.block.glass.items.GLASSItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class GLASS implements ModInitializer {

    @Override
    public void onInitialize() {
        GLASSBlocks.init();
        GLASSItems.init();

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("glass", "terminal_channel_changed"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String channel = buf.readString();

            server.executeSync(() -> {
                var entity = player.getWorld().getBlockEntity(pos);

                System.out.println(entity);

                if(entity instanceof TerminalBlockEntity terminal) {
                    terminal.channel = channel;
                    terminal.markDirty();
                }
            });
        });
    }
}
