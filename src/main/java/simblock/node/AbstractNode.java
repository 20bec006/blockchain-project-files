/*
 * Copyright 2019 Distributed Systems Group
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simblock.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import observable.interfaces.IBlockPropagationObserver;
import observable.interfaces.IBlockPropagationSubject;
import pojo.events.AddBlock;
import pojo.events.VisualizerEventEmitter;
import simblock.block.Block;
import simblock.node.consensus.AbstractConsensusAlgo;
import simblock.node.routing.RoutingTable;
import simblock.simulator.Network;
import simblock.simulator.TaskManager;
import simblock.simulator.Timer;
import simblock.task.AbstractMessageTask;
import simblock.task.AbstractMintingTask;
import simblock.task.BlockMessageTask;
import simblock.task.InvMessageTask;
import simblock.task.RecMessageTask;
import visualizer.VisualizerEvent;

/**
 * A class representing a node in the network.
 */
public abstract class AbstractNode extends TaskManager implements IBlockPropagationSubject, INetworkEntity, RoutingTable {

  @Getter
  @Setter
  private int numConnection;

  /**
   * The list of outbound connections.
   */
  @Getter
  private final ArrayList<AbstractNode> outbound = new ArrayList<>();

  /**
   * The list of inbound connections.
   */
  @Getter
  private final ArrayList<AbstractNode> inbound = new ArrayList<>();

  /**
   * Unique node ID.
   */
  private final int nodeID;

  /**
   * Region assigned to the node.
   */
  private final int region;

  /**
   * Mining power assigned to the node.
   */
  @Getter
  @Setter
  private long miningPower;

  /**
   * The consensus algorithm used by the node.
   */
  private AbstractConsensusAlgo consensusAlgo;

  /**
   * The current block.
   */
  private Block block;

  /**
   * Orphaned blocks known to node.
   */
  private final Set<Block> orphans = new HashSet<>();

  /**
   * The current minting task.
   */
  private AbstractMintingTask mintingTask = null;

  /**
   * In the process of downloading a new block.
   * This gets flagged true when the node is receiving the bits for the block
   * Happens in response to the getdata message
   */
  private boolean downloadingBlock = false;

  private final ArrayList<RecMessageTask> messageQue = new ArrayList<>();
  private final Set<Block> downloadingBlocks = new HashSet<>();

  /**
   * Currently hard coded to 0.5MB.
   * @see helpers.SimulationSettings.java
   */
  @Getter
  private final long blockSize;

  /**
   * Currently hard coded to 2 milliseconds.
   * @see helpers.SimulationSettings.java
   */
  @Getter
  private final long processingTime;

  @Getter
  @Setter
  // Opt for composition since extending is not an option
  // Use setter injection
  private VisualizerEventEmitter visualizerEventEmitter;

  private boolean performanceMode = true;

  @Getter
  private List<IBlockPropagationObserver> blockObservers;

  @Setter
  private Network network;

  /**
   * Instantiates a new Node.
   *
   * @param nodeID      the node id
   * @param region      the region
   * @param miningPower the mining power
   */
  public AbstractNode(int nodeID, int region, int numConnection, long miningPower, Timer timer, long blockSize,
      long processingTime) {
    super(timer);
    this.nodeID = nodeID;
    this.region = region;
    this.numConnection = numConnection;
    this.miningPower = miningPower;
    this.blockObservers = new ArrayList<>();
    this.blockSize = blockSize;
    this.processingTime = processingTime;

  }

  /**
   * Gets the node id.
   *
   * @return the node id
   */
  public int getNodeID() {
    return this.nodeID;
  }

  /**
   * Gets the region ID assigned to a node.
   *
   * @return the region
   */
  public int getRegion() {
    return this.region;
  }

  /**
   * Gets the consensus algorithm.
   *
   * @return the consensus algorithm. 
   * @see AbstractConsensusAlgo.java
   */
  @SuppressWarnings("unused")
  public AbstractConsensusAlgo getConsensusAlgo() {
    return this.consensusAlgo;
  }

