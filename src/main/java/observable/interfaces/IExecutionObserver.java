package observable.interfaces;

import simblock.task.ScheduledTask;

public interface IExecutionObserver {
  void update(ScheduledTask t);
}
