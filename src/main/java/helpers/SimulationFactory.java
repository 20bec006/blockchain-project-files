package helpers;

import simblock.Simulation;

/**
 * The type Simulation factory simplifies simulation generation and allows for experiment rerun.
 */
public class SimulationFactory {

  /**
   * Gets the simulation instance.
   *
   * @param type the type
   * @return the instance
   */
  public static Simulation getInstance(SimulationType type) {
    switch (type) {
      case COMPLEX:
        return complexSimulation();
      case SIMPLE:
        return simpleSimulation();
      case BTC_6000_NODES:
        return bitcoinMassive();
      case SELFISH_MINING:
        return selfishMining();
      case SIMPLE_SELFISH_MINING:
        return simpleSelfishMining();
      case TEST:
        return testSimulation();
      default:
        return defaultSimulation();
    }
  }

  /**
   * Gets the default instance.
   *
   * @return the instance
   */
  public static Simulation getInstance() {
    return getInstance(SimulationType.DEFAULT);
  }

  /**
   * Simple simulation.
   *
   * @return the simulation
   */
  private static Simulation simpleSimulation() {
    SimulationSettings seed = new SimulationSettings();
    seed.numOfNodes = 5;
    seed.interval = 1000 * 60 * 10; // 1000 * 60 * (10 milliseconds) = 10 minutes
    seed.endBlockHeight = 5;
    seed.simulationType = SimulationType.SIMPLE;

    return new Simulation(seed);
  }

  private static Simulation defaultSimulation() {

    SimulationSettings seed = new SimulationSettings();
    seed.numOfNodes = 10;
    seed.interval = 1000 * 60 * 10; // 1000 * 60 * (10 milliseconds) = 10 minutes
    seed.endBlockHeight = 50;
    seed.simulationType = SimulationType.DEFAULT;

    return new Simulation(seed);
  }


  private static Simulation complexSimulation() {

    SimulationSettings seed = new SimulationSettings();
    seed.numOfNodes = 600;
    seed.interval = 1000 * 60 * 10; // 1000 * 60 * (10 milliseconds) = 10 minutes
    seed.endBlockHeight = 100;
    seed.simulationType = SimulationType.COMPLEX;

    return new Simulation(seed);
  }

  private static Simulation bitcoinMassive() {

    SimulationSettings seed = new SimulationSettings();
    seed.numOfNodes = 6000;
    seed.interval = 1000 * 60 * 10; // 1000 * 60 * (10 milliseconds) = 10 minutes
    seed.endBlockHeight = 10000;
    seed.simulationType = SimulationType.BTC_6000_NODES;

    return new Simulation(seed);

  }

  /**
   * Returns a simulation that has a selfish mining attack.
   * @return a selfish mining simulation
   */
  private static Simulation selfishMining() {

    SelfishMiningSimulationSettings seed = new SelfishMiningSimulationSettings();
    seed.numOfNodes = 600;
    seed.interval = 1000 * 60 * 10; // 1000 * 60 * (10 milliseconds) = 10 minutes
    seed.endBlockHeight = 100;
    seed.selfishMiningPowerPercentage = 25;
    seed.simulationType = SimulationType.SELFISH_MINING;

    return new Simulation(seed);
  }

  /**
   * Returns a simple simulation that has a selfish mining attack.
   * @return a selfish mining simulation
   */
  private static Simulation simpleSelfishMining() {

    SelfishMiningSimulationSettings seed = new SelfishMiningSimulationSettings();
    seed.numOfNodes = 10;
    seed.interval = 1000 * 60 * 10; // 1000 * 60 * (10 milliseconds) = 10 minutes
    seed.endBlockHeight = 100;
    seed.selfishMiningPowerPercentage = 25;
    seed.simulationType = SimulationType.SELFISH_MINING;

    return new Simulation(seed);
  }

  /**
   * Returns a test simulation.
   * @return a test simulation
   */
  private static Simulation testSimulation() {

    SimulationSettings seed = new SimulationSettings();
    seed.numOfNodes = 10;
    seed.interval = 1000 * 60 * 10; // 1000 * 60 * (10 milliseconds) = 10 minutes
    seed.endBlockHeight = 10;
    seed.simulationType = SimulationType.TEST;

    return new Simulation(seed);
  }
}
