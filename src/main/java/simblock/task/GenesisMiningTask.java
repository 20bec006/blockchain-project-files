package simblock.task;

import java.math.BigInteger;
import simblock.block.IBlockIdGenerator;
import simblock.block.ProofOfWorkBlock;
import simblock.node.AbstractNode;
import simblock.simulator.Timer;


//The genesis difficulty needs to be given here, and the matching constructor used,2
public class GenesisMiningTask extends AbstractMintingTask {


  private BigInteger genesisNextDifficulty;
  private IBlockIdGenerator generator;

  /**
   * Instantiates a new Abstract minting task.
   *
   * @param minter   the minter
   * @param interval the interval in milliseconds
   * @param timer the timer instance shared in the simulation
   */

  public GenesisMiningTask(AbstractNode minter, long interval, Timer timer, BigInteger genesisNextDifficulty, IBlockIdGenerator generator) {
    super(minter, interval, timer);
    this.genesisNextDifficulty = genesisNextDifficulty;
    this.generator = generator;
  }

  @Override
  public void run() {
    ProofOfWorkBlock genesis = new ProofOfWorkBlock(
        this.getMinter(), genesisNextDifficulty, generator.createNextBlockID()
    );

    this.getMinter().receiveBlock(genesis);
  }

}

