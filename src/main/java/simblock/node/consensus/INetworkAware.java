package simblock.node.consensus;

import java.util.List;
import simblock.node.AbstractNode;

public interface INetworkAware {
  List<AbstractNode> getSimulatedNodes();

  long getTargetInterval();

}
