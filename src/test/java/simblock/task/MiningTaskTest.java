package simblock.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import simblock.AutoIncrementingGenerator;
import simblock.block.IBlockIdGenerator;
import simblock.block.ProofOfWorkBlock;
import simblock.node.AbstractNode;
import simblock.simulator.Timer;

class MiningTaskTest {

  private AbstractNode minter;
  private Timer timer;
  private IBlockIdGenerator generator;

  @BeforeEach
  void setUp() {
    minter = mock(AbstractNode.class);
    timer = mock(Timer.class);
    generator = new AutoIncrementingGenerator();
  }

  @Test
  void canCreateDescendantBlock() {

    BigInteger genesisNextDifficulty = BigInteger.valueOf(500);
    ProofOfWorkBlock expectedGenesis = new ProofOfWorkBlock(
        minter, genesisNextDifficulty, 0
    );

    new GenesisMiningTask(minter, 0, timer, genesisNextDifficulty, generator).run();


    ArgumentCaptor<ProofOfWorkBlock> argument = ArgumentCaptor.forClass(ProofOfWorkBlock.class);
    verify(minter).receiveBlock(argument.capture());
    ProofOfWorkBlock actual = argument.getValue();

    assertEquals(actual, expectedGenesis);

    when(minter.getBlock()).thenReturn(expectedGenesis);

    BigInteger descendantDifficulty = BigInteger.valueOf(500);

    ProofOfWorkBlock expectedDescendant = new ProofOfWorkBlock(expectedGenesis, minter, 0, descendantDifficulty, 1);

    new MiningTask(minter, 0, descendantDifficulty, timer, generator).run();


    ArgumentCaptor<ProofOfWorkBlock> argumentDescendant = ArgumentCaptor.forClass(ProofOfWorkBlock.class);
    verify(minter, times(2)).receiveBlock(argumentDescendant.capture());
    ProofOfWorkBlock actualDescendant = argumentDescendant.getValue();

    assertEquals(expectedDescendant, actualDescendant);
  }

}