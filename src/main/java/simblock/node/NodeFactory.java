package simblock.node;

import java.util.Random;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import observable.interfaces.IBlockPropagationObserver;
import pojo.events.VisualizerEventEmitter;
import pojo.events.VisualizerEvents;
import simblock.block.IBlockIdGenerator;
import simblock.node.consensus.AbstractConsensusAlgo;
import simblock.node.consensus.INetworkAware;
import simblock.node.consensus.ProofOfWork;
import simblock.simulator.Network;
import simblock.simulator.Timer;

/**
 * The type Node factory abstracts the creation of DLT nodes.
 */
@AllArgsConstructor
@Getter
public class NodeFactory {


  private IBlockPropagationObserver blockObserver;
  /**
   * The log containing events for the visualizer.
   */
  private VisualizerEvents log;
  /**
   * The timer instance shared in the simulation.
   */
  private Timer timer;
  /**
   * The log containing events for the visualizer.
   */
  private INetworkAware networkAware;
  /**
   * The random instance shared in the simulation.
   */
  private Random random;
  /**
   * The block ID generator.
   */
  private IBlockIdGenerator generator;
  /**
   * The log containing events for the visualizer.
   */
  private Network network;

  private int stdevOfMiningPower;
  private int averageMiningPower;
  private long blockSize;
  private long processingTime;

  /**
   * The factory used to create new {@link AbstractNode} instances. Hides all the complexity of node creation,
   *
   * @param timer     the timer instance shared in the simulation
   * @param random    the random instance shared in the simulation
   * @param generator the block ID generator
   */
  public NodeFactory(Timer timer, Random random, IBlockIdGenerator generator) {
    this.timer = timer;

    // Specify explicitly for clarity
    // No one observes blocks
    this.blockObserver = null;

    // Visualizer events are not important, the instance is encapsulated
    this.log = null;

    // No need to know about network etc
    this.networkAware = null;

    // Always inject random for determinism
    this.random = random;

    this.generator = generator;

  }


  /**
   * Create node .
   *
   * @param nodeID        the node id
   * @param numConnection the num connection
   * @param region        the region
   * @param type          the type
   * @return the node
   */
  public AbstractNode createNode(int nodeID,
                         int numConnection,
                         int region,
                         NodeType type

  ) throws NotImplementedException {
    switch (type) {
      case BTC:
        return buildBtcNode(nodeID, numConnection, region);
      case BTC_SelfishMiner:
        return buildSelfishMiningBtcNode(nodeID, numConnection, region);
      default:
        throw new NotImplementedException(type.name() + " is not implemented");
    }

  }

  private AbstractNode buildBtcNode(int nodeID,
                            int numConnection,
                            int region
  ) {

    //TODO block size and processing time should be put here. MAP: agreed. Not all blocks will be the same size


    // The complexity of this method is a consequence of the chosen legacy code architecture.
    // After removing static imports to be able to unit test, interfaces have been used
    // in an effort to clarify code and rewrite it using IoC. The factory class hides this complexity

    // Visualizer events that a node emits will be added to this log
    VisualizerEventEmitter eventEmitter = new VisualizerEventEmitter(log, timer);
    AbstractNode btcNode = new NetworkNode(nodeID, 
                                           region, 
                                           numConnection, 
                                           this.genMiningPower(), 
                                           timer, 
                                           blockSize, 
                                           processingTime);
    btcNode.setVisualizerEventEmitter(eventEmitter);

    // A consensus algo needs knowledge of a node, and a node needs knowledge of the consensus algo
    // Opted for setter injection as a solution for cyclic references
    AbstractConsensusAlgo pow = new ProofOfWork(btcNode, timer, networkAware, random, generator);
    btcNode.setConsensusAlgo(pow);

    // A node needs to know the latency, and the bandwith of the network
    btcNode.setNetwork(network);

    // We need to register any entities observing block propagation
    if (blockObserver != null) {
      btcNode.register(blockObserver);
    }

    // A node needs the simulation timer to be able to register simulation execution
    // Opted for setter injection to keep constructor short
    btcNode.setTimer(timer);

    return btcNode;
  }

  private AbstractNode buildSelfishMiningBtcNode(int nodeID,
                                         int numConnection,
                                         int region
  ) {

    // The complexity of this method is a consequence of the chosen legacy code architecture.
    // After removing static imports to be able to unit test, interfaces have been used
    // in an effort to clarify code and rewrite it using IoC. The factory class hides this complexity

    // Visualizer events that a node emits will be added to this log
    VisualizerEventEmitter eventEmitter = new VisualizerEventEmitter(log, timer);
    AbstractNode btcNode = new SelfishMiningNode(nodeID, 
                                                 region, 
                                                 numConnection, 
                                                 this.genMiningPower(), 
                                                 timer, 
                                                 blockSize, 
                                                 processingTime);
    btcNode.setVisualizerEventEmitter(eventEmitter);

    // A consensus algo needs knowledge of a node, and a node needs knowledge of the consensus algo
    // Opted for setter injection as a solution for cyclic references
    AbstractConsensusAlgo pow = new ProofOfWork(btcNode, timer, networkAware, random, generator);
    btcNode.setConsensusAlgo(pow);

    // A node needs to know the latency, and the bandwith of the network
    btcNode.setNetwork(network);

    // We need to register any entities observing block propagation
    if (blockObserver != null) {
      btcNode.register(blockObserver);
    }

    // A node needs the simulation timer to be able to register simulation execution
    // Opted for setter injection to keep constructor short
    btcNode.setTimer(timer);

    return btcNode;
  }

  /**
   * Generates a random mining power expressed as Hash Rate, and is the number of mining (hash
   * calculation) executed per millisecond.
   *
   * @return the number of hash calculations executed per millisecond.
   */
  private int genMiningPower() {
    double r = this.random.nextGaussian();

    return Math.max((int) (r * this.stdevOfMiningPower + this.averageMiningPower), 1);
  }

}
