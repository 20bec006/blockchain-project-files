package simblock.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simblock.node.NetworkNode;

class BlockTest {

  NetworkNode mockedNode;
  ProofOfWorkBlock mockedParentBlock;

  @BeforeEach
  void setup() {
    mockedNode = mock(NetworkNode.class);
    mockedParentBlock = mock(ProofOfWorkBlock.class);
    when(mockedParentBlock.getTotalDifficulty()).thenReturn(BigInteger.ZERO);
    // TODO: change when difficulty is implemented
    when(mockedParentBlock.getNextDifficulty()).thenReturn(BigInteger.ZERO); 
  }

  @Test
  void getHeight() {
    Block testBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 0);

    assertEquals(1, testBlock.getHeight());
  }

  @Test
  void getParent() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock secondBlock = new ProofOfWorkBlock(firstBlock, mockedNode, 1, BigInteger.ZERO, 2);

    assertEquals(secondBlock.getParent(), firstBlock);
  }

  @Test
  void getMinter() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 1);

    assertEquals(mockedNode, firstBlock.getMinter());
  }

  @Test
  void getTime() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 1);

    assertEquals(0, firstBlock.getTime());
  }

  @Test
  void getId() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock secondBlock = new ProofOfWorkBlock(firstBlock, mockedNode, 1, BigInteger.ZERO, 2);

    assertEquals(2, secondBlock.getId());
  }

  @Test
  void genesisBlock() throws NoSuchFieldException, IllegalAccessException {
    Block genesisBlock = new Block(null, mockedNode, 0, 0);

    assertEquals(genesisBlock, Block.genesisBlock(mockedNode));
  }

  @Test
  void getBlockWithHeight() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock secondBlock = new ProofOfWorkBlock(firstBlock, mockedNode, 1, BigInteger.ZERO, 2);

    assertEquals(firstBlock, secondBlock.getBlockWithHeight(1));
  }

  /**
   * Verify a request for a non-existant block height returns null and doesn't error.
   */
  @Test
  void getBlockWithHeight_shouldReturn_null() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock secondBlock = new ProofOfWorkBlock(firstBlock, mockedNode, 1, BigInteger.ZERO, 2);

    assertEquals(null, secondBlock.getBlockWithHeight(3));
  }

  @Test
  void isOnSameChainAs() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, mockedNode, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock secondBlock = new ProofOfWorkBlock(firstBlock, mockedNode, 1, BigInteger.ZERO, 2);

    assertTrue(secondBlock.isOnSameChainAs(firstBlock));
  }
}