package mine.block.glass.blocks.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class ProjectionBlockBase extends BlockEntity {

    public static final HashMap<Direction, ArrayList<Direction>> PROPAGATION_FACES = new HashMap<>();
    static
    {
        for(Direction face : Direction.values())
        {
            ArrayList<Direction> faces = PROPAGATION_FACES.computeIfAbsent(face, v -> new ArrayList<>());
            for(Direction face1 : Direction.values())
            {
                if(!face1.getAxis().equals(face.getAxis()))
                {
                    faces.add(face1);
                }
            }
        }
    }
    
    public static int FADEOUT_TIME = 12;
    public static int PROPAGATE_TIME = 2;

    public ArrayList<Direction> activeFaces = new ArrayList<>();
    public String channel = "";
    public int fadeoutTime = 12;
    public int distance = 0; //distance = 0 also means off
    public int propagateTime = 2;

    public int fadePropagate;
    public int fadeDistance;

    public int lastDraw;

    public boolean active = false;

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.putString("channel", channel);
        tag.putInt("distance", distance);
        tag.putInt("fadePropagate", fadePropagate);
        tag.putInt("fadeDistance", fadeDistance);
        tag.putInt("lastDraw", lastDraw);
        tag.putBoolean("active", active);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        channel = tag.getString("channel");
        fadeoutTime = FADEOUT_TIME;
        propagateTime = PROPAGATE_TIME;
        fadePropagate = tag.getInt("fadePropagate");
        fadeDistance = tag.getInt("fadeDistance");
        lastDraw = tag.getInt("lastDraw");
        active = tag.getBoolean("active");
    }


    public ProjectionBlockBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if(fadeoutTime > 0)
        {
            fadeoutTime--;
            if(fadeoutTime == 0)
            {
                if(!active)
                {
                    activeFaces.clear();
                }
                if(fadeDistance > 0)
                {
                    fadeDistance = 0;
                }
            }
        }
        if(propagateTime > 0)
        {
            propagateTime--;
            if(Objects.requireNonNull(world).isClient && propagateTime == 0)
            {
                propagate();
            }
        }
        if(fadePropagate > 0)
        {
            fadePropagate--;
            if(!Objects.requireNonNull(world).isClient && fadePropagate == 0)
            {
                fadePropagate();
            }
        }
        if(lastDraw > 0)
        {
            lastDraw--;
        }
    }

    public void propagate() //do I need to send active state, channel, online/offline, block change/init propagation?
    {
        if(!canPropagate())
        {
            return;
        }
        HashSet<Direction> propagationFaces = new HashSet<>();
        for(Direction facing : activeFaces)
        {
            propagationFaces.addAll(PROPAGATION_FACES.get(facing));
        }
        for(Direction facing : propagationFaces)
        {
            BlockPos pos = this.getPos().offset(facing);
            BlockEntity te = Objects.requireNonNull(getWorld()).getBlockEntity(pos);
            if(te instanceof ProjectionBlockBase base)
            {
                base.bePropagatedTo(this, channel, active);
            }
        }
        if(!active)
        {
            channel = "";
            distance = 0;
            BlockState state = Objects.requireNonNull(getWorld()).getBlockState(getPos());
            getWorld().updateListeners(getPos(), state, state, 3);
        }
    }

    public void bePropagatedTo(ProjectionBlockBase base, String newChannel, boolean activate)
    {
        boolean flag = false;
        if(active && activate && channel.equalsIgnoreCase(newChannel)) //same channel and both activated but this is further than the other from master.
        {
            if(distance > base.distance + 1)
            {
                distance = base.distance + 1;
                checkFacesToTurnOn(base);
                flag = true;
            }
        }
        if(activate && !active && (distance > base.distance || distance == 0)) //turn on
        {
            active = true;
            channel = newChannel;
            distance = base.distance + 1;
            checkFacesToTurnOn(base);
            flag = true;
        }
        if(!activate && active && channel.equalsIgnoreCase(newChannel)) //turn off
        {
            if(distance > base.distance || base == this)
            {
                active = false;
                flag = true;
            }
            else
            {
                propagateTime = ProjectionBlockBase.PROPAGATE_TIME + 1;
                BlockState state = Objects.requireNonNull(getWorld()).getBlockState(getPos());
                getWorld().updateListeners(getPos(), state, state, 3);
            }
            //do not set channel or distance as we're still propagating
        }
        if(flag)
        {
            fadeoutTime = ProjectionBlockBase.FADEOUT_TIME;
            propagateTime = ProjectionBlockBase.PROPAGATE_TIME;
            BlockState state = Objects.requireNonNull(getWorld()).getBlockState(getPos());
            getWorld().updateListeners(getPos(), state, state, 3);
        }
    }

    public void checkFacesToTurnOn(ProjectionBlockBase origin)
    {
        if(origin != this)
        {
            activeFaces.clear();
            activeFaces.addAll(origin.activeFaces); //check origin location and remove that active face.
            if(activeFaces.size() > 1)
            {
                for(int i = activeFaces.size() - 1; i >= 0; i--)
                {
                    Direction facing = activeFaces.get(i);
                    BlockPos facePos = getPos().offset(facing, -1);
                    BlockEntity te = Objects.requireNonNull(getWorld()).getBlockEntity(facePos);
                    if(te instanceof ProjectionBlockBase && ((ProjectionBlockBase)te).active && ((ProjectionBlockBase)te).channel.equalsIgnoreCase(channel) && ((ProjectionBlockBase)te).distance < distance)
                    {
                        activeFaces.remove(i);
                        continue;
                    }
                    facePos = getPos().offset(facing);
                    te = getWorld().getBlockEntity(facePos);
                    if(te instanceof ProjectionBlockBase && ((ProjectionBlockBase)te).active && ((ProjectionBlockBase)te).channel.equalsIgnoreCase(channel) && ((ProjectionBlockBase)te).distance < distance)
                    {
                        activeFaces.remove(i);
                    }
                }
            }

            HashSet<Direction> newFaces = new HashSet<>();
            for(Direction facing : activeFaces)
            {
                BlockPos facePos = getPos().offset(facing);
                BlockEntity te = Objects.requireNonNull(getWorld()).getBlockEntity(facePos);
                if(te instanceof ProjectionBlockBase) //inner corner
                {
                    BlockPos originPos = origin.getPos().offset(facing);
                    Direction newFace = Direction.getFacing(originPos.getX() - facePos.getX(), originPos.getY() - facePos.getY(), originPos.getZ() - facePos.getZ());
                    newFaces.add(newFace);
                }
                else //outer corner
                {
                    facePos = getPos().offset(facing, -1);
                    te = getWorld().getBlockEntity(facePos);
                    if(te instanceof ProjectionBlockBase)
                    {
                        if(origin.getPos().getY() != getPos().getY())
                        {
                            // maybe the origin is from below but we prefer horizontals
                            for(Direction newFacing : PROPAGATION_FACES.get(facing))
                            {
                                if(newFacing.getAxis() != Direction.Axis.Y)
                                {
                                    BlockPos newPos = getPos().offset(newFacing);
                                    BlockEntity te1 = getWorld().getBlockEntity(newPos);
                                    if(te1 instanceof ProjectionBlockBase && ((ProjectionBlockBase)te1).activeFaces.contains(facing))
                                    {
                                        origin = ((ProjectionBlockBase)te1);
                                    }
                                }
                            }
                        }
                        BlockPos originPos = origin.getPos().offset(facing, -1);
                        Direction newFace = Direction.getFacing(facePos.getX() - originPos.getX(), facePos.getY() - originPos.getY(), facePos.getZ() - originPos.getZ());
                        newFaces.add(newFace);
                    }
                }
            }
            activeFaces.addAll(newFaces);

            BlockState state = Objects.requireNonNull(getWorld()).getBlockState(getPos());
            getWorld().updateListeners(getPos(), state, state, 3);
        }
    }

    public void fadePropagate()
    {
        if(fadeDistance <= 0 || !active)
        {
            return;
        }
        HashSet<Direction> propagationFaces = new HashSet<>();
        for(Direction facing : activeFaces)
        {
            propagationFaces.addAll(PROPAGATION_FACES.get(facing));
        }
        for(Direction facing : propagationFaces)
        {
            BlockPos pos = this.getPos().offset(facing);
            BlockEntity te = Objects.requireNonNull(getWorld()).getBlockEntity(pos);
            if(te instanceof ProjectionBlockBase base)
            {
                if(base.active && base.channel.equalsIgnoreCase(channel) && base.fadeDistance <= fadeDistance)
                {
                    base.fadeoutTime = FADEOUT_TIME;
                    base.fadePropagate = PROPAGATE_TIME;
                    base.fadeDistance = fadeDistance - 1;
                }
            }
        }
    }

    public boolean canPropagate()
    {
        return distance < 40;
    }


}
