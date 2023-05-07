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

import lombok.Getter;
import simblock.block.Block;
import simblock.simulator.Timer;

/**
 * A class representing a node in the network.
 */
public class SelfishMiningNode extends AbstractNode  {

  /**
   * The selfish miner's secret selfishly mined blocks.
   */
  @Getter
  private Block selfishMiningBlock;

  /**
   * Instantiates a new Node.
   *
   * @param nodeID      the node id
   * @param region      the region
   * @param miningPower the mining power
   */
  public SelfishMiningNode(
      int nodeID,
      int region,
      int numConnection,
      long miningPower,
      Timer timer,
      long blockSize,
      long processingTime
  ) {
    super(nodeID, region, numConnection, miningPower, timer, blockSize, processingTime);
  }

  /**
   * Receive block.
   * Implements the selfish mining algorithm.
   * @param block the block
   */
  @Override
  public void receiveBlock(Block block) {
    if (this.getConsensusAlgo().isReceivedBlockValid(block, this.getBlock())) {

      // If this miner mined the block then implement the selfish mining strategy
      // Add block to private chain and start mining the next block
      if (block.getMinter().getNodeID() == this.getNodeID()) {
        this.addToSelfishMiningChain(block);
        this.minting();
      } else {

        if (this.selfishMiningBlock != null) {
          implementSelfishMining(block);
        } else {
          originalReceiveBlockAlgorithm(block);
        }
      }
    } else if (!this.getOrphans().contains(block) && !block.isOnSameChainAs(this.getBlock())) {
      // If the block was not valid but was an unknown orphan and is not on the same
      // chain as the current block
      this.addOrphans(block, this.getBlock());
      this.notifyObservers(block, this);
    }
  }

  /**
   * Implement the normal received block algorithm.
   * @see AbstractNode.java for details
   */
  private void originalReceiveBlockAlgorithm(Block block) {
    if (this.getBlock() != null && !this.getBlock().isOnSameChainAs(block)) {
      // If the new block orphans this node's current block then add it to the orphans
      this.addOrphans(this.getBlock(), block);
    } 

    // Add to canonical chain
    this.addToChain(block);
    // Generates a new minting task
    this.minting();
    // Advertise received block
    this.sendInv(block);
    
  }

  /**
   * Adds the block to the selfish mining chain that is hidden from the rest of the network.
   * @param newBlock the newly mined block
   */
  public void addToSelfishMiningChain(Block newBlock) {
    this.removeMiningTask();
    this.selfishMiningBlock = newBlock;
    this.printAddBlock(newBlock);

    // Observe and handle new block arrival
    this.notifyObservers(newBlock, this);
  }

  /**
   * Implement the selfish mining strategy.
   */
  private void implementSelfishMining(Block incomingBlock) {
    // If the selfish miner is even with the rest of the network or only one block ahead then release this selfish mining chain
    // If the selfish miner is at least 2 two block ahead then just continue to mine on the selfish chain and discard the honest block
    if (this.selfishMiningBlock.getHeight() <= incomingBlock.getHeight() + 1) {

      // Selfish mining no longer has a significant lead so release the selfish block on the network
      this.addToChain(this.selfishMiningBlock);
      this.selfishMiningBlock = null;
      // Generates a new minting task
      this.minting();
      // Advertise received block
      this.sendInv(this.getBlock());
    }
  }
}
