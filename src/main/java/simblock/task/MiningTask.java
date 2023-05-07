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


import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import simblock.block.IBlockIdGenerator;
import simblock.block.ProofOfWorkBlock;
import simblock.node.AbstractNode;
import simblock.simulator.Timer;

/**
 * The type Mining task.
 */
@EqualsAndHashCode(callSuper = false)
@ToString
public class MiningTask extends AbstractMintingTask {
  protected final BigInteger difficulty;
  protected final IBlockIdGenerator generator;

  /**
   * Instantiates a new Mining task.
   *
   * @param minter     the minter
   * @param interval   the interval
   * @param difficulty the difficulty
   */
  public MiningTask(AbstractNode minter, long interval, BigInteger difficulty, Timer timer, IBlockIdGenerator generator) {
    super(minter, interval, timer);
    this.difficulty = difficulty;
    this.generator = generator;
  }

  @Override
  public void run() {

    ProofOfWorkBlock createdBlock = new ProofOfWorkBlock(
        (ProofOfWorkBlock) this.getParent(), this.getMinter(), getCurrentTime(),
        this.difficulty, generator.createNextBlockID()

    );
    this.getMinter().receiveBlock(createdBlock);
  }
}
