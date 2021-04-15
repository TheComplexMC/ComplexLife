package net.thecomplex.complexlife.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.thecomplex.complexlife.ServerEventSubscriber;
import net.thecomplex.complexlife.item.ItemCopperCable;
import net.thecomplex.complexlife.item.ItemManager;
import net.thecomplex.complexlife.misc.energy.ISocketBlock;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

// TODO: Create a general cable entity
public class EntityCopperCable extends Entity implements IEntityAdditionalSpawnData {
    private BlockPos blockPos;
    private Entity holder;
    private int holderId;
    private Direction direction;
    private boolean isHolder;

    public static final DataParameter<Integer> HOLDER = EntityDataManager.defineId(EntityCopperCable.class, DataSerializers.INT);

    public EntityCopperCable(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    public EntityCopperCable(World world, BlockPos blockPos, Entity player, Vector3d location, Direction direction, boolean isHolder) {
        super(EntityTypeManager.COPPER_CABLE, world);
        this.blockPos = blockPos;
        this.holder = player;
        if(this.holder != null)
            this.holderId = player.getId();
        this.direction = direction;
        this.isHolder = isHolder;
        this.setPos(location.x, location.y, location.z);
        this.setBoundingBox(new AxisAlignedBB(location.x - 0.5D, location.y - 0.5D, location.z - 0.5D, location.x + 0.5D, location.y + 0.5D, location.z + 0.5D));
        this.forcedLoading = true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void thunderHit(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
        super.thunderHit(p_241841_1_, p_241841_2_);
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        disconnect(player);
        return ActionResultType.CONSUME;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float power) {
        disconnect(damageSource.getEntity());
        return true;
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack p_70099_1_, float p_70099_2_) {
        ItemEntity itementity = new ItemEntity(this.level, this.getX() + (double)((float)this.direction.getStepX() * 0.15F), this.getY() + (double)p_70099_2_, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15F), p_70099_1_);
        itementity.setDefaultPickUpDelay();
        this.level.addFreshEntity(itementity);
        return itementity;
    }

    protected void dropItem(Entity entity) {
        this.spawnAtLocation(new ItemStack(ItemManager.COPPER_CABLE));
        if(this.holder instanceof EntityCopperCable) {
            this.holder.remove();
        }
    }

    public void disconnect(Entity entity) {
        if(level.isClientSide) return;
        if(!(this.holder instanceof EntityCopperCable)) {
            BlockState state = level.getBlockState(blockPos);
            ((ISocketBlock)state.getBlock()).getSocket(state).disconnect();
            // TODO: Player that performs disconnect must not be Player holding the cable
            ItemCopperCable.activeCopperCableHolders.remove(entity.getUUID());
            this.remove();
            return;
        }
        EntityCopperCable holderPlug = (EntityCopperCable)holder;
        BlockPos posA;
        BlockPos posB;
        if(isHolder) {
            posB = blockPos;
            posA = holderPlug.blockPos;
        }
        else {
            posA = blockPos;
            posB = holderPlug.blockPos;
        }
        BlockState stateA = level.getBlockState(posA);
        ((ISocketBlock)stateA.getBlock()).getSocket(stateA).disconnect();
        BlockState stateB = level.getBlockState(posB);
        ((ISocketBlock)stateB.getBlock()).getSocket(stateB).disconnect();
        ServerEventSubscriber.executeDisconnect(posA, posB);
        dropItem(entity);
        this.remove();
    }

    protected void playPlacementSound() {
        // TODO Custom Sound
        this.playSound(SoundEvents.STONE_PLACE, 1.0f, 1.0f);
    }

    public static EntityCopperCable create(World world, BlockPos blockPos, Entity player, Vector3d location, Direction direction, boolean isHolder) {
        EntityCopperCable entity = new EntityCopperCable(world, blockPos, player, location, direction, isHolder);
        world.addFreshEntity(entity);
        entity.playPlacementSound();
        return entity;
    }

    @Override
    public void tick() {
        if(holder == null && holderId > 0) {
            Entity e = this.level.getEntity(holderId);
            if(e != null)
                this.holder = e;
        }
        super.tick();
    }

    public Entity getHolder() {
        return this.holder;
    }

    public void setHolder(Entity holder) {
        this.holder = holder;
        this.holderId = holder.getId();
        this.getEntityData().set(HOLDER, holderId);
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean IsHolder() {
        return this.isHolder;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(HOLDER, holderId);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> parameter) {
        boolean isClient = level.isClientSide;
        if(HOLDER.equals(parameter)) {
            this.holderId = this.getEntityData().get(HOLDER);
            Entity e = this.level.getEntity(holderId);
            if(e != null)
                this.holder = e;
        }
        super.onSyncedDataUpdated(parameter);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUUID("holder", holder.getUUID());
        nbt.putInt("direction", direction.get3DDataValue());
        nbt.putBoolean("isHolder", isHolder);
        nbt.putInt("holderId", holderId);
        buffer.writeNbt(nbt);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        CompoundNBT nbt = additionalData.readNbt();
        UUID playerID = nbt.getUUID("holder");
        direction = Direction.from3DDataValue(nbt.getInt("direction"));
        holder = this.level.getPlayerByUUID(playerID);
        isHolder = nbt.getBoolean("isHolder");
        holderId = nbt.getInt("holderId");
    }
}
