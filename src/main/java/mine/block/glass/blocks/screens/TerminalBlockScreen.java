package mine.block.glass.blocks.screens;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import mine.block.glass.client.gui.TerminalBlockGUI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class TerminalBlockScreen extends CottonInventoryScreen<TerminalBlockGUI> {
    public TerminalBlockScreen(TerminalBlockGUI gui, PlayerEntity player, Text title) {
        super(gui, player, title);
    }
}
