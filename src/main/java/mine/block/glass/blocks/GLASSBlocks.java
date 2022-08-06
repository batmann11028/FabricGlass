package mine.block.glass.blocks;

import mine.block.glass.blocks.entity.ProjectionBlockBase;
import mine.block.glass.blocks.entity.ProjectorBlockEntity;
import mine.block.glass.blocks.entity.TerminalBlockEntity;
import mine.block.glass.client.render.ProjectorBlockEntityRenderer;
import mine.block.glass.client.render.TerminalBlockEntityRenderer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GLASSBlocks {
    public static final TerminalBlock TERMINAL = new TerminalBlock(AbstractBlock.Settings.copy(Blocks.OBSIDIAN));
    public static final ProjectorBlock PROJECTOR = new ProjectorBlock(AbstractBlock.Settings.copy(Blocks.BEACON));
    public static final ProjectionBlock PROJECTION_PANEL = new ProjectionBlock(AbstractBlock.Settings.copy(Blocks.GLASS));

    public static void init() {
        register("terminal", TERMINAL);
        register("projector", PROJECTOR);
        register("projection_panel", PROJECTION_PANEL);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("glass", "terminal_entity"), TerminalBlockEntity.BLOCK_ENTITY_TYPE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("glass", "projector_entity"), ProjectorBlockEntity.BLOCK_ENTITY_TYPE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("glass", "projection_entity"), ProjectionBlockBase.BLOCK_ENTITY_TYPE);
    }

    public static void initClient() {
        BlockEntityRendererRegistry.register(TerminalBlockEntity.BLOCK_ENTITY_TYPE, TerminalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ProjectorBlockEntity.BLOCK_ENTITY_TYPE, ProjectorBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(PROJECTOR, RenderLayer.getCutout());
    }

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(Registry.BLOCK, new Identifier("glass", id), block);
    }
}
