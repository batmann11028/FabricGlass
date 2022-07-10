package mine.block.glass.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LinkedChannelsComponent implements HashMapComponent<BlockPos, String> {
    private final World provider;
    public LinkedChannelsComponent(World world) {
        this.provider = world;
    }

    private HashMap<BlockPos, String> _values = new HashMap<>();

    @Override
    public HashMap<BlockPos, String> getValue() {
        return _values;
    }

    @Override
    public void setValue(HashMap<BlockPos, String> value) {
        _values = value;
        GLASSComponents.LINKED_CHANNELS.sync(this.provider);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        _values.clear();
        for (String key : tag.getKeys()) {
            if(!key.startsWith("channel_")) continue;
            int[] coords = tag.getIntArray(key);
            _values.put(new BlockPos(coords[0], coords[1], coords[2]), key);
        }

        System.out.println("Writing Component (LinkedChannels) - " + tag.asString());
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        _values.forEach((a, b) -> {
            tag.putIntArray("channel_" + b, new int[] {a.getX(), a.getY(), a.getZ()});
        });

        System.out.println("Reading Component (LinkedChannels) - " + tag.asString());
    }
}
