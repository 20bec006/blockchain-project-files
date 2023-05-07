package observable;

import observable.interfaces.IExecutionObserver;
import pojo.events.FlowBlock;
import pojo.events.VisualizerEventEmitter;
import pojo.events.VisualizerEvents;
import simblock.simulator.Timer;
import simblock.task.BlockMessageTask;
import simblock.task.ScheduledTask;
import visualizer.VisualizerEvent;

public class VisualizerExecutionObserver extends VisualizerEventEmitter implements IExecutionObserver {


  public VisualizerExecutionObserver(VisualizerEvents log, Timer timer) {
    super(log, timer);
  }

  @Override
  public void update(ScheduledTask t) {

    if (t.getTask() instanceof BlockMessageTask) {
      BlockMessageTask bmt = (BlockMessageTask) t.getTask();

      VisualizerEvent flowBlock =
          new FlowBlock(getCurrentTime() - bmt.getDuration(),
              getCurrentTime(),
              bmt.getFrom().getNodeID(),
              bmt.getTo().getNodeID(),
              bmt.getBlock().getId()
          );

      this.getLog().add(flowBlock);

    }


  }
}
