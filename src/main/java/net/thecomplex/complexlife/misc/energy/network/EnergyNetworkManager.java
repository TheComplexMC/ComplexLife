package net.thecomplex.complexlife.misc.energy.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import org.codehaus.plexus.util.FastMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/***
 * The class represents an energy network as an undirected graph and interconnected subnetworks as indexed subgraphs.
 * Nodes represent the components of the energy network and edges the connections between them.
 * New components can be added to the network in the form of energy producers and energy consumers and existing ones
 * can be removed along with their connections.
 * Energy consumers can report their electricity consumption and energy producers can report the amount of energy they
 * produce to their sub-network. The total capacity of the network is always known to all participants of the sub-network.
 */
public class EnergyNetworkManager {

    // Represents the components of a network.
    // The components are indexed by the BlockPos of their block.
    private Map<BlockPos, INetworkNode> nodes;

    // Represents the List of connections between two components.
    private List<NetworkEdge> edges;

    // Contains all vacated network ids
    private Stack<Integer> freeNetworkIds;

    // Contains a list of the information of all interconnected sub-networks
    private SortedMap<Integer, NetworkInfo> networkInfo;

    // Contains the highest free network id
    private int nextNetworkId;

    // Indicates whether the energy network must be updated due to changes
    // in the amount of energy of individual components.
    private boolean dirty;

    /***
     * Creates a new Instance of an energy network.
     */
    public EnergyNetworkManager() {
        nodes = new HashMap<>();
        edges = new ArrayList<>();
        freeNetworkIds = new Stack<>();
        networkInfo = new TreeMap<>();
        nextNetworkId = 0;
        dirty = false;
    }

    /***
     * Saves the energy network into the passed DataOutput object.
     * Called when the world is saved.
     * @param data The DataOutput object into which the energy network is to be written.
     * @throws IOException If an I/O error occurs.
     */
    public void write(DataOutput data) throws IOException {
        updateNetworks();

        data.writeInt(nextNetworkId);
        data.writeInt(freeNetworkIds.size());
        for(int id : freeNetworkIds) {
            data.writeInt(id);
        }

        data.writeInt(networkInfo.size());
        for(Map.Entry<Integer, NetworkInfo> entry : networkInfo.entrySet()) {
            data.writeInt(entry.getKey());
            NetworkInfo info = entry.getValue();
            data.writeInt(info.getComponentCount());
            data.writeInt(info.getProducerCount());
            data.writeDouble(info.getConsumingAmount());
            data.writeDouble(info.getProducingCapacity());
        }

        data.writeInt(nodes.size());
        for(Map.Entry<BlockPos, INetworkNode> entry : nodes.entrySet()) {
            data.writeLong(entry.getKey().asLong());
            INetworkNode node = entry.getValue();
            data.writeByte(node instanceof ProducerNetworkNode ? 0x00 : 0x01);
            data.writeDouble(node.getEnergyAmount());
            data.writeInt(node.getNetworkId());
        }

        data.writeInt(edges.size());
        for(NetworkEdge edge : edges) {
            data.writeLong(edge.getNodeA().getBlockPos().asLong());
            data.writeLong(edge.getNodeB().getBlockPos().asLong());
            data.writeInt(edge.getNodeASocketId());
            data.writeInt(edge.getNodeBSocketId());
        }
    }

    /***
     * Loads the energy network from the passed DataInput object.
     * Called when the world is loaded.
     * @param data The DataInput object from which the energy network is to be read.
     * @throws IOException If an I/O error occurs.
     */
    public void read(DataInput data) throws IOException {
        nextNetworkId = data.readInt();

        int entryCount = data.readInt();
        freeNetworkIds = new Stack<>();
        for(int i = 0; i < entryCount; i++) {
            freeNetworkIds.push(data.readInt());
        }

        entryCount = data.readInt();
        networkInfo = new TreeMap<>();
        for(int i = 0; i < entryCount; i++) {
            int key = data.readInt();
            NetworkInfo info = new NetworkInfo();
            info.setComponentCount(data.readInt());
            info.setProducerCount(data.readInt());
            info.setConsumingAmount(data.readDouble());
            info.setProducingCapacity(data.readDouble());
            networkInfo.put(key, info);
        }

        entryCount = data.readInt();
        nodes = new HashMap<>(entryCount);
        for(int i = 0; i < entryCount; i++) {
            BlockPos pos = BlockPos.of(data.readLong());
            int type = data.readByte();
            INetworkNode node = type == 0 ? new ProducerNetworkNode() : new ConsumerNetworkNode();
            node.setEnergyAmount(data.readDouble());
            node.setNetworkId(data.readInt());
            node.setBlockPos(pos);
            nodes.put(pos, node);
        }

        entryCount = data.readInt();
        edges = new ArrayList<>(entryCount);
        for(int i = 0; i < entryCount; i++) {
            INetworkNode nodeA = nodes.get(BlockPos.of(data.readLong()));
            INetworkNode nodeB = nodes.get(BlockPos.of(data.readLong()));
            int nodeASocketId = data.readInt();
            int nodeBSocketId = data.readInt();
            NetworkEdge edge = new NetworkEdge(nodeA, nodeASocketId, nodeB, nodeBSocketId);
            edges.add(edge);
        }

        dirty = true;
        updateNetworks();
    }

