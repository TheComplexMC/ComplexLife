package net.thecomplex.complexlife.misc.energy.network;

public class NetworkEdge {
    private INetworkNode nodeA;
    private int nodeASocketId;
    private INetworkNode nodeB;
    private int nodeBSocketId;

    public NetworkEdge(INetworkNode a, int aId, INetworkNode b, int bId) {
        nodeA = a;
        nodeB = b;
        nodeASocketId = aId;
        nodeBSocketId = bId;
    }

    public INetworkNode getNodeA() {
        return nodeA;
    }

    public INetworkNode getNodeB() {
        return nodeB;
    }

    public int getNodeASocketId() {
        return nodeASocketId;
    }

    public int getNodeBSocketId() {
        return nodeBSocketId;
    }

    public void setNodeA(INetworkNode a) {
        this.nodeA = a;
    }

    public void setNodeB(INetworkNode b) {
        this.nodeB = b;
    }

    public void setNodeASocketId(int nodeASocketId) {
        this.nodeASocketId = nodeASocketId;
    }

    public void setNodeBSocketId(int nodeBSocketId) {
        this.nodeBSocketId = nodeBSocketId;
    }
}
