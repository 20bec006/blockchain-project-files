package observable.interfaces;

import simblock.task.ScheduledTask;

public interface IExecutionSubject {
  void register(IExecutionObserver o);

  void unregister(IExecutionObserver o);

  void notifyObservers(ScheduledTask t);
}
