package mine.block.glass.persistence;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public record Channel(String name, @Nullable BlockPos linkedBlock) {
    @Override
    public String toString() {
        return "{Name:" + name + ",LinkedBlock" + linkedBlock + "}";
    }
}
