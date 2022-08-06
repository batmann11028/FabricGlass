package mine.block.glass.items;

import mine.block.glass.blocks.GLASSBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GLASSItems {
    public static final BlockItem TERMINAL = new BlockItem(GLASSBlocks.TERMINAL, new FabricItemSettings().group(ItemGroup.REDSTONE));
    public static final BlockItem PROJECTOR = new BlockItem(GLASSBlocks.PROJECTOR, new FabricItemSettings().group(ItemGroup.REDSTONE));
    public static final BlockItem PROJECTION_PANEL = new BlockItem(GLASSBlocks.PROJECTION_PANEL, new FabricItemSettings().group(ItemGroup.REDSTONE));

    public static void init() {
        register("terminal", TERMINAL);
        register("projector", PROJECTOR);
        register("projection_panel", PROJECTION_PANEL);
    }

    public static void initClient() {

    }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registry.ITEM, new Identifier("glass", id), item);
    }
}
