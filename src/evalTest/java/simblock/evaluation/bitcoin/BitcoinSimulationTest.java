package simblock.evaluation.bitcoin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import helpers.SimulationFactory;
import helpers.SimulationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simblock.Simulation;
import simblock.exceptions.PendingSimulationException;
import simblock.utils.stats.SimulationStatistics;

public class BitcoinSimulationTest {

  // BTC fork rates, SimBlock: A blockchain network simulator
  // Table 1 PARAMETERS OF GERVAIS ET AL.’S SIMULATOR
  private static final double measuredForkRate = 0.41; // percent
  private static final double gervaisForkRate = 1.85; // Percent

  private static final double targetBlockInterval = 1000 * 60 * 10; // 10 min in milliseconds
  private static final double blockIntervalTolerance = 1000 * 60; // 1 min in milliseconds

  private static final double measuredMedianBlockPropagationTime = 8.7 * 1000; // milliseconds
  private static final double gervaisMedianBlockPropagationTime = 9.42 * 1000; // milliseconds

  private static SimulationStatistics stats;

  @BeforeAll
  static void setup() throws PendingSimulationException {
    Simulation s = SimulationFactory.getInstance(SimulationType.COMPLEX);
    s.run();

    stats = s.getSimulationStatistics();
    System.out.println(stats);
  }

  @Test
  void forkRateIsSound() {
    double actualForkRate = stats.getForkRate();
    assertTrue(actualForkRate >= measuredForkRate);
    assertTrue(actualForkRate <= gervaisForkRate);
  }

  @Test
  void blockIntervalIsSound() {
    double actualBlockTime = stats.getBlockInterval();

    assertTrue(actualBlockTime >= (targetBlockInterval - blockIntervalTolerance));
    assertTrue(actualBlockTime <= (targetBlockInterval + blockIntervalTolerance));
  }

  @Test
  void medianBlockPropagationTimeIsSound() {
    double actualMedianBlockTime = stats.getMedianBlockPropagationTime();

    assertTrue(actualMedianBlockTime >= measuredMedianBlockPropagationTime);
    assertTrue(actualMedianBlockTime <= gervaisMedianBlockPropagationTime);
  }
}