    /***
     *
     * @param pos
     * @param amount
     */
    public void updateEnergyAmount(BlockPos pos, double amount) {
        if(!nodes.containsKey(pos))
            throw new IllegalArgumentException("No energy network component found");

        INetworkNode node = nodes.get(pos);
        node.setEnergyAmount(amount);
        dirty = true;
    }

    /***
     *
     * @param pos
     * @return
     */
    public double getProducerUsedCapacity(BlockPos pos) {
        if(!nodes.containsKey(pos))
            throw new IllegalArgumentException("No energy network component found");

        INetworkNode node = nodes.get(pos);
        NetworkInfo info = networkInfo.get(node.getNetworkId());
        int producerCountInNetwork = info.getProducerCount();
        double powerConsumption = info.getConsumingAmount();

        if(info.networkIsFullyOccupied() || powerConsumption / producerCountInNetwork > node.getEnergyAmount()) {
            return node.getEnergyAmount();
        }

        return powerConsumption / producerCountInNetwork;
    }

    /***
     *
     * @param pos
     * @return
     */
    public double getAvailableConsumptionAmount(BlockPos pos) {
        if(!nodes.containsKey(pos))
            throw new IllegalArgumentException("No energy network component found");

        INetworkNode node = nodes.get(pos);
        NetworkInfo info = networkInfo.get(node.getNetworkId());

        return Math.max(0, Math.min(node.getEnergyAmount() - info.energyLostPerComponent(), info.getProducingCapacity()));
    }

    /***
     *
     * @param pos
     * @return
     */
    public double getEnergyAmount(BlockPos pos) {
        INetworkNode node = nodes.get(pos);

        return node.getEnergyAmount();
    }

    /***
     *
     * @param pos
     * @return
     */
    public EnergyComponentType getComponentType(BlockPos pos) {
        if(!nodes.containsKey(pos)) return EnergyComponentType.NOTHING;

        INetworkNode node = nodes.get(pos);
        if(node instanceof ProducerNetworkNode) {
            return EnergyComponentType.PRODUCER;
        }
        else
            return EnergyComponentType.CONSUMER;
    }

    /***
     *
     * @param pos
     */
    public void addProducer(BlockPos pos) {
        if(nodes.containsKey(pos))
            throw new IllegalArgumentException("There cannot be two producers on the same place");

        INetworkNode node = new ProducerNetworkNode();
        node.setNetworkId(allocNetworkId());
        node.setBlockPos(pos);

        networkInfo.get(node.getNetworkId()).incrementComponentCount(true);

        nodes.put(pos, node);
    }

    /***
     *
     * @param pos
     * @return
     */
    public boolean removeProducer(BlockPos pos) {
        if(!nodes.containsKey(pos))
            return false;

        INetworkNode node = nodes.get(pos);

        disconnectAll(pos);

        int networkId = node.getNetworkId();

        networkInfo.get(networkId).decrementComponentCount(true);
        freeNetworkIdIfEmpty(networkId);

        nodes.remove(pos);

        dirty = true;
        return true;
    }

    /***
     *
     * @param pos
     */
    public void addConsumer(BlockPos pos) {
        if(nodes.containsKey(pos))
            throw new IllegalArgumentException("There cannot be two consumers on the same place");

        INetworkNode node = new ConsumerNetworkNode();
        node.setNetworkId(allocNetworkId());
        node.setBlockPos(pos);

        networkInfo.get(node.getNetworkId()).incrementComponentCount(false);

        nodes.put(pos, node);
    }

