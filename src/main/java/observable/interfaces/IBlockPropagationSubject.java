package observable.interfaces;

import simblock.block.Block;
import simblock.node.AbstractNode;

public interface IBlockPropagationSubject {
  void register(IBlockPropagationObserver o);

  void unregister(IBlockPropagationObserver o);

  void notifyObservers(Block block, AbstractNode node);
}
