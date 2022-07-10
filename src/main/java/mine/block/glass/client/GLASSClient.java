package mine.block.glass.client;

import mine.block.glass.blocks.GLASSBlocks;
import mine.block.glass.blocks.screens.TerminalBlockScreen;
import mine.block.glass.client.gui.TerminalBlockGUI;
import mine.block.glass.items.GLASSItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class GLASSClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GLASSBlocks.initClient();
        GLASSItems.initClient();

        ScreenRegistry.<TerminalBlockGUI, TerminalBlockScreen>register(TerminalBlockGUI.SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new TerminalBlockScreen(gui, inventory.player, title));
    }
}
