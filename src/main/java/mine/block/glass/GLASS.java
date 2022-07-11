package mine.block.glass;

import mine.block.glass.blocks.GLASSBlocks;
import mine.block.glass.items.GLASSItems;
import mine.block.glass.persistence.ChannelManagerPersistence;
import mine.block.glass.server.GLASSPackets;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        ChannelManagerPersistence.init();
    }
}
