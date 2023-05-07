package simblock;

//import static simblock.settings.SimulationConfiguration.AVERAGE_MINING_POWER;
//import static simblock.settings.SimulationConfiguration.END_BLOCK_HEIGHT;
//import static simblock.settings.SimulationConfiguration.INTERVAL;
//import static simblock.settings.SimulationConfiguration.NUM_OF_NODES;
//import static simblock.settings.SimulationConfiguration.STDEV_OF_MINING_POWER;

import helpers.SelfishMiningSimulationSettings;
import helpers.SimulationSettings;
import helpers.SimulationType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import lombok.Getter;
import lombok.SneakyThrows;
import observable.ArchiveExecutionObserver;
import observable.BlockPropigationObserver;
import observable.BlockSighting;
import observable.VisualizerExecutionObserver;
import observable.interfaces.IBlockPropagationObserver;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.events.AddNode;
import pojo.events.SimulationEnd;
import pojo.events.VisualizerEvents;
import simblock.block.Block;
import simblock.block.ProofOfWorkBlock;
import simblock.exceptions.PendingSimulationException;
import simblock.node.AbstractNode;
import simblock.node.NodeFactory;
import simblock.node.NodeType;
import simblock.node.SelfishMiningNode;
import simblock.simulator.Network;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;
import simblock.utils.probability.ProbabilityUtils;
import simblock.utils.stats.SimulationStatistics;
import visualizer.VisualizerEvent;
import visualizer.VisualizerJsonWriter;

public class Simulation implements Runnable {

  /**
   * The initial simulation time.
   */
  private long simulationTime;

  /**
   * Output path.
   */
  private URI outDirectoryUri;

  private final Logger logger = LoggerFactory.getLogger(Simulator.class);

  @Getter
  private VisualizerEvents visualizerEvents;
  private NodeFactory nodeFactory;
  private VisualizerJsonWriter writer;
  private Timer timer;
  @Getter
  private ArchiveExecutionObserver executionObserver;
  private IBlockPropagationObserver blockPropagationObserver;

  private SimulationSettings simulationSettings;

  private int averageOrphansSize;
  private boolean simulationExecuted;

  @Getter
  private Set<Block> orphans;

  /**
   * Every block (orphan and canonical) seen in the simulation.
   */
  @Getter
  private Set<Block> blocks;

  /**
   * URI to the graph directory.
   */
  private URI graphDirectoryUri;
  private Simulator simulator;
  private Network network;
  private SimulationStatistics simulationStatistics;

  /**
   * Abstraction and encapsulation of a simulation.
   *
   * @param simSettings the settings to be used for reproducibility
   */
  public Simulation(SimulationSettings simSettings) {

    // Set the simulation seed and use it to generate a random instance for
    // deterministic runs
    this.simulationSettings = simSettings;

    // A random instance shared in the simulation
    Random random = new Random(SimulationSettings.SIMULATION_SEED);

    // Simulation starts at 0
    this.simulationTime = 0;

    // Network setup
    this.network = new Network(random);

    // Set the output directories and files
    this.outDirectoryUri = Paths.get("dist/output").toUri();
    this.graphDirectoryUri = outDirectoryUri.resolve("./graph/");

    // Set the transient data structures for the simulation
    this.visualizerEvents = new VisualizerEvents();
    this.blockPropagationObserver = new BlockPropigationObserver(this.outDirectoryUri);
    this.writer = new VisualizerJsonWriter();
    this.timer = new Timer();

    this.simulator = new Simulator(timer, simulationSettings.interval);

    this.nodeFactory = new NodeFactory(blockPropagationObserver,  
                                       visualizerEvents,   
                                       timer, 
                                       simulator,  
                                       random,
                                       new AutoIncrementingGenerator(), 
                                       network, 
                                       SimulationSettings.STDEV_OF_MINING_POWER,
                                       SimulationSettings.AVERAGE_MINING_POWER, 
                                       SimulationSettings.BLOCK_SIZE, 
                                       SimulationSettings.PROCESSING_TIME);
    this.executionObserver = new ArchiveExecutionObserver();



    // Set the internal state
    this.averageOrphansSize = 0;
    this.simulationExecuted = false;
    // No blocks seen until now
    this.blocks = new HashSet<>();
    // No orphans seen until now
    this.orphans = new HashSet<>();

  }

