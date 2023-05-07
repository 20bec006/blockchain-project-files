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

package simblock.simulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simblock.block.Block;
import simblock.node.AbstractNode;
import simblock.node.consensus.INetworkAware;


/**
 * The type Simulator is tasked with maintaining the list of simulated nodes and managing the
 * block interval. It observes and manages the arrival of new blocks at the simulation level.
 */
@AllArgsConstructor
public class Simulator extends TaskManager implements INetworkAware {

  private static final Logger logger = LoggerFactory.getLogger(Simulator.class);

  /**
   * A list of nodes that will be used in a simulation.
   */
  @Getter
  private ArrayList<AbstractNode> simulatedNodes;

  /**
   * The target block interval in milliseconds.
   */
  private long targetInterval;

  /**
   * Instantiates a new Simulator using the default BlockPropagationObserver comparator.
   * @param timer the timer instance shar-ed in the simulation
   */
  public Simulator(Timer timer, long blockInterval) {
    super(timer);

    this.simulatedNodes = new ArrayList<>();
    this.targetInterval = blockInterval;
  }

  /**
   * Get target block interval.
   *
   * @return the target block interval in milliseconds
   */
  @Override
  public long getTargetInterval() {
    return targetInterval;
  }

  /**
   * Sets the target block interval.
   *
   * @param interval - block interval in milliseconds
   */
  public void setTargetInterval(long interval) {
    targetInterval = interval;
  }

  /**
   * Add node to the list of simulated nodes.
   *
   * @param node the node
   */
  public void addNode(AbstractNode node) {
    simulatedNodes.add(node);
  }

  /**
   * Remove node from the list of simulated nodes.
   *
   * @param node the node
   */
  @SuppressWarnings("unused")
  public void removeNode(AbstractNode node) {
    simulatedNodes.remove(node);
  }

  /**
   * Add node to the list of simulated nodes and immediately try to add the new node as a
   * neighbor to all simulated
   * nodes.
   *
   * @param node the node
   */
  @SuppressWarnings("unused")
  @SneakyThrows(NotImplementedException.class)
  public void addNodeWithConnection(AbstractNode node) {
    throw new NotImplementedException("This method is no longer used. "
                                      + "If you want to use it you'll need to figure out how to get the random class in here");

    // node.joinNetwork(simulatedNodes, null);
    // addNode(node);
    // for (AbstractNode existingNode : simulatedNodes) {
    //   existingNode.addNeighbor(node);
    // }
  }

  /**
   * Print propagation information about the propagation of the provided block in the format:
   *
   * <p><em>node_ID, propagation_time</em>
   *
   * <p><em>propagation_time</em>: The time from when the block of the block ID is generated to
   * when the
   * node of the <em>node_ID</em> is reached.
   *
   * @param block       the block
   * @param propagation the propagation of the provided block as a list of {@link AbstractNode} IDs and
   *                    propagation times
   */
  public void printPropagation(Block block, LinkedHashMap<Integer, Long> propagation) {
    // Print block and its height
    //TODO block does not have a toString method, what is printed here
    logger.info(block + " propagation information:");
    for (Map.Entry<Integer, Long> timeEntry : propagation.entrySet()) {
      logger.info("Node ID: " + timeEntry.getKey() + "," + " Propagation time: " + timeEntry.getValue());
    }
  }

  
}
