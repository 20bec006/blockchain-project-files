package simblock.node.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pojo.events.VisualizerEvents;
import simblock.block.IBlockIdGenerator;
import simblock.node.AbstractNode;
import simblock.node.NodeFactory;
import simblock.node.NodeType;
import simblock.simulator.Timer;

class BitcoinCoreTableTest {

  private AbstractNode btcNode1;
  private AbstractNode btcNode2;

  private List<AbstractNode> bootstrapNodes;
  private VisualizerEvents log;
  private NodeFactory nodeFactory;

  private Timer timer;
  private Random random;

  @BeforeEach
  @SneakyThrows(NotImplementedException.class)
  void setUp() {
    random = new Random();
    log = mock(VisualizerEvents.class);
    timer = mock(Timer.class);
    nodeFactory = new NodeFactory(timer, random, mock(IBlockIdGenerator.class));
    btcNode1 = nodeFactory.createNode(1, 8, 0, NodeType.BTC);
    btcNode2 = nodeFactory.createNode(2, 8, 1, NodeType.BTC);

    bootstrapNodes = new ArrayList<>(List.of(btcNode1, btcNode2));
  }

  @Test
  void getNeighbors() {

    btcNode1.addNeighbor(btcNode2);
    btcNode2.addNeighbor(btcNode1);

    List<AbstractNode> expected1 = new ArrayList<>(List.of(btcNode2));
    List<AbstractNode> expected2 = new ArrayList<>(List.of(btcNode1));

    assertEquals(expected1, btcNode1.getNeighbors());
    assertEquals(expected2, btcNode2.getNeighbors());
  }

  @Test
  void initTable() {

    btcNode1.initTable(bootstrapNodes, random);

    assertEquals(List.of(btcNode2), btcNode1.getOutbound());
    assertEquals(List.of(btcNode1), btcNode2.getInbound());
  }

  @Test
  void addNeighborProperlyAddsNewNodes() {
    assertFalse(btcNode1.getOutbound().contains(btcNode2));
    assertFalse(btcNode2.getInbound().contains(btcNode1));

    btcNode1.addNeighbor(btcNode2);

    assertTrue(btcNode1.getOutbound().contains(btcNode2));
    assertTrue(btcNode2.getInbound().contains(btcNode1));
  }

  @Test
  void addNeighborDoesNotAddSelfNode() {
    assertFalse(btcNode1.getOutbound().contains(btcNode1));
    assertFalse(btcNode1.getInbound().contains(btcNode1));

    btcNode1.addNeighbor(btcNode1);

    assertFalse(btcNode1.getOutbound().contains(btcNode1));
    assertFalse(btcNode1.getInbound().contains(btcNode1));

  }

  @Test
  @SneakyThrows(NotImplementedException.class)
  void addNeighborDoesNotSurpassCapacity() {
    AbstractNode btcNode3 = nodeFactory.createNode(3, 1, 0, NodeType.BTC);

    btcNode3.addNeighbor(btcNode1);
    btcNode3.addNeighbor(btcNode2);

    assertTrue(btcNode3.getOutbound().contains(btcNode1));
    assertTrue(btcNode1.getInbound().contains(btcNode3));

    assertEquals(1, btcNode3.getOutbound().size());
    assertEquals(1, btcNode1.getInbound().size());

    assertFalse(btcNode3.getOutbound().contains(btcNode2));
    assertFalse(btcNode2.getInbound().contains(btcNode3));
  }

  @Test
  void addNeighborDoesNotAddAlreadyExistingNode() {
    btcNode1.addNeighbor(btcNode2);
    btcNode1.addNeighbor(btcNode2);

    assertTrue(btcNode1.getOutbound().contains(btcNode2));
    assertEquals(1, btcNode1.getOutbound().size());
    assertFalse(btcNode2.getInbound().contains(btcNode2));
    assertEquals(1, btcNode2.getInbound().size());
  }

  @Test
  void removeNeighbor() {
    btcNode1.addNeighbor(btcNode2);
    btcNode1.removeNeighbor(btcNode2);
    assertFalse(btcNode1.getOutbound().contains(btcNode2));
    assertFalse(btcNode2.getInbound().contains(btcNode1));
  }

  @Test
  void addInbound() {
    assertFalse(btcNode1.getInbound().contains(btcNode2));
    btcNode1.addInbound(btcNode2);
    assertTrue(btcNode1.getInbound().contains(btcNode2));
  }

  @Test
  void removeInbound() {

    btcNode1.addInbound(btcNode2);
    btcNode1.removeInbound(btcNode2);
    assertFalse(btcNode1.getInbound().contains(btcNode2));
  }
}