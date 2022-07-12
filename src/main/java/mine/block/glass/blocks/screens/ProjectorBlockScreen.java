package mine.block.glass.blocks.screens;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import mine.block.glass.client.gui.ProjectorBlockGUI;
import mine.block.glass.client.gui.TerminalBlockGUI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ProjectorBlockScreen extends CottonInventoryScreen<ProjectorBlockGUI> {
    public ProjectorBlockScreen(ProjectorBlockGUI gui, PlayerEntity player, Text title) {
        super(gui, player, title);
    }
}
