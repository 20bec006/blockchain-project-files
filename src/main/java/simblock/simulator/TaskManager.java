package simblock.simulator;

import lombok.AllArgsConstructor;
import simblock.task.interfaces.ITask;

@AllArgsConstructor
public class TaskManager extends TimerCarrier {

  public TaskManager(Timer timer) {
    super(timer);
  }

  public void putTask(ITask t) {
    super.getTimer().putTask(t);

  }

  public void removeTask(ITask t) {
    super.getTimer().removeTask(t);
  }


}
