package simblock.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import simblock.simulator.Network;
import simblock.simulator.Timer;
import simblock.task.InvMessageTask;
import simblock.task.RecMessageTask;

public class NetworkNodeTest {

  private AbstractNode btcNode1;
  private AbstractNode btcNode2;
  private NodeFactory nodeFactory;
  ProofOfWorkBlock mockedParentBlock;
  private Timer timer;
  private Random random;

  @BeforeEach
  @SneakyThrows(NotImplementedException.class)
  void setUp() {
    random = new Random();
    timer = new Timer();
    nodeFactory = new NodeFactory(timer, random, mock(IBlockIdGenerator.class));
    btcNode1 = nodeFactory.createNode(1, 8, 0, NodeType.BTC);
    btcNode2 = nodeFactory.createNode(2, 8, 1, NodeType.BTC);

    Network network = new Network(new Random(1));
    btcNode1.setNetwork(network);
    btcNode2.setNetwork(network);

    // Set up the fake parent block
    mockedParentBlock = mock(ProofOfWorkBlock.class);
    when(mockedParentBlock.getTotalDifficulty()).thenReturn(BigInteger.ZERO);
    when(mockedParentBlock.getNextDifficulty()).thenReturn(BigInteger.ZERO);
    when(mockedParentBlock.getDifficulty()).thenReturn(BigInteger.ZERO);
    when(mockedParentBlock.getHeight()).thenReturn(0);
    when(mockedParentBlock.getBlockWithHeight(0)).thenReturn(mockedParentBlock);
  }

  /**
   * Verify that orphan blocks get added correctly when a node recieves a longer
   * chain.
   */
  @Test
  void addOrphans() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock secondBlock = new ProofOfWorkBlock(firstBlock, btcNode1, 1, BigInteger.ZERO, 2);
    ProofOfWorkBlock thirdBlock = new ProofOfWorkBlock(secondBlock, btcNode1, 2, BigInteger.ZERO, 3);
    ProofOfWorkBlock orphanBlock = new ProofOfWorkBlock(firstBlock, btcNode2, 1, BigInteger.ZERO, 4);

    btcNode1.addOrphans(orphanBlock, thirdBlock);

    assertEquals(1, btcNode1.getOrphans().size());
    assertTrue(btcNode1.getOrphans().contains(orphanBlock));
  }

  /**
   * Verify that orphaning a longer chain adds all of the longer chain's blocks.
   * I'm not entirely sure this is a valid case but the legacy code seems to think
   * it is.
   */
  @Test
  void orphanLongerChain() {

    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 1);
    ProofOfWorkBlock orphanChainBlock1 = new ProofOfWorkBlock(firstBlock, btcNode1, 1, BigInteger.ZERO, 2);
    ProofOfWorkBlock orphanChainBlock2 = new ProofOfWorkBlock(orphanChainBlock1, btcNode1, 2, BigInteger.ZERO, 3);
    ProofOfWorkBlock validChainBlock1 = new ProofOfWorkBlock(firstBlock, btcNode2, 1, BigInteger.ZERO, 4);

    btcNode2.addOrphans(orphanChainBlock2, validChainBlock1);

    assertEquals(2, btcNode2.getOrphans().size());
    assertTrue(btcNode2.getOrphans().contains(orphanChainBlock1));
    assertTrue(btcNode2.getOrphans().contains(orphanChainBlock2));
  }

  /**
   * Verify that an inv message queues a receive message task.
   */
  @Test
  void receiveInvMessage() {
    ProofOfWorkBlock newBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 1);
    InvMessageTask invMsg = new InvMessageTask(btcNode2, btcNode1, newBlock, 10);

    btcNode1.receiveMessage(invMsg);
    var receiveMessageTask = btcNode1.getTimer().getTask();

    assertTrue(receiveMessageTask instanceof RecMessageTask);
  }

  /**
   * Verify that incoming block with the same height as the current block are rejected.
   */
  @Test
  void receiveInvMessage_shouldRejectSameHeightBlock() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 1);
    final ProofOfWorkBlock secondBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode2, 1, BigInteger.ZERO, 2);
    btcNode1.addToChain(mockedParentBlock);
    btcNode2.addToChain(mockedParentBlock);

    btcNode1.receiveBlock(firstBlock);
    var oneReceiveBlockTask = timer.getTaskQueueCopy().size();

    // Attempt to queue a inv message task by receiving an inv task
    // for a block with the same height as block this node already knows about
    InvMessageTask invMsg = new InvMessageTask(btcNode2, btcNode1, secondBlock, 10);
    btcNode1.receiveMessage(invMsg);

    // Verify that this node ignored the new block
    assertEquals(oneReceiveBlockTask, timer.getTaskQueueCopy().size());
  }

  /**
   * Verify that a receive block message queues a task to download the block.
   */
  @Test
  void receiveBlockMessage_shouldQueueBlockTask() {
    ProofOfWorkBlock firstBlock = new ProofOfWorkBlock(mockedParentBlock, btcNode1, 0, BigInteger.ZERO, 1);
    btcNode1.addToChain(mockedParentBlock);
    btcNode2.addToChain(firstBlock);

    RecMessageTask receiveBlockTask = new RecMessageTask(btcNode2, btcNode1, firstBlock, 10);
    btcNode1.receiveMessage(receiveBlockTask);

    // Verify that this node added the task
    assertEquals(1, timer.getTaskQueueCopy().size());
  }
}
