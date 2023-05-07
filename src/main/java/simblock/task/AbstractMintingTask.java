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

package simblock.task;

import java.util.UUID;
import simblock.block.Block;
import simblock.node.AbstractNode;
import simblock.simulator.Timer;
import simblock.simulator.interfaces.ITimeAware;
import simblock.task.interfaces.ITask;

/**
 * The type Abstract minting task represents .
 */
public abstract class AbstractMintingTask implements ITask, ITimeAware {

  /**
   * The task ID.
   */
  private final UUID taskID;
  
  /**
   * The node to mint the block.
   */
  private final AbstractNode minter;
  /**
   * The parent block.
   */
  private final Block parent;

  /**
   * Block interval (how long it takes to mine) in milliseconds.
   */
  private final long interval;
  private Timer timer;

  /**
   * Instantiates a new Abstract minting task.
   *
   * @param minter   the minter
   * @param interval the interval in milliseconds
   */
  public AbstractMintingTask(AbstractNode minter, long interval, Timer timer) {
    this.parent = minter.getBlock();
    this.minter = minter;
    this.interval = interval;
    this.timer = timer;
    this.taskID = UUID.randomUUID();
  }

  /**
   * Gets minter.
   *
   * @return the minter
   */
  public AbstractNode getMinter() {
    return minter;
  }

  /**
   * Gets the minted blocks parent.
   *
   * @return the parent
   */
  public Block getParent() {
    return parent;
  }

  @Override
  public long getDuration() {
    return this.interval;
  }

  @Override
  public long getCurrentTime() {
    return timer.getCurrentTime();
  }

  /**
   * Get the task ID.
   *
   * @return the block
   */
  public UUID getTaskID() {
    return this.taskID;
  }
}
