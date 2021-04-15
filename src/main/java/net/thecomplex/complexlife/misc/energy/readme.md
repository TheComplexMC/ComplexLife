# The RedstoneWatt Energy System
The RedstoneWatt Energy System is a new Energy-System for Minecraft based on the properties of electricity in
the real world. In the current state this system is extremely simplified and contains only the very base.

The energy is measured in Watt:
* Production in produced Watt per tick (W/T) or Watt per kilotick (W/kT).
* Consumption in consumed Watt per tick (W/T) or Watt per kilotick (W/kT).

## The Goal
The goal is to be able to create large networks of electrical components. The individual
components can have not only one connection to a network, but any number (with an upper limit).
It should also be possible to control the flow of energy through components, so that energy can only flow in one direction or can be throttled and/or measured in sub-networks.

The advantage of this energy system is that the calculation of energy for individual nodes in the
network is not done by the existing entities in Minecraft, but by a superordinate
instance, which the entities can access and request their data. This system offers
in contrast to systems based on entities, a communication between the components in real time.
(e.g. the energy of an energy producer arrives quasi immediately at the consumer).


## Current Version
The higher-level energy system already exists (the EnergyNetworkManager). The energy network is currently
represented as an undirected graph, with the nodes as network components ( producers and consumers) and
the edges as undirected connections between the components. Due to the fact that it is an undirected
graph, flow control components (e.g., switches) cannot be implemented.

The current structure of the network allows a component in the network to be connected to any number of other components. You can add and remove components to the network in the form of power producers and consumers
and create or break connections between them. The generated energy of an energy producer
component is available to all components that are in the same network as the producer.
Likewise, energy consumer components can take the energy they need from a network.

The system is complex in itself but can be easily addressed from the outside and extended with new components.

Translated with www.DeepL.com/Translator (free version)

