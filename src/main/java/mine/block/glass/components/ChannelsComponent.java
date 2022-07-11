package mine.block.glass.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ChannelsComponent implements ArrayComponent<String> {

    private final World provider;


    public ChannelsComponent(World world) {
        this.provider = world;
    }

    private ArrayList<String> _value = new ArrayList<>();

    @Override
    public ArrayList<String> getValue() {
        return _value;
    }

    @Override
    public void addValue(String value) {
        _value.add(value);
    }

    @Override
    public void setValue(ArrayList<String> value) {
        _value = value;
    }

    @Override
    public void removeValue(String value) {
        _value.remove(value);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        System.out.println("Reading Component (Channels) - " + tag.asString());
        NbtList list = tag.getList("channels", NbtElement.STRING_TYPE);
        for (int i = 0; i < list.size(); i++) {
            _value.add(list.getString(i));
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        System.out.println("Begin Write Component (Channels) - val = " + _value.toString());
        NbtList list = new NbtList();
        for (String s : _value) {
            list.add(NbtString.of(s));
        }
        tag.put("channels", list);
        System.out.println("Writing Component (Channels) - " + tag.asString());
    }
}
