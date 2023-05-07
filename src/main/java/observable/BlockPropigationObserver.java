package observable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.SneakyThrows;
import observable.interfaces.IBlockPropagationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simblock.block.Block;
import simblock.node.AbstractNode;

public class BlockPropigationObserver implements IBlockPropagationObserver {

  private final URI outDirectoryUri;

  private static final Logger logger = LoggerFactory.getLogger(BlockPropigationObserver.class);
  
  /**
   * Max size the list of observed blocks is allowed to grow to before flushing.
   */
  private final int maxObservedBlocksListSize = 10;

  private final int maxBlockSightingsListSize = 10000;

  /**
   * A list of observed {@link Block} instances.
   */
  private final ArrayList<Block> observedBlocks = new ArrayList<>();

  /**
   * List of the blocks seens by a node and how long it took to receive the block.
   */
  private final Set<BlockSighting> blockSightings;

  /**
   * A list of observed block propagation times. The map key represents the id of the node that
   * has seen the block, the value represents the difference between the current time and the block minting
   * time, effectively recording the absolute time it took for a node to witness the block.
   */
  @Getter
  private final ArrayList<LinkedHashMap<Integer, Long>> observedPropagations =
      new ArrayList<>();

  /**
   * Constructor, create a new block propigation observer.
   */
  public BlockPropigationObserver(URI outDirectoryUri) {
    super();
    
    this.outDirectoryUri = outDirectoryUri;

    File outFile = new File(outDirectoryUri.resolve("./blockSightings.txt"));
    outFile.delete();
    
    // Default method from interface
    this.blockSightings = new TreeSet<>(this.getComparator());
  }
  
  /**
   * Handle the arrival of a new block. For every observed block, propagation information is
   * updated, and for a new block propagation information is created.
   *
   * @param block the block
   * @param node  the node
   */
  private void observeBlockArrival(Block block, AbstractNode node, long currentTime) {

    // If block is already seen by any node
    if (observedBlocks.contains(block)) {
      // Get the propagation information for the current block
      LinkedHashMap<Integer, Long> propagation = observedPropagations.get(
          observedBlocks.indexOf(block)
      );
      // Update information for the new block
      propagation.put(node.getNodeID(), currentTime - block.getTime());
    } else {
      // If the block has not been seen by any node and there is no memory allocated
      if (observedBlocks.size() > maxObservedBlocksListSize) {
        // After the observed blocks limit is reached, log and remove old blocks by FIFO principle
        printPropagation(observedBlocks.get(0), observedPropagations.get(0));
        observedBlocks.remove(0);
        observedPropagations.remove(0);
      }
      // If the block has not been seen by any node and there is additional memory
      LinkedHashMap<Integer, Long> propagation = new LinkedHashMap<>();
      propagation.put(node.getNodeID(), currentTime - block.getTime());
      // Record the block as seen
      observedBlocks.add(block);
      // Record the propagation time
      observedPropagations.add(propagation);
    }
  }

  /**
   * Print propagation information about the propagation of the provided block in the format:
   *
   * <p><em>node_ID, propagation_time</em>
   *
   * <p><em>propagation_time</em>: The time from when the block of the block ID is generated to
   * when the
   * node of the <em>node_ID</em> is reached.
   *
   * @param block       the block
   * @param propagation the propagation of the provided block as a list of {@link AbstractNode} IDs and
   *                    propagation times
  */
  public void printPropagation(Block block, LinkedHashMap<Integer, Long> propagation) {
    // Print block and its height
    logger.info(block + " propagation information:");
    for (Map.Entry<Integer, Long> timeEntry : propagation.entrySet()) {
      logger.info("Node ID: " + timeEntry.getKey() + "," + " Propagation time: " + timeEntry.getValue());
    }
  }

  @Override
  public void update(Block block, AbstractNode node, long currentTime) {
    this.observeBlockArrival(block, node, currentTime);
    this.markSighting(block, node);
  }

  /**
   * Print propagation information about all blocks, internally relying on
   * {@link Simulator#printPropagation(Block, LinkedHashMap)}.
   */
  public void printAllPropagation() {
    for (int i = 0; i < observedBlocks.size(); i++) {
      printPropagation(observedBlocks.get(i), observedPropagations.get(i));
    }
  }



  /**
   * Mark the block sightings so we can generate the statistics at the end of the simulation.
   * @param block the block.
   * @param node the node.
   */
  @SneakyThrows
  private void markSighting(Block block, AbstractNode node) {

    if (blockSightings.size() > maxBlockSightingsListSize) {
      flushBlockSightings();
    }

    if (block.getMinter().getNodeID() != node.getNodeID()) {
      
      // Since this gets flsuhed every 1000 blocks, the default comparer won't sort the collection properly 
      // You'll have to sort the printed list manually 
      blockSightings.add(
          BlockSighting.builder()
            .blockID(block.getId())
            .nodeID(node.getNodeID())
            .propagationTime(node.getCurrentTime() - block.getTime())
            .build()
      );
    }    
  }
  
  /**
   * Flush the list of block sightings so it doesn't become too large and cause performance issues.
   * Since this gets flsuhed every 1000 blocks, the default comparer won't sort the collection properly 
   * You'll have to sort the printed list manually 
   */
  @SneakyThrows
  private void flushBlockSightings() {

    try (FileWriter fw = new FileWriter(new File(outDirectoryUri.resolve("./blockSightings.txt")), true)) {

      try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {

        for (BlockSighting currentSighting : blockSightings) {
          pw.println(currentSighting.toCsvString());
        }
      }
    }

    // Flush it
    blockSightings.clear();
  }

  /**
   * Returns a reader for accessing the block sightings.
   * You must close this reader after use!
   * @return the reader that accesses the block sightings. Close when finished.
   */
  @SneakyThrows
  public BufferedReader getBlockSightings() {
    flushBlockSightings();
    FileReader fr = new FileReader(new File(outDirectoryUri.resolve("./blockSightings.txt")));
    BufferedReader br = new BufferedReader(fr);
    
    return br;
  }
}
