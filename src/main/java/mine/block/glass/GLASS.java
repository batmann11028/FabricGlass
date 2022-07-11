package mine.block.glass;

import mine.block.glass.blocks.GLASSBlocks;
import mine.block.glass.blocks.TerminalBlock;
import mine.block.glass.blocks.entity.TerminalBlockEntity;
import mine.block.glass.components.GLASSComponents;
import mine.block.glass.components.LinkedChannelsComponent;
import mine.block.glass.items.GLASSItems;
import mine.block.glass.server.GLASSPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class GLASS implements ModInitializer {

    public static Logger LOGGER = LoggerFactory.getLogger(GLASS.class);

    public static void Log(Object contents) {
        LOGGER.info(contents.toString());
    }

    @Override
    public void onInitialize() {
        GLASSBlocks.init();
        GLASSItems.init();

        for (GLASSPackets packet : GLASSPackets.values()) {
            packet.register();
        }
    }
}
