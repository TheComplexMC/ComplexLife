package net.thecomplex.complexlife.misc.energy;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.thecomplex.complexlife.entity.EntityCopperCable;

public class EnergyBlockSocket {
    private int id;
    private Direction side;
    private int posX;
    private int posY;
    private EntityCopperCable socketConnector;

    public EnergyBlockSocket(int id, Direction side, int posX, int posY) {
        this.id = id;
        this.side = side;
        this.posX = posX;
        this.posY = posY;
        this.socketConnector = null;
    }

    public Direction getSide() {
        return side;
    }

    public void setSide(Direction side) {
        this.side = side;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getId() {
        return id;
    }

    public boolean connect(EntityCopperCable connector) {
        if(socketConnector != null)
            return false;

        socketConnector = connector;
        return true;
    }

    public boolean disconnect() {
        if(socketConnector == null)
            return false;

        socketConnector = null;
        return true;
    }

    public EntityCopperCable getSocketConnector() {
        return socketConnector;
    }

    public boolean isOccupied() {
        return socketConnector != null;
    }

    public Vector3d getWorldPos(BlockPos pos) {
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;
        switch (side) {
            case UP:
                x = pos.getX() + posX / 16F;
                y = pos.getY() + 1;
                z = pos.getZ() + posY / 16F;
                break;
            case DOWN:
                x = pos.getX() + posX / 16F;
                y = pos.getY();
                z = pos.getZ() + posY / 16F;
                break;
            case NORTH:
                x = pos.getX() + posX / 16F;
                y = pos.getY() + posY / 16F;
                z = pos.getZ();
                break;
            case SOUTH:
                x = pos.getX() + posX / 16F;
                y = pos.getY() + posY / 16F;
                z = pos.getZ() + 1;
                break;
            case EAST:
                x = pos.getX() + 1;
                y = pos.getY() + posY / 16F;
                z = pos.getZ() + posX / 16F;
                break;
            case WEST:
                x = pos.getX();
                y = pos.getY() + posY / 16F;
                z = pos.getZ() + posX / 16F;
                break;
        }

        return new Vector3d(x, y, z);
    }
}