  // https://projectlombok.org/features/SneakyThrows
  @SneakyThrows
  @Override
  public void run() {

    createFile(outDirectoryUri);
    createFile(graphDirectoryUri);

    final long start = System.currentTimeMillis();
  
    // Simulation execution trace
    timer.register(executionObserver);

    // Simulation execution trace
    timer.register(new VisualizerExecutionObserver(visualizerEvents, timer));

    // Register JSON writer
    visualizerEvents.register(writer);

    // start json format
    writer.open();

    // Log regions
    network.printRegion();

    // Setup network
    this.constructNetworkWithAllNodes(simulationSettings.numOfNodes);

    // Starts the simulation by designating a random node to mint the genesis block
    // (nodes in list are randomized)
    AbstractMintingTask genesisMint = simulator.getSimulatedNodes().get(0).getConsensusAlgo().genesisBlockTask();
    genesisMint.run();

    // Initial block height, we stop at END_BLOCK_HEIGHT
    int currentBlockHeight = 1;

    // StopWatch stopwatch = StopWatch.createStarted();
    // try (FileWriter fw = new FileWriter(new File(outDirectoryUri.resolve("./blockTimes.txt")), false)) {
    //try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {

    // Iterate over tasks and handle
    while (timer.getTask() != null) {
      if (timer.getTask() instanceof AbstractMintingTask) {
        AbstractMintingTask task = (AbstractMintingTask) timer.getTask();
        if (task.getParent().getHeight() == currentBlockHeight) {
          currentBlockHeight++;
          //printBlockTimes(pw, currentBlockHeight, stopwatch.getTime(TimeUnit.SECONDS));

        }
        if (currentBlockHeight > simulationSettings.endBlockHeight) {
          break;
        }
        // Log every 100 blocks and at the second block
        // TODO use constants here
        if (currentBlockHeight % 100 == 0 || currentBlockHeight == 2) {
          this.writeGraph(currentBlockHeight);
        }
      }
      // Execute task
      timer.runTask();
    }

    //}
    //}

    logEndOfSimulationInfo();

    VisualizerEvent simulationEnd = new SimulationEnd(timer.getCurrentTime());

    visualizerEvents.add(simulationEnd);

    // end json format
    writer.close();

    long end = System.currentTimeMillis();
    simulationTime += end - start;
    // Log simulation time in milliseconds
    logger.info("Simulation time: " + simulationTime + "ms");

    this.simulationExecuted = true;

    printSimulationVerification();
  }

  /**
   * Prints the simulation verification.
   */
  public void printBlockTimes(PrintWriter pw, int blocknumber, long time) {

    pw.println(time);
    pw.flush();
      
  }

