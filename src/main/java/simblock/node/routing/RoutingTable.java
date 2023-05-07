package simblock.node.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import simblock.node.AbstractNode;

public interface RoutingTable {
  
  ArrayList<AbstractNode> getOutbound();

  ArrayList<AbstractNode> getInbound();

  boolean addNeighbor(AbstractNode neighborNode);

  boolean removeNeighbor(AbstractNode neighborNode);

  boolean addInbound(AbstractNode from);

  boolean removeInbound(AbstractNode from);
  
  void initTable(List<AbstractNode> bootstrapNodes, Random random);
}
