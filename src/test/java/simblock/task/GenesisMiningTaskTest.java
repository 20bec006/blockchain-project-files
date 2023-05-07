package simblock.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import simblock.block.IBlockIdGenerator;
import simblock.block.ProofOfWorkBlock;
import simblock.node.AbstractNode;
import simblock.simulator.Timer;

class GenesisMiningTaskTest {
  private AbstractNode minter;
  private Timer timer;
  private IBlockIdGenerator generator;

  @BeforeEach
  void setUp() {
    minter = mock(AbstractNode.class);
    timer = mock(Timer.class);
    generator = mock(IBlockIdGenerator.class);
    when(generator.createNextBlockID()).thenReturn(0);
  }

  @Test
  void canCreateGenesisBlock() {

    BigInteger genesisNextDifficulty = BigInteger.valueOf(500);

    AbstractMintingTask mt = new GenesisMiningTask(minter, 0, timer, genesisNextDifficulty, generator);

    ProofOfWorkBlock expectedGenesis = new ProofOfWorkBlock(
        minter, genesisNextDifficulty, generator.createNextBlockID()
    );
    mt.run();

    ArgumentCaptor<ProofOfWorkBlock> argument = ArgumentCaptor.forClass(ProofOfWorkBlock.class);
    verify(minter).receiveBlock(argument.capture());
    ProofOfWorkBlock actual = argument.getValue();

    assertEquals(actual, expectedGenesis);
  }
}