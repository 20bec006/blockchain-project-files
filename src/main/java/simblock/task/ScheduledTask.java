package simblock.task;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import simblock.task.interfaces.ITask;

/**
 * Represents a {@link ITask} that is scheduled to be executed.
 */
@EqualsAndHashCode
@ToString
public class ScheduledTask implements Comparable<ScheduledTask> {
  private final ITask task;
  private final long scheduledTime;

  /**
   * Instantiates a new ScheduledTask.
   *
   * @param task          - the task to be executed
   * @param scheduledTime - the simulation time at which the task is to be
   *                      executed
   */
  public ScheduledTask(ITask task, long scheduledTime) {
    this.task = task;
    this.scheduledTime = scheduledTime;
  }

  /**
   * Gets the task.
   *
   * @return the {@link ITask} instance
   */
  public ITask getTask() {
    return this.task;
  }

  /**
   * Gets the scheduled time at which the task is to be executed.
   *
   * @return the scheduled time
   */
  public long getScheduledTime() {
    return this.scheduledTime;
  }

  /**
   * Compares the two scheduled tasks.
   *
   * @param o other task
   * @return 1 if self is executed later, 0 if concurrent and -1 if self is to be
   *         executed before.
   */
  public int compareTo(ScheduledTask o) {
    if (this.equals(o)) {
      return 0;
    }
    int order = Long.signum(this.scheduledTime - o.scheduledTime);
    if (order != 0) {
      return order;
    }
    order = System.identityHashCode(this) - System.identityHashCode(o);
    return order;
  }
}
