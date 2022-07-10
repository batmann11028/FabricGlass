package mine.block.glass.blocks;

import mine.block.glass.blocks.entity.TerminalBlockEntity;
import mine.block.glass.client.render.TerminalBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GLASSBlocks {
    public static final TerminalBlock TERMINAL = new TerminalBlock(AbstractBlock.Settings.copy(Blocks.OBSIDIAN));

    public static void init() {
        register("terminal", TERMINAL);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("glass", "terminal_entity"), TerminalBlockEntity.BLOCK_ENTITY_TYPE);
    }

    public static void initClient() {
        BlockEntityRendererRegistry.register(TerminalBlockEntity.BLOCK_ENTITY_TYPE, TerminalBlockEntityRenderer::new);
    }

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(Registry.BLOCK, new Identifier("glass", id), block);
    }
}
