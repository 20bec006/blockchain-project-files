package simblock.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simblock.node.AbstractNode;

class ProofOfWorkBlockTest {
  private AbstractNode minter;

  @BeforeEach
  void setup() {
    minter = mock(AbstractNode.class);
  }

  @Test
  void canGenerateGenesis() {
    BigInteger genesisDifficulty = BigInteger.valueOf(500);
    int genesisID = 0;

    ProofOfWorkBlock genesis = new ProofOfWorkBlock(minter, genesisDifficulty, genesisID);

    assertEquals(0L, genesis.getTime());
    assertNull(genesis.getParent());
    assertEquals(minter, genesis.getMinter());
    assertEquals(0L, genesis.getHeight());
    assertEquals(BigInteger.ZERO, genesis.getDifficulty());
    assertEquals(BigInteger.ZERO, genesis.getTotalDifficulty());
    assertEquals(genesisDifficulty, genesis.getNextDifficulty());
    assertEquals(genesisID, genesis.getId());

  }

  @Test
  void canGenerateDescendant() {
    // Understand this first eh...
    // https://gitlab.com/doktorski/simblock/-/blob/master/src/main/java/simblock/block/ProofOfWorkBlock.java#L51
    BigInteger genesisNextDifficulty = BigInteger.valueOf(500);
    BigInteger descendantDifficulty = BigInteger.valueOf(700);

    int genesisID = 0;
    long descendantTime = 10L;

    ProofOfWorkBlock genesis = new ProofOfWorkBlock(minter, genesisNextDifficulty, genesisID);
    ProofOfWorkBlock descendant = new ProofOfWorkBlock(genesis, minter, descendantTime, descendantDifficulty, 1);

    assertEquals(descendantTime, descendant.getTime());
    assertEquals(genesis, descendant.getParent());
    assertEquals(minter, descendant.getMinter());
    assertEquals(1L, descendant.getHeight());
    assertEquals(descendantDifficulty, descendant.getDifficulty());

    // Total difficulty at genesis + descendant difficulty
    assertEquals(BigInteger.ZERO.add(descendantDifficulty), descendant.getTotalDifficulty());
    // Total difficulty from genesis, at this point it just perpetuates in the codebase
    assertEquals(genesisNextDifficulty, descendant.getNextDifficulty());

    assertEquals(descendantDifficulty, descendant.getDifficulty());

    assertEquals(1, descendant.getId());

  }

}