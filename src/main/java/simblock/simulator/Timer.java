/*
 * Copyright 2019 Distributed Systems Group
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simblock.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import lombok.Setter;
import observable.interfaces.IExecutionObserver;
import observable.interfaces.IExecutionSubject;
import simblock.task.ScheduledTask;
import simblock.task.interfaces.ITask;


/**
 * The type Timer schedules the execution of simulation tasks stored in a Future Event List (FEL)
 * . Each {@link ITask}
 * can be scheduled for execution. Tasks that have been run get removed from the FEL.
 */
public class Timer implements IExecutionSubject {

  /**
   * A sorted queue of scheduled tasks.
   */
  private final PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>();

  /**
   * Getter for returning the task queue.
   * This returns a copy of the queue since the queue is closed to modification 
   * unless accessed through the approved methods.
   * This is needed for unit tests
   * @see GetTask 
   * @see RemoveTask
   * @return Copy of the task queue
   */
  public PriorityQueue<ScheduledTask> getTaskQueueCopy() {
    return new PriorityQueue<>(taskQueue);
  }

  private List<IExecutionObserver> executionObservers;

  public Timer() {
    this.executionObservers = new ArrayList<>();
  }

  /**
   * A map containing a mapping of all tasks to their ScheduledTask counterparts. When
   * executed, the key - value
   * pair is to be removed from the mapping.
   */
  private final Map<UUID, ScheduledTask> taskMap = new HashMap<>();
  /**
   * Initial simulation time in milliseconds.
   */
  @Setter
  private long currentTime = 0L;

  @Override
  public void register(IExecutionObserver o) {
    executionObservers.add(o);

  }

  @Override
  public void unregister(IExecutionObserver o) {
    executionObservers.remove(o);

  }

  @Override
  public void notifyObservers(ScheduledTask t) {
    // This feeds a filter which feeds visualizer event log
    for (IExecutionObserver o : executionObservers) {
      o.update(t);
    }

  }

  
  /**
   * Runs a {@link ScheduledTask}.
   */
  public void runTask() {
    // If there are any tasks
    if (taskQueue.size() > 0) {
      // Get the next ScheduledTask
      ScheduledTask currentScheduledTask = taskQueue.poll();
      ITask currentTask = currentScheduledTask.getTask();
      currentTime = currentScheduledTask.getScheduledTime();

      // Remove the task from the mapping of all tasks
      taskMap.remove(currentTask.getTaskID(), currentScheduledTask);
      // Execute
      currentTask.run();

      //notifyObservers(currentScheduledTask);

    }
  }

  /**
   * Remove task from the mapping of all tasks and from the execution queue.
   * @param task the task to be removed
   */
  public void removeTask(ITask task) {
    if (taskMap.containsKey(task.getTaskID())) {
      ScheduledTask scheduledTask = taskMap.get(task.getTaskID());
      taskQueue.remove(scheduledTask);
      taskMap.remove(task.getTaskID(), scheduledTask);
    }
  }

  /**
   * Get the {@link ITask} from the execution queue to be executed next.
   *
   * @return the task from the queue or null if task queue is empty.
   */
  public ITask getTask() {

    if (taskQueue.size() > 0) {
      ScheduledTask currentTask = taskQueue.peek();
      return currentTask.getTask();
    } else {
      return null;
    }
  }

  /**
   * Schedule task to be executed at the current time incremented by the task duration.
   *
   * @param task the task
   */
  public void putTask(ITask task) {
    ScheduledTask scheduledTask = new ScheduledTask(task, currentTime + task.getDuration());
    taskMap.put(task.getTaskID(), scheduledTask);
    taskQueue.add(scheduledTask);
  }

  /**
   * Schedule task to be executed at the provided absolute timestamp.
   *
   * @param task the task
   * @param time the time in milliseconds
   */
  public void putTaskAbsoluteTime(ITask task, long time) {
    ScheduledTask scheduledTask = new ScheduledTask(task, time);
    taskMap.put(task.getTaskID(), scheduledTask);
    taskQueue.add(scheduledTask);
  }

  /**
   * Get current time in milliseconds.
   *
   * @return the time
   */
  public long getCurrentTime() {
    return currentTime;
  }
}
