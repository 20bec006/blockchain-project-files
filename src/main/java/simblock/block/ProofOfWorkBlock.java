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

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import simblock.node.AbstractNode;


/**
 * The type Proof of work block.
 */
public class ProofOfWorkBlock extends Block {
  private final BigInteger difficulty;
  private final BigInteger totalDifficulty;
  private final BigInteger nextDifficulty;
  private int transactionCount = 0;
  private final int ballparkTransactionCount = 4200;

  /**
   * A  mining dates are calculate as starting on January 1st 2020.
   */
  private final Date january2020 = new Date(1577858400000L);
  //private final Date myDate = dateFormatter.parse("01-01-2020");

  /**
   * Assumes a 1,000,000 byte block divided by 4,200 transaction per block.
   */
  private final int averageBytesPerTransaction = 238;

  /**
   * Gets the transaction count.
   * According to https://en.bitcoin.it/wiki/Maximum_transaction_rate
   * the safe transaction rate is 4200 txs/block
   * By examining the btc block explorer, I saw anywhere from 15 to 2,500
   * @return the block's transaction count
   */
  public int getTransactionCount() {

    if (transactionCount == 0) {
      Random numberGenerator = new Random();
      transactionCount = numberGenerator.nextInt(ballparkTransactionCount);

      if (transactionCount == 0) {
        transactionCount++;
      } 
    }

    return transactionCount;
  }

  /**
   * Instantiates a new Proof of work block.
   *
   * @param parent     the parent
   * @param minter     the minter
   * @param time       the time
   * @param difficulty the difficulty.
   */
  public ProofOfWorkBlock(ProofOfWorkBlock parent, AbstractNode minter, long time, BigInteger difficulty, int id) {
    super(parent, minter, time, id);
    this.difficulty = difficulty;

    this.totalDifficulty = parent.getTotalDifficulty().add(difficulty);
    // TODO: difficulty adjustment is lacking, was never implemented in legacy
    // Next difficulty perpetuates from genesis next difficulty and is never adjusted
    this.nextDifficulty = parent.getNextDifficulty();

  }

  /**
   * Instantiates a new genesis block.
   *
   * @param minter the minter to mint the genesis
   * @param genesisNextDifficulty the next block difficulty
   * @param id the genesis block ID.
   */
  public ProofOfWorkBlock(AbstractNode minter, BigInteger genesisNextDifficulty, int id) {
    super(null, minter, 0, id);
    this.difficulty = BigInteger.ZERO;
    this.totalDifficulty = BigInteger.ZERO.add(difficulty);  //This is then 0+0??
    this.nextDifficulty = genesisNextDifficulty;


  }

  /**
   * Gets difficulty.
   *
   * @return the difficulty
   */
  public BigInteger getDifficulty() {
    return this.difficulty;
  }

  /**
   * Gets total difficulty.
   *
   * @return the total difficulty
   */
  public BigInteger getTotalDifficulty() {
    return this.totalDifficulty;
  }

  /**
   * Gets next difficulty.
   *
   * @return the next difficulty
   */
  public BigInteger getNextDifficulty() {
    return this.nextDifficulty;
  }

  /**
   * Gets the block data in the format needed for the random forest classifier.
   * @return the block data string
   */
  public String getMlBlockString(boolean isSelfish) {
    return "{"
        + "\"isSelfish\":" + (isSelfish ? 1 : 0)
        + ", \"height\":" + this.getHeight()
        + ", \"miner\":" + this.getMinter().getNodeID()
        + ", \"size\":" + getTransactionCount() * averageBytesPerTransaction
        // All the mining times are calculated since january 1st 2020
        + ", \"time\":" + getTimeDelta()
        + ", \"difficulty\":\"" + difficulty + "\""
        + ", \"txCount\":" + getTransactionCount()
        + '}';
  }

  private String getTimeDelta() {

    Long thisBlockTime = (TimeUnit.MILLISECONDS.toSeconds(time) + (january2020.getTime() / 1000));
    Long parentBlockTime = (TimeUnit.MILLISECONDS.toSeconds(parent.time) + (january2020.getTime() / 1000));
    Long timeDelta = thisBlockTime - parentBlockTime;

    return timeDelta.toString();
  }

}
