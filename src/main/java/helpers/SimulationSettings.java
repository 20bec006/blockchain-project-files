package helpers;

public class SimulationSettings {

  public static final int SIMULATION_SEED = 0;
  
  /**
   * The average mining power of each node. Mining power corresponds to Hash Rate in Bitcoin, and
   * is the number of mining (hash calculation) executed per millisecond.
   */
  public static final int AVERAGE_MINING_POWER = 400000;

  /**
   * Block size. (unit: byte).
   */
  public static final long BLOCK_SIZE = 535000; //6110;//8000;//535000;//0.5MB

  /**
   * The mining power of each node is determined randomly according to the normal distribution
   * whose average is AVERAGE_MINING_POWER and standard deviation is STDEV_OF_MINING_POWER.
   */
  public static final int STDEV_OF_MINING_POWER = 100000;

  /**
   * Processing time of tasks expressed in milliseconds.
   */
  public static final long PROCESSING_TIME = 2;

  /**
   * The type of the simulation.
   */
  public SimulationType simulationType;

  /**
   * Number of nodes to generate in the simulation.
   */
  public int numOfNodes;

  /**
   * The average interval between poisson distributed blocks.
   */
  public long interval; // 1000 * 60 * (10 milliseconds) = 10 minutes

  /**
   * Number of blocks to generate during the simulation.
   */
  public int endBlockHeight;
}
