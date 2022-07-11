package mine.block.glass.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LinkedChannelsComponent implements ArrayComponent<Pair<String, BlockPos>> {
    private final World provider;
    public LinkedChannelsComponent(World world) {
        this.provider = world;
    }
    private ArrayList<Pair<String, BlockPos>> _value = new ArrayList<>();

    @Override
    public ArrayList<Pair<String, BlockPos>> getValue() {
        return _value;
    }

    @Override
    public void addValue(Pair<String, BlockPos> value) {
        _value.add(value);
    }

    @Override
    public void setValue(ArrayList<Pair<String, BlockPos>> value) {
        _value = value;
    }

    @Override
    public void removeValue(Pair<String, BlockPos> value) {
        _value.remove(value);
    }

    public boolean contains(String key) {
        for (Pair<String, BlockPos> stringBlockPosPair : _value) {
            if(Objects.equals(stringBlockPosPair.getLeft(), key)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(BlockPos key) {
        for (Pair<String, BlockPos> stringBlockPosPair : _value) {
            if(stringBlockPosPair.getRight() == key) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        System.out.println("Reading Component (LinkedChannels) - " + tag.asString());
        NbtList list = tag.getList("channels", NbtElement.STRING_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound compound = list.getCompound(i);
            int[] right = compound.getIntArray("right");
            _value.add(new Pair<>(compound.getString("left"), new BlockPos(right[0], right[1], right[2])));
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        System.out.println("Begin Write Component (LinkedChannels) - val = " + _value.toString());
        NbtList list = new NbtList();
        for (Pair<String, BlockPos> s : _value) {
            NbtCompound compound = new NbtCompound();
            compound.putString("left", s.getLeft());
            compound.putIntArray("right", new int[] {s.getRight().getX(), s.getRight().getY(), s.getRight().getZ()});
            list.add(compound);
        }
        tag.put("channels", list);
        System.out.println("Writing Component (LinkedChannels) - " + tag.asString());
    }

    public void remove(BlockPos pos) {
        int index = -1;
        for (Pair<String, BlockPos> pair : _value) {
            if(pair.getRight() == pos) {
                index = _value.indexOf(pair);
            }
        }
        if(index == -1) return;
        _value.remove(index);
    }

    public void remove(String pos) {
        int index = -1;
        for (Pair<String, BlockPos> pair : _value) {
            if(Objects.equals(pair.getLeft(), pos)) {
                index = _value.indexOf(pair);
            }
        }
        if(index == -1) return;
        _value.remove(index);
    }

    @Nullable
    public Pair<String, BlockPos> get(String str) {
        for (Pair<String, BlockPos> pair : _value) {
            if(Objects.equals(pair.getLeft(), str)) {
                return pair;
            }
        }
        return null;
    }

    @Nullable
    public Pair<String, BlockPos> get(BlockPos pos) {
        for (Pair<String, BlockPos> pair : _value) {
            if(pair.getRight() == pos) {
                return pair;
            }
        }
        return null;
    }
}