    /***
     *
     * @param pos
     * @return
     */
    public boolean removeConsumer(BlockPos pos) {
        if(!nodes.containsKey(pos))
            return false;

        INetworkNode node = nodes.get(pos);

        disconnectAll(pos);

        int networkId = node.getNetworkId();

        networkInfo.get(networkId).decrementComponentCount(false);

        freeNetworkIdIfEmpty(networkId);

        nodes.remove(pos);

        dirty = true;
        return true;
    }

    /***
     *
     * @param posA
     * @param socketAId
     * @param posB
     * @param socketBId
     * @return
     */
    public boolean connect(BlockPos posA, int socketAId, BlockPos posB, int socketBId) {
        if(!nodes.containsKey(posA) || !nodes.containsKey(posB))
            return false;

        INetworkNode nodeA = nodes.get(posA);
        INetworkNode nodeB = nodes.get(posB);

        // Set the networkId of the Nodes of the second network
        // to the networkId of the first one if there are not the same
        if(nodeA.getNetworkId() != nodeB.getNetworkId()) {
            int nodeANetworkId = nodeA.getNetworkId();
            int nodeBNetworkId = nodeB.getNetworkId();
            int networkBComponentCount = networkInfo.get(nodeBNetworkId).getComponentCount();
            int originalBNetworkComponentCount = networkBComponentCount;

            for(Map.Entry<BlockPos, INetworkNode> node : nodes.entrySet()) {
                if(node.getValue().getNetworkId() == nodeBNetworkId) {
                    networkBComponentCount--;
                    node.getValue().setNetworkId(nodeANetworkId);
                    if(networkBComponentCount <= 0) {
                        break;
                    }
                }
            }
            int originalANetworkComponentCount = networkInfo.get(nodeANetworkId).getComponentCount();
            networkInfo.get(nodeANetworkId).setComponentCount(originalBNetworkComponentCount + originalANetworkComponentCount);

            // Free the networkId of the second network
            freeNetworkIdIfEmpty(nodeBNetworkId);
        }

        edges.add(new NetworkEdge(nodes.get(posA), socketAId, nodes.get(posB), socketBId));

        dirty = true;
        return true;
    }

    /***
     *
     * @param pos
     * @return
     */
    public boolean disconnectAll(BlockPos pos) {
        if(!nodes.containsKey(pos))
            return false;

        INetworkNode node = nodes.get(pos);

        Optional<NetworkEdge> searchResult = edges.stream().filter((x) -> x.getNodeA() == node || x.getNodeB() == node).findAny();
        if(!searchResult.isPresent())
            return false;

        int networkId = node.getNetworkId();
        edges.removeIf((x) -> x.getNodeA() == node || x.getNodeB() == node);

        List<NetworkEdge> edgeGroup = edges.stream().filter(e -> e.getNodeA().getNetworkId() == networkId).collect(Collectors.toList());
        Map<INetworkNode, Integer> groupMap = new FastMap<>();
        nodes.values().stream().filter(e -> e.getNetworkId() == networkId).forEach(e -> groupMap.put(e, 0));
        int nextFreeGroupNr = 1;

        for(NetworkEdge edge : edgeGroup) {
            INetworkNode a = edge.getNodeA();
            INetworkNode b = edge.getNodeB();

            int mapValA = groupMap.get(a);
            int mapValB = groupMap.get(b);

            if(mapValA == 0 && mapValB == 0) {
                groupMap.replace(a, nextFreeGroupNr);
                groupMap.replace(b, nextFreeGroupNr);
                nextFreeGroupNr++;
            }
            else if(mapValA == 0 && mapValB != 0) {
                groupMap.replace(a, mapValB);
            }
            else if(mapValB == 0 && mapValA != 0) {
                groupMap.replace(b, mapValA);
            }
            else if(mapValA != mapValB) {
                for(Map.Entry<INetworkNode, Integer> entry : groupMap.entrySet()) {
                    if(entry.getValue() == mapValB)
                        entry.setValue(mapValA);
                }
            }
        }

        List<Map.Entry<INetworkNode, Integer>> singles = groupMap.entrySet().stream().filter(e -> e.getValue() == 0).collect(Collectors.toList());
        for(Map.Entry<INetworkNode, Integer> singel : singles) {
            singel.setValue(nextFreeGroupNr++);
        }

        SortedMap<Integer, Integer> managedGroups = new TreeMap<>();
        for(Map.Entry<INetworkNode, Integer> entry : groupMap.entrySet()) {
            if(!managedGroups.containsKey(entry.getValue())) {
                int newNetworkId = managedGroups.isEmpty() ? networkId : allocNetworkId();
                managedGroups.put(entry.getValue(), newNetworkId);
            }

            entry.getKey().setNetworkId(managedGroups.get(entry.getValue()));
        }

        dirty = true;

        return true;
    }

