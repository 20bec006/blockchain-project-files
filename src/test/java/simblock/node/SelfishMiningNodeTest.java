package simblock.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Random;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simblock.block.IBlockIdGenerator;
import simblock.block.ProofOfWorkBlock;
import simblock.node.consensus.AbstractConsensusAlgo;
import simblock.simulator.Timer;

public class SelfishMiningNodeTest {

  private AbstractConsensusAlgo mockedConsensus;
  private AbstractNode btcNode1;
  private SelfishMiningNode selfishNode;
  private NodeFactory nodeFactory;
  ProofOfWorkBlock mockedParentBlock;
  private Timer timer;
  private Random random;

  @BeforeEach
  @SneakyThrows(NotImplementedException.class)
  void setUp() {

    random = new Random();
    timer = mock(Timer.class);

    // Mock the consensus algorithm
    mockedConsensus = mock(AbstractConsensusAlgo.class);
    when(mockedConsensus.isReceivedBlockValid(any(), any())).thenReturn(true);

    nodeFactory = new NodeFactory(timer, random, mock(IBlockIdGenerator.class));

    btcNode1 = nodeFactory.createNode(1, 8, 0, NodeType.BTC);
    selfishNode = (SelfishMiningNode) nodeFactory.createNode(3, 8, 1, NodeType.BTC_SelfishMiner);
    selfishNode.setConsensusAlgo(mockedConsensus);

    // Set up the fake parent block
    mockedParentBlock = mock(ProofOfWorkBlock.class);
    when(mockedParentBlock.getTotalDifficulty()).thenReturn(BigInteger.ZERO);
    when(mockedParentBlock.getNextDifficulty()).thenReturn(BigInteger.ZERO);

  }

  /**
   * Verify that a selfish miner with a significant lead won't release 
   * its blocks when a new block arrives.
   */
  @Test
  void blockArrival_hasSignificantLead() {
    
    ProofOfWorkBlock selfishBlock1 = new ProofOfWorkBlock(mockedParentBlock, selfishNode, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock selfishBlock2 = new ProofOfWorkBlock(selfishBlock1, selfishNode, 1, BigInteger.ZERO, 2);
    ProofOfWorkBlock selfishBlock3 = new ProofOfWorkBlock(selfishBlock2, selfishNode, 2, BigInteger.ZERO, 3);
    selfishNode.receiveBlock(selfishBlock3);

    assertEquals(selfishBlock3, selfishNode.getSelfishMiningBlock());

    // Recieve an honest block
    ProofOfWorkBlock honestBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 4);
    selfishNode.receiveBlock(honestBlock);
    
    // Verify that the selfish miners didn't accept the honest block
    assertEquals(selfishBlock3, selfishNode.getSelfishMiningBlock());
  }

  /**
   * Verify that a selfish miner whos lead is threatened will 
   * release its selfish mining blocks.
   */
  @Test
  void blockArrival_releasesAttack() {
    
    ProofOfWorkBlock selfishBlock1 = new ProofOfWorkBlock(mockedParentBlock, selfishNode, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock selfishBlock2 = new ProofOfWorkBlock(selfishBlock1, selfishNode, 1, BigInteger.ZERO, 2);
    selfishNode.receiveBlock(selfishBlock2);

    assertEquals(selfishBlock2, selfishNode.getSelfishMiningBlock());

    // Recieve an honest block
    ProofOfWorkBlock honestBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 3);
    selfishNode.receiveBlock(honestBlock);
    
    // Verify that the selfish miners didn't accept the honest block and
    // released the selfishly mined chain onto the network
    assertEquals(null, selfishNode.getSelfishMiningBlock());
    assertEquals(selfishBlock2, selfishNode.getBlock());
  }

  /**
   * Verify that a selfish miner will accept a network block
   * when they don't have an active selfish mining attack.
   */
  @Test
  void blockArrival_noActiveAttack() {
    
    ProofOfWorkBlock honestBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 1);
    selfishNode.receiveBlock(honestBlock);

    // Verify that the selfish miners didn't accept the honest block and
    // released the selfishly mined chain onto the network
    assertEquals(null, selfishNode.getSelfishMiningBlock());
    assertEquals(honestBlock, selfishNode.getBlock());  
  }
}
