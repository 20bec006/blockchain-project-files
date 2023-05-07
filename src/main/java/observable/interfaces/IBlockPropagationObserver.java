package observable.interfaces;

import java.io.BufferedReader;
import java.util.Comparator;
import observable.BlockSighting;
import simblock.block.Block;
import simblock.node.AbstractNode;

public interface IBlockPropagationObserver {
  
  void update(Block block, AbstractNode node, long currentTime);

  void printAllPropagation();

  BufferedReader getBlockSightings();

  default Comparator<BlockSighting> getComparator() {
    return Comparator.comparing(BlockSighting::getBlockID).thenComparing(BlockSighting::getNodeID).thenComparing(BlockSighting::getPropagationTime);
  }
}
