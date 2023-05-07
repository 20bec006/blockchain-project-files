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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import simblock.block.Block;
import simblock.node.AbstractNode;

// Bitcoin protocol Wiki: https://en.bitcoin.it/wiki/Protocol_documentation#block
/**
 * The type Block message task.
 */
@EqualsAndHashCode(callSuper = false)
@ToString
public class BlockMessageTask extends AbstractMessageTask {
  /**
   * The {@link Block} that is sent.
   */
  private final Block block;

  /**
   * Instantiates a new Block message task.
   *
   * @param from  the sender
   * @param to    the receiver
   * @param block the block instance
   * @param duration the block message sending delay in milliseconds.
   */
  public BlockMessageTask(AbstractNode from, AbstractNode to, Block block, long duration) {
    super(from, to, duration);
    this.block = block;
    // Lazy loaded latency...
    // Interval is latency + delay
    // Lazy loading should be ignored and this should be injected as a precalculated value
    // Here interval is latency + delay
    //this.interval = getLatency(this.getFrom().getRegion(), this.getTo().getRegion()) + delay;

  }

  /**
   * Sends a new block message from the sender to the receiver and logs the event.
   */
  @Override
  public void run() {

    // Again timer is used only for visualizer
    this.getFrom().processReceiveBlockMessage();
    super.run();
  }

  /**
   * Get block.
   *
   * @return the block
   */
  public Block getBlock() {
    return this.block;
  }


}
