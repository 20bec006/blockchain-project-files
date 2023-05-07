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

package simblock.task;


import java.util.UUID;
import simblock.node.AbstractNode;
import simblock.task.interfaces.ITask;

/**
 * The type Abstract message task.
 */
public abstract class AbstractMessageTask implements ITask {

  private final UUID taskID;

  /**
   * The sending entity.
   */
  private final AbstractNode from;
  /**
   * The receiving entity.
   */
  private final AbstractNode to;

  private final long duration;

  /**
   * Instantiates new AbstractMessageTask.
   *
   * @param from     the origin of the message
   * @param to       the endpoint of the message
   * @param duration the duration, consisting out of latency and duration
   */
  public AbstractMessageTask(AbstractNode from, AbstractNode to, long duration) {
    this.from = from;
    this.to = to;
    this.duration = duration;
    this.taskID = UUID.randomUUID();
  }


  /**
   * Get the sending node.
   *
   * @return the <em>from</em> node
   */
  public AbstractNode getFrom() {
    return this.from;
  }

  /**
   * Get the receiving node.
   *
   * @return the <em>to</em> node
   */
  public AbstractNode getTo() {
    return this.to;
  }

  /**
   * Get the message delay with regards to respective regions.
   *
   * @return the message sending interval
   */
  public long getDuration() {
    return this.duration;
  }

  
  /**
   * Receive message at the <em>to</em> side.
   */
  public void run() {
    // TODO 50% of the processing time is taken up here
    this.to.receiveMessage(this);
  }

  /**
   * Get the task ID.
   *
   * @return the block
   */
  public UUID getTaskID() {
    return this.taskID;
  }
}