    /***
     *
     * @param posA
     * @param posB
     * @return
     */
    public boolean disconnect(BlockPos posA, BlockPos posB) {
        if(!nodes.containsKey(posA) || !nodes.containsKey(posB))
            return false;

        INetworkNode nodeA = nodes.get(posA);
        INetworkNode nodeB = nodes.get(posB);

        Optional<NetworkEdge> searchResult = edges.stream().filter((x) -> x.getNodeA() == nodeA && x.getNodeB() == nodeB).findAny();
        if(!searchResult.isPresent())
            return false;

        int networkId = nodeA.getNetworkId();

        edges.remove(searchResult.get());

        List<NetworkEdge> edgeGroup = edges.stream().filter(e -> e.getNodeA().getNetworkId() == networkId).collect(Collectors.toList());
        Map<INetworkNode, Integer> groupMap = new FastMap<>();
        nodes.values().stream().filter(e -> e.getNetworkId() == networkId).forEach(e -> groupMap.put(e, 0));
        int nextFreeGroupNr = 1;

        for(NetworkEdge edge : edgeGroup) {
            INetworkNode a = edge.getNodeA();
            INetworkNode b = edge.getNodeB();

            int mapValA = groupMap.get(a);
            int mapValB = groupMap.get(b);

            if(mapValA == 0 && mapValB == 0) {
                groupMap.replace(a, nextFreeGroupNr);
                groupMap.replace(b, nextFreeGroupNr);
                nextFreeGroupNr++;
            }
            else if(mapValA == 0 && mapValB != 0) {
                groupMap.replace(a, mapValB);
            }
            else if(mapValB == 0 && mapValA != 0) {
                groupMap.replace(b, mapValA);
            }
            else if(mapValA != mapValB) {
                for(Map.Entry<INetworkNode, Integer> entry : groupMap.entrySet()) {
                    if(entry.getValue() == mapValB)
                        entry.setValue(mapValA);
                }
            }
        }


        List<Map.Entry<INetworkNode, Integer>> singles = groupMap.entrySet().stream().filter(e -> e.getValue() == 0).collect(Collectors.toList());
        for(Map.Entry<INetworkNode, Integer> singel : singles) {
            singel.setValue(nextFreeGroupNr++);
        }

        int mapValPrimary = -1;
        int newNetworkId = allocNetworkId();
        for(Map.Entry<INetworkNode, Integer> entry : groupMap.entrySet()) {
            if(mapValPrimary == -1) {
                mapValPrimary = entry.getValue();
                continue;
            }

            if(entry.getValue() != mapValPrimary) {
                entry.getKey().setNetworkId(newNetworkId);
            }
        }

        dirty = true;

        return true;
    }

    /***
     *
     * @return
     */
    public List<NetworkEdge> getEdges() {
        return edges;
    }

    /***
     *
     * @return
     */
    public Map<BlockPos, INetworkNode> getNodes() {
        return nodes;
    }

    /***
     *
     * @return
     */
    private int allocNetworkId() {
        int networkId = -1;
        if(!freeNetworkIds.isEmpty()) {
            networkId = freeNetworkIds.pop();
        }
        else {
            networkId = nextNetworkId;
            nextNetworkId++;
        }

        networkInfo.put(networkId, new NetworkInfo());

        return networkId;
    }

    /***
     *
     * @param networkId
     */
    private void freeNetworkIdIfEmpty(int networkId) {
        int actualNetworkComponents = networkInfo.get(networkId).getComponentCount();
        if(actualNetworkComponents <= 1) {
            networkInfo.remove(networkId);
            freeNetworkIds.push(networkId);
        }
    }

    /***
     *
     */
    public void updateNetworks() {
        // TODO: Update interconnected sub networks separately
        if(dirty) {
            networkInfo.values().forEach(e -> {
                e.setProducingCapacity(0);
                e.setConsumingAmount(0);
            });


            for (INetworkNode node : nodes.values()) {
                if (node instanceof ConsumerNetworkNode) {
                    networkInfo.get(node.getNetworkId()).incrementConsumingAmount(node.getEnergyAmount());
                } else if (node instanceof ProducerNetworkNode) {
                    networkInfo.get(node.getNetworkId()).incrementProducingCapacity(node.getEnergyAmount());
                }
            }

            dirty = false;
        }
    }

    /***
     *
     */
    public static enum EnergyComponentType {
        PRODUCER,
        CONSUMER,
        NOTHING
    }
}