  /**
   * Gets the current block.
   *
   * @return the block
   */
  public Block getBlock() {
    return this.block;
  }

  /**
   * Gets all orphans known to node.
   *
   * @return the orphans
   */
  public Set<Block> getOrphans() {
    return this.orphans;
  }

  /**
   * Gets the nodes neighbors.
   *
   * @return the neighbors
   */
  public ArrayList<AbstractNode> getNeighbors() {
    ArrayList<AbstractNode> neighbors = new ArrayList<>();
    neighbors.addAll(outbound);
    neighbors.addAll(inbound);
    return neighbors;
  }

  /**
   * Adds the node as a neighbor.
   * @param neighborNode the node to be added as a neighbor
   * @return the success state of the operation
   */
  public boolean addNeighbor(AbstractNode neighborNode) {
    if (neighborNode.getNodeID() == this.nodeID || this.outbound.contains(neighborNode)
        || this.inbound.contains(neighborNode) || this.outbound.size() >= this.getNumConnection()) {
      return false;
    } else if (this.outbound.add(neighborNode) && neighborNode.addInbound(this)) {
      emitAddLink(neighborNode);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Adds the provided node as an inbound connection.
   *
   * @param from the node to be added as an inbound connection
   * @return the success state of the operation
   */
  public boolean addInbound(AbstractNode from) {
    if (this.inbound.add(from)) {
      emitAddLink(from);
      return true;
    }
    return false;
  }

  /**
   * Removes the provided node as an inbound connection.
   *
   * @param from the node to be removed as an inbound connection
   * @return the success state of the operation
   */
  public boolean removeInbound(AbstractNode from) {
    if (this.inbound.remove(from)) {
      emitRemoveLink(from);
      return true;
    }
    return false;
  }

  /**
   * Removes the neighbor form the node.
   *
   * @param node the node to be removed as a neighbor
   * @return the success state of the operation
   */
  public boolean removeNeighbor(AbstractNode node) {
    if (this.outbound.remove(node) && node.removeInbound(this)) {
      emitRemoveLink(node);
      return true;
    }
    return false;
  }

  /**
   * Initializes the routing table.
   *
   * @param bootstrapNodes the bootstrap nodes
   */
  public void joinNetwork(List<AbstractNode> bootstrapNodes, Random random) {
    initTable(bootstrapNodes, random);
  }

  /**
   * Initializes a new BitcoinCore routing table. From a pool of all available
   * nodes, choose candidates at random and fill the table using the allowed
   * outbound connections amount.
   */
  // TODO this should be done using the bootstrap node
  public void initTable(List<AbstractNode> bootstrapNodes, Random random) {

    ArrayList<Integer> candidates = new ArrayList<>();
    for (int i = 0; i < bootstrapNodes.size(); i++) {
      candidates.add(i);
    }

    // Workaround to keep simulation deterministic
    Collections.shuffle(candidates, new Random(random.nextLong()));
    for (int candidate : candidates) {
      if (this.outbound.size() < this.getNumConnection()) {
        this.addNeighbor(bootstrapNodes.get(candidate));
      } else {
        break;
      }
    }
  }

  /**
   * Adds a new block to the to chain. 
   * If node was minting that task instance is abandoned, 
   * and the new block arrival is handled.
   *
   * @param newBlock the new block
   */
  public void addToChain(Block newBlock) {
    // If the node has been minting
    this.removeMiningTask();

    // Update the current block
    this.block = newBlock;
    printAddBlock(newBlock);
    // Observe and handle new block arrival
    notifyObservers(newBlock, this);
    // arriveBlock(newBlock, this);
  }

  /**
   * Logs the provided block to the logfile.
   *
   * @param newBlock the block to be logged
   */
  protected void printAddBlock(Block newBlock) {

    if (!performanceMode) {
      VisualizerEvent addBlock = new AddBlock(getCurrentTime(), this.getNodeID(), newBlock.getId());
      visualizerEventEmitter.emit(addBlock);
    }
  }


  /**
   * Removes the mining task.
   */
  protected void removeMiningTask() {
    if (this.mintingTask != null) {
      removeTask(this.mintingTask);
      this.mintingTask = null;
    }
  }

  /**
   * Add orphans.
   * If the valid chain is A->B->C and the orphan chain is A->D->E
   * then C orphans is E, and B's orphans is D
   * Not entirely sure why this is set like this
   * @param orphanBlock the orphan block
   * @param validBlock  the valid block
   */
  // TODO figure out why every block in the chain gets the orphan block set instead of just the last block
  public void addOrphans(Block orphanBlock, Block validBlock) {

    // Recursively traverse the two block chains until you find matching blocks
    if (orphanBlock != validBlock) {
      this.orphans.add(orphanBlock);
      this.orphans.remove(validBlock);
      if (validBlock == null || orphanBlock.getHeight() > validBlock.getHeight()) {
        this.addOrphans(orphanBlock.getParent(), validBlock);
      } else if (orphanBlock.getHeight() == validBlock.getHeight()) {
        this.addOrphans(orphanBlock.getParent(), validBlock.getParent());
      } else {
        this.addOrphans(orphanBlock, validBlock.getParent());
      }
    }
  }

  /**
   * Generates a new minting task and registers it.
   */
  public void minting() {
    AbstractMintingTask task = this.consensusAlgo.minting();
    this.mintingTask = task;
    if (task != null) {
      putTask(task);
    }
  }

  /**
   * Send an inv message to notify neighboring nodes that this node has a new block.
   * @param block the block
   */
  public void sendInv(Block block) {
    for (AbstractNode to : getNeighbors()) {

      long latency = this.getLatency(to.getRegion());

      // TODO duration;
      long duration = 10;
      AbstractMessageTask task = new InvMessageTask(this, to, block, latency + duration);
      putTask(task);
    }
  }

  /**
   * Receive block.
   * If the received block is valid, the node will propigate block to peers
   * @param receivedBlock the block
   */
  public void receiveBlock(Block receivedBlock) {

    if (this.consensusAlgo.isReceivedBlockValid(receivedBlock, this.block)) {

      if (this.block != null && !this.block.isOnSameChainAs(receivedBlock)) {
        // If the new block orphans this node's current block then add it to the orphans
        this.addOrphans(this.block, receivedBlock);
      } 

      // This actually replaces the current block with the received block rather than adding it
      this.addToChain(receivedBlock);
      // Generates a new minting task
      this.minting();
      // Advertise received block
      this.sendInv(receivedBlock);

    } else if (!this.orphans.contains(receivedBlock) && !receivedBlock.isOnSameChainAs(this.block)) {
      // TODO better understand - what if orphan is not valid?
      // If the block was not valid but was an unknown orphan and is not on the same
      // chain as the current block 
      this.addOrphans(receivedBlock, this.block);
      this.notifyObservers(receivedBlock, this);
    }
  }

  /**
   * Receive message.
   * @see Network.Java details how the latency is generated
   * @param message the message
   */
  public void receiveMessage(AbstractMessageTask message) {

    if (message instanceof InvMessageTask) {
      // TODO must be a better way than to add 10 this way
      long delay = 10; // As it was hardcoded in AbstractMessageTask in legacy code

      // The latency is generated from a table that lists the latency between geographical locations in milliseconds
      // The "from" geographical location is compared to the "to" geographical location
      // There is no bandwidth caluculation here because they assume the message size is 0. See paper on page 327
      AbstractNode from = message.getFrom();
      long latencyInMilliseconds = this.getLatency(from.getRegion()); 

      Block newBlock = ((InvMessageTask) message).getBlock();


      // Simplified version of the inv message exchange
      // Ignore messages that this node already knows about or are on a shorter chain
      // See https://bitcoin.stackexchange.com/questions/61191/how-do-bitcoin-nodes-sync-with-the-inv-mechanism
      if ((this.block == null || newBlock.getHeight() > this.block.getHeight()) 
          && !this.downloadingBlocks.contains(newBlock)) {

        AbstractMessageTask recBlockTask = new RecMessageTask(this, from, newBlock, latencyInMilliseconds + delay);
        putTask(recBlockTask);
        downloadingBlocks.add(newBlock);

      } 
      // else if (!this.orphans.contains(newBlock)) {
      //   this.orphans.add(newBlock);
      //   // I think I should just add the block to the orphans I know about
      //   // AbstractMessageTask task = new RecMessageTask(this, from, newBlock, latencyInMilliseconds + delay);
      //   // putTask(task);
      //   // downloadingBlocks.add(newBlock);
      // }
    }

    // Send a getdata message
    // See https://en.bitcoin.it/wiki/Protocol_documentation#getdata
    if (message instanceof RecMessageTask) {
      this.messageQue.add((RecMessageTask) message);
      if (!downloadingBlock) {
        this.processReceiveBlockMessage();
      }
    }

    // Receive a block from a neighboring node
    // See https://en.bitcoin.it/wiki/Protocol_documentation#block
    if (message instanceof BlockMessageTask) {
      Block block = ((BlockMessageTask) message).getBlock();
      downloadingBlocks.remove(block);
      this.receiveBlock(block);
    }
  }

  /**
   * Calculates how long it should take to receive a block from a neighbor and adds task.
   * Gets the sending node from the message queue and creates a new block message
   * This is the getdata message. See https://en.bitcoin.it/wiki/Protocol_documentation#getdata
   */
  public void processReceiveBlockMessage() {
    if (this.messageQue.size() > 0) {
      downloadingBlock = true;

      AbstractNode fromNode = this.messageQue.get(0).getFrom();
      Block blockToReceive = this.messageQue.get(0).getBlock();
      this.messageQue.remove(0);
      long bandwidthBetweenNodes = getBandwidth(fromNode.getRegion()); // upload and download speeds between regions

      // Convert bytes to bits and divide by the bandwidth expressed as bit per
      // millisecond, add processing time. (block size is currently hard coded to 0.5MB)
      // The processing time is currently hard coded to 2 milliseconds
      long bitsPerMillisecond = bandwidthBetweenNodes / 1000;
      long blockSizeInBits = this.blockSize * 8;
      long delay = blockSizeInBits / bitsPerMillisecond + this.processingTime; 

      long durationInMilliseconds = this.getLatency(fromNode.getRegion()) + delay;

      BlockMessageTask messageTask = new BlockMessageTask(this, fromNode, blockToReceive, durationInMilliseconds);

      putTask(messageTask);
    } else {
      downloadingBlock = false;
    }
  }

  @Override
  public String toString() {
    return "Node{" + "nodeID=" + nodeID + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractNode node = (AbstractNode) o;
    return nodeID == node.nodeID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodeID);
  }

  @Override
  public void register(IBlockPropagationObserver o) {
    this.blockObservers.add(o);
  }

  @Override
  public void unregister(IBlockPropagationObserver o) {
    this.blockObservers.remove(o);
  }

  /**
   * Notify the block observers to keep track of statistics.
   */
  @Override
  public void notifyObservers(Block block, AbstractNode node) {
    for (IBlockPropagationObserver o : blockObservers) {
      o.update(block, node, getCurrentTime());
    }

  }

  public void setConsensusAlgo(AbstractConsensusAlgo consensusAlgo) {
    this.consensusAlgo = consensusAlgo;
  }

  @Override
  public long getBandwidth(int toRegionID) {
    return this.network.getBandwidth(this.getRegion(), toRegionID);
  }

  @Override
  public long getLatency(int toRegionID) {
    return this.network.getLatency(this.getRegion(), toRegionID);
  }

  private void emitAddLink(AbstractNode endNode) {
    // TODO - no execution task preceeds or follows this

    if (!performanceMode) {
      visualizerEventEmitter.emitAddLink(this.nodeID, endNode.getNodeID());
    }
    

  }

  private void emitRemoveLink(AbstractNode endNode) {
    // TODO - no execution task preceeds or follows this

    if (!performanceMode) {
      visualizerEventEmitter.emitRemoveLink(this.nodeID, endNode.getNodeID());
    }
    
  }
}
