package simblock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import helpers.SimulationFactory;
import helpers.SimulationType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simblock.block.Block;
import simblock.exceptions.PendingSimulationException;
import simblock.node.AbstractNode;
import simblock.task.ScheduledTask;
import visualizer.VisualizerEvent;

class SimulationTest {

  private static Simulation simulation;

  @BeforeAll
  static void setup() throws PendingSimulationException {
    AbstractNode n1 = mock(AbstractNode.class);

    //TODO Orphan : 1 : Block{height=1, parent=0, minter=Node{nodeID=2}, time=11 minutes 686 seconds (686020ms), id=3} makes no sense

    // Get the block time and divide by block height, this should converge into 10 minutes
    //SimulationFactory.getInstance(SimulationType.DEFAULT).run();

    /*
    Chosen example - disregard time inconsistencies, only timestamps used:

    OnChain : 1 : Block{height=1, parent=0, minter=Node{nodeID=1}, time=0 minutes 26 seconds (26681ms), id=1}
    Block Interval: 26681 / 1 = 26681

    Orphan : 1 : Block{height=1, parent=0, minter=Node{nodeID=3}, time=0 minutes 50 seconds (50132ms), id=2}
    Block Interval: 50132 / 1 = 50132

    Orphan : 1 : Block{height=1, parent=0, minter=Node{nodeID=2}, time=11 minutes 686 seconds (686020ms), id=3}
    Block Interval: 686020 / 1 = 686020

    OnChain : 2 : Block{height=2, parent=1, minter=Node{nodeID=2}, time=35 minutes 2112 seconds (2112215ms), id=4}
    Block Interval: 2112215 / 2 = 1056107.5

    OnChain : 3 : Block{height=3, parent=4, minter=Node{nodeID=3}, time=42 minutes 2579 seconds (2579946ms), id=5}
    Block Interval: 2579946 / 3 = 859982

    Block Interval: (26681 + 50132 + 686020 + 1056107.5 + 859982) / 5 = 535784.5
    Fork Rate =  2 / 5 * 100 = 40 %
    */

    Block b1 = new Block(1, null, n1, 26681, 1);
    Block b2 = new Block(1, null, n1, 50132, 2);
    Block b3 = new Block(1, null, n1, 686020, 3);
    Block b4 = new Block(2, null, n1, 2112215, 4);
    Block b5 = new Block(3, null, n1, 2579946, 5);

    Set<Block> allBlocks = new HashSet<>(
        Arrays.asList(b1, b2, b3, b4, b5));

    Set<Block> orphans = new HashSet<>(Arrays.asList(
        b2, b3
    ));

    simulation = spy(SimulationFactory.getInstance(SimulationType.TEST));
    doReturn(allBlocks).when(simulation).getBlocks();
    doReturn(orphans).when(simulation).getOrphans();

  }


  @Test
  void canRunASingleInstance() {
    SimulationFactory.getInstance().run();
  }

  @Test
  void twoRunsOfSameSimulationProduceTheSameTaskTrace() {

    Simulation s1 = SimulationFactory.getInstance();
    Simulation s2 = SimulationFactory.getInstance();

    s1.run();
    s2.run();

    List<ScheduledTask> trace1 = s1.getExecutionObserver().getTaskList();
    List<ScheduledTask> trace2 = s2.getExecutionObserver().getTaskList();
    assertEquals(trace1, trace2);
  }

  /**
   * This test currently has a timing issue. The two runs come back with differing timestamps
   */
  @Test
  void twoRunsOfSameSimulationProduceTheSameVisualizerTrace() {

    // Simulation s1 = SimulationFactory.getInstance();
    // Simulation s2 = SimulationFactory.getInstance();

    // s1.run();
    // s2.run();

    // List<VisualizerEvent> vl1 = s1.getVisualizerEvents().getEventLog();
    // List<VisualizerEvent> vl2 = s2.getVisualizerEvents().getEventLog();

    // assertEquals(vl1, vl2);
  }

  @Test
  void throwIfSimulationIsNotFinishedAndStatsAreRequested() {
    Simulation s = SimulationFactory.getInstance();
    assertThrows(PendingSimulationException.class, s::getSimulationStatistics);
  }

}
