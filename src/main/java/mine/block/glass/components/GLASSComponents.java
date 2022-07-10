package mine.block.glass.components;

import dev.onyxstudios.cca.api.v3.component.ComponentFactory;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class GLASSComponents implements WorldComponentInitializer {
    public static final ComponentKey<ChannelsComponent> CHANNELS =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("glass", "channels"), ChannelsComponent.class);

    public static final ComponentKey<LinkedChannelsComponent> LINKED_CHANNELS = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("glass", "linked_channels"), LinkedChannelsComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(CHANNELS, ChannelsComponent::new);
        registry.register(LINKED_CHANNELS, LinkedChannelsComponent::new);
    }
}