  /**
   * Logs the data for the simulation run.
   */
  private void logEndOfSimulationInfo() {

    // Print propagation information about all blocks
    this.blockPropagationObserver.printAllPropagation();

    // Get the latest block from the first simulated node
    Block block = simulator.getSimulatedNodes().get(0).getBlock();

    // Update the list of known blocks by adding the parents of the aforementioned
    // block
    while (block.getParent() != null) {
      blocks.add(block);
      block = block.getParent();
    }

    // Gather all known orphans
    for (AbstractNode node : simulator.getSimulatedNodes()) {
      orphans.addAll(node.getOrphans());
      averageOrphansSize += node.getOrphans().size();
    }
    averageOrphansSize = averageOrphansSize / simulator.getSimulatedNodes().size();

    // Record orphans to the list of all known blocks
    blocks.addAll(orphans);

    ArrayList<Block> blockList = new ArrayList<>(blocks);

    // Sort the blocks first by time, then by hash code
    blockList.sort((a, b) -> {
      int order = Long.signum(a.getTime() - b.getTime());
      if (order != 0) {
        return order;
      }
      order = System.identityHashCode(a) - System.identityHashCode(b);
      return order;
    });

    // Log all orphans
    for (Block orphan : orphans) {
      logger.info(orphan + ":" + orphan.getHeight());
    }
    logger.info("Average orphan size: " + averageOrphansSize);

    /*
     * Log in format: ＜fork_information, block height, block ID＞ fork_information:
     * One of "OnChain" and "Orphan". "OnChain" denote block is on Main chain.
     * "Orphan" denote block is an orphan block.
     */
    // TODO move to method and use logger

    try {
      FileWriter fw = new FileWriter(new File(outDirectoryUri.resolve("./blockList.txt")), false);
      PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

      for (Block b : blockList) {
        if (!orphans.contains(b)) {
          pw.println(((ProofOfWorkBlock) b).getMlBlockString(b.getMinter() instanceof SelfishMiningNode) + ",");
          //pw.println("OnChain : " + b.getHeight() + " : " + ((ProofOfWorkBlock) b).getMlBlockString());
        } else {
          // turing this off for a second as the other block explorers don't record orphans
          //pw.println("Orphan : " + b.getHeight() + " : " + ((ProofOfWorkBlock) b).getMlBlockString());
        }
      }
      pw.close();

      logger.info("Output located at " + outDirectoryUri.resolve("./blockList.txt"));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Construct network with the provided number of nodes.
   *
   * @param numNodes the num nodes
   */
  @SneakyThrows(NotImplementedException.class)
  public void constructNetworkWithAllNodes(int numNodes) {

    long totalNetworkMiningPower = 0;
    ProbabilityUtils regionProbability = new ProbabilityUtils();
    ProbabilityUtils degreeProbability = new ProbabilityUtils();

    // Random distribution of nodes per region
    double[] regionDistribution = network.getRegionDistribution();
    regionProbability.fromPmf(regionDistribution);
    List<Integer> regionList = regionProbability.generatePopulation(numNodes, SimulationSettings.SIMULATION_SEED);

    // Random distribution of node degrees
    double[] degreeDistribution = network.getDegreeDistribution();
    degreeProbability.fromCdf(degreeDistribution);
    List<Integer> degreeList = degreeProbability.generatePopulation(numNodes, SimulationSettings.SIMULATION_SEED);

    var claimedNodeIDs = new ArrayList<Integer>();
    int randomNodeID; /* Node ids must be random in order to generate a machine learning dataset. 
                         If it's not random, it will learn incorrectly on the id  */

    for (int i = 1; i <= numNodes; i++) {

      do {
        randomNodeID = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
      } while (claimedNodeIDs.contains(randomNodeID));
      claimedNodeIDs.add(randomNodeID);
      
      // What if the contents of this for loop were a function that got passed in?
      // Could also be an interface
      // Is a lambda a bit overkill for this?
      if ((simulationSettings.simulationType == SimulationType.SELFISH_MINING
          || simulationSettings.simulationType == SimulationType.SIMPLE_SELFISH_MINING) && i == numNodes) {

        AbstractNode selfishNode = nodeFactory.createNode(randomNodeID, degreeList.get(i - 1) + 1, regionList.get(i - 1),
            NodeType.BTC_SelfishMiner);

        // According to Eyal and Sirer, the selfish mining threshold should be 25% of
        // the total network mining power
        // Set the selfish mining power equal to 25% of the total network hash rate
        SelfishMiningSimulationSettings selfishSettings = (SelfishMiningSimulationSettings) this.simulationSettings;
        int honestMinerMiningPercentage = 100 - selfishSettings.selfishMiningPowerPercentage;

        double twentyFivePercentOfHashRate = ((totalNetworkMiningPower * 100) / honestMinerMiningPercentage)
            - totalNetworkMiningPower;
        selfishNode.setMiningPower((long) twentyFivePercentOfHashRate);
        simulator.addNode(selfishNode);

      } else {
        // Each node gets assigned a region, its degree, mining power, routing table and
        // consensus algorithm
        AbstractNode node = nodeFactory.createNode(randomNodeID, degreeList.get(i - 1) + 1, regionList.get(i - 1),
            NodeType.BTC);

        // Add the node to the list of simulated nodes
        simulator.addNode(node);

        totalNetworkMiningPower += node.getMiningPower();
      }

      VisualizerEvent addNode = new AddNode(0, randomNodeID, regionList.get(i - 1));

      visualizerEvents.add(addNode);

    }

    List<AbstractNode> bootstrapNodes = simulator.getSimulatedNodes();
    // Link newly generated nodes
    for (AbstractNode node : bootstrapNodes) {
      node.joinNetwork(bootstrapNodes, nodeFactory.getRandom());
    }
  }

  /**
   * Network information when block height is <em>blockHeight</em>, in format:
   * <p>
   * <em>nodeID_1</em>, <em>nodeID_2</em>
   * </p>
   * meaning there is a connection from nodeID_1 to right nodeID_1.
   * 
   * @param blockHeight the index of the graph and the current block height
   */

  public void writeGraph(int blockHeight) {
    try {
      FileWriter fw = new FileWriter(new File(graphDirectoryUri.resolve(blockHeight + ".txt")), false);
      PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

      for (int index = 1; index <= simulator.getSimulatedNodes().size(); index++) {
        AbstractNode node = simulator.getSimulatedNodes().get(index - 1);
        for (int i = 0; i < node.getNeighbors().size(); i++) {
          AbstractNode neighbor = node.getNeighbors().get(i);
          pw.println(node.getNodeID() + " " + neighbor.getNodeID());
        }
      }
      pw.close();

    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  /**
   * Creates files for logging output.
   * 
   * @param fileUri file uri
   */
  private void createFile(URI fileUri) {
    File outFile = new File(fileUri);

    if (!outFile.exists()) {
      if (outFile.mkdirs()) {
        logger.info(fileUri + " has been created");
      } else {
        logger.error("Error creating " + fileUri);
      }
    }
  }

  /**
   * Prints the simulation verification.
   * 
   * @throws PendingSimulationException throws if simulation not finished
   */
  @SneakyThrows(PendingSimulationException.class)
  public void printSimulationVerification() throws PendingSimulationException {

    try {
      var statistics = getSimulationStatistics();

      try (FileWriter fw = new FileWriter(new File(outDirectoryUri.resolve("./verification.txt")), false)) {

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {
          pw.print(statistics.toString());
        }
      }

      logger.info("Verification located at " + outDirectoryUri.resolve("./verification.txt"));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Gets the statistics gathered from the executed simulation.
   *
   * @return the {@link SimulationStatistics} instance
   * @throws PendingSimulationException if simulation has not been run yet.
   */
  @SneakyThrows(IOException.class)
  public SimulationStatistics getSimulationStatistics() throws PendingSimulationException {
    if (!simulationExecuted) {
      throw new PendingSimulationException();
    }

    // Lazy load this
    if (simulationStatistics != null) {
      return simulationStatistics;
    }

    DoubleSummaryStatistics dss = getBlocks().stream()
        .mapToDouble((block) -> (double) block.getTime() / block.getHeight()).summaryStatistics();

    double forkRatePercentage = (double) getOrphans().size() / getBlocks().size() * 100; // TODO: understand this

    BufferedReader reader = blockPropagationObserver.getBlockSightings();

    List<Double> propagationTimes = new ArrayList<Double>();
    try {
      String line;
      while ((line = reader.readLine()) != null) {  
        String[] blockSightingParts = line.split(",");
        propagationTimes.add(Double.parseDouble(blockSightingParts[BlockSighting.PROPIGATION_INDEX]));
      }

    } finally {
      reader.close();
    }

    double[] blockPropArray = new double[propagationTimes.size()];
    for (int i = 0; i < propagationTimes.size(); i++) {
      blockPropArray[i] = propagationTimes.get(i);
    }
    
    this.simulationStatistics = SimulationStatistics.builder()
                                                    .blockInterval(dss.getAverage())
                                                    .medianBlockPropagationTime(
                                                      new Median().evaluate(blockPropArray)
                                                    )
                                                    .forkRate(forkRatePercentage).build();

    return this.simulationStatistics;
  }
}
