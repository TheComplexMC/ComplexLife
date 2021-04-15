package net.thecomplex.complexlife.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.thecomplex.complexlife.ServerEventSubscriber;
import net.thecomplex.complexlife.entity.EntityCopperCable;
import net.thecomplex.complexlife.misc.energy.EnergyBlockSocket;
import net.thecomplex.complexlife.misc.energy.ISocketBlock;
import org.codehaus.plexus.util.FastMap;

import java.util.Map;
import java.util.UUID;

public class ItemCopperCable extends Item {
    public static final Map<UUID, EnergySocketInfo> activeCopperCableHolders = new FastMap<>();

    public ItemCopperCable(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();
        Block block = world.getBlockState(blockpos).getBlock();
        Vector3d location = context.getClickLocation();
        Direction direction = context.getClickedFace();
        if(!(block instanceof ISocketBlock)) return ActionResultType.PASS;
        ISocketBlock socketBlock = (ISocketBlock) block;
        EnergyBlockSocket socket = socketBlock.getSocket(world.getBlockState(blockpos));
        if(socket.getSide() != direction) return ActionResultType.PASS;
        if(socket.isOccupied()) return ActionResultType.PASS;

        if (!world.isClientSide && player != null) {
            socket.connect(setOrCreateCopperCable(world, socket.getWorldPos(blockpos), direction, player, blockpos, socket.getId()));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if(entity instanceof PlayerEntity) {
            if(((PlayerEntity)entity).getMainHandItem().getItem() != this) {
                if(activeCopperCableHolders.containsKey((entity.getUUID()))) {
                    activeCopperCableHolders.get(entity.getUUID()).cable.remove();
                    activeCopperCableHolders.remove(entity.getUUID());
                }
            }
        }
        super.inventoryTick(p_77663_1_, p_77663_2_, entity, p_77663_4_, p_77663_5_);
    }

    private EntityCopperCable setOrCreateCopperCable(World world, Vector3d location, Direction direction, PlayerEntity player, BlockPos blockPos, int socketId) {
        if(activeCopperCableHolders.containsKey(player.getUUID())) {
            EnergySocketInfo info = activeCopperCableHolders.get(player.getUUID());
            EntityCopperCable cableStart = info.cable;
            EntityCopperCable cableEnd = EntityCopperCable.create(world, blockPos, player, location, direction, true);
            cableStart.setHolder(cableEnd);
            cableEnd.setHolder(cableStart);
            ServerEventSubscriber.executeConnect(info.block, info.socketAId, blockPos, socketId);
            activeCopperCableHolders.remove(player.getUUID());
            return cableEnd;
        }
        else
        {
            EntityCopperCable cable = EntityCopperCable.create(world, blockPos, player, location, direction, false);
            EnergySocketInfo info = new EnergySocketInfo();
            info.cable = cable;
            info.block = blockPos;
            info.socketAId = socketId;
            activeCopperCableHolders.put(player.getUUID(), info);
            return cable;
        }
    }

    public class EnergySocketInfo {
        public EntityCopperCable cable;
        public BlockPos block;
        public int socketAId;
    }
}
