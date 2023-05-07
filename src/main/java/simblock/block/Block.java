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

package simblock.block;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simblock.node.AbstractNode;

/**
 * The representation of a block.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Block {
  
  /**
   * The current height of the block.
   */
  @Getter
  private final int height;

  /**
   * The parent {@link Block}.
   */
  protected final Block parent;

  /**
   * The {@link AbstractNode} that minted the block.
   */
  private final AbstractNode minter;

  /**
   * Minting timestamp, absolute time since the beginning of the simulation.
   */
  protected final long time;

  /**
   * Block unique id.
   */
  @EqualsAndHashCode.Include
  private final int id;


  /**
   * Instantiates a new Block.
   *
   * @param parent the parent
   * @param minter the minter
   * @param time   the time
   */
  public Block(Block parent, AbstractNode minter, long time, int id) {
    this.height = parent == null ? 0 : parent.getHeight() + 1;
    this.parent = parent;
    this.minter = minter;
    this.time = time;
    this.id = id;
  }


  /**
   * Get parent block.
   *
   * @return the block
   */
  public Block getParent() {
    return this.parent;
  }

  /**
   * Get minter node.
   *
   * @return the node
   */
  public AbstractNode getMinter() {
    return this.minter;
  }

  /**
   * Gets the simulation time since minting in milliseconds time.
   *
   * @return the time
   */
  public long getTime() {
    return this.time;
  }

  /**
   * Gets the block id.
   *
   * @return the id
   */
  public int getId() {
    return this.id;
  }

  /**
   * Generates the genesis block. The parent is set to null and the time is set to 0
   *
   * @param minter the minter
   * @return the block
   */
  public static Block genesisBlock(AbstractNode minter) {
    return new Block(null, minter, 0, 0);
  }

  /**
   * Recursively searches for the block at the provided height.
   *
   * @param height the height
   * @return the block with the provided height
   */
  public Block getBlockWithHeight(int height) {
    if (this.height == height) {
      return this;
    } else {
      return this.parent.getBlockWithHeight(height);
    }
  }

  /**
   * Checks if the provided block is on the same chain as self.
   *
   * @param block the block to be checked
   * @return true if block are on the same chain false otherwise
   */
  public boolean isOnSameChainAs(Block block) {
    if (block == null) {
      return false;
    } else if (this.height <= block.height) {
      return this.equals(block.getBlockWithHeight(this.height));
    } else {
      return this.getBlockWithHeight(block.height).equals(block);
    }
  }

  @Override
  public String toString() {

    return "Block{"
        + "height=" + height
        + ", parent=" + (parent == null ? null : parent.getId())
        + ", minter=" + minter
        + ", time=" + TimeUnit.MILLISECONDS.toMinutes(time) + " minutes " + TimeUnit.MILLISECONDS.toSeconds(time) + " seconds (" + time + "ms)"
        + ", id=" + id
        + '}';
  }
}
