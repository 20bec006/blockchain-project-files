package pojo.events;

import lombok.Getter;
import simblock.simulator.Timer;
import simblock.simulator.TimerCarrier;
import visualizer.VisualizerEvent;

@Getter
public class VisualizerEventEmitter extends TimerCarrier {
  private VisualizerEvents log;


  /**
   * The entity emitting new events to the visualizer log.
   *
   * @param log   the log to which the events are to be emitted
   * @param timer the simulation timer used to create timestamps.
   */
  public VisualizerEventEmitter(VisualizerEvents log, Timer timer) {
    super(timer);

    this.log = log;
  }

  /**
   * Adds a new {@link VisualizerEvent} to the log, if there is a log registered (is not null).
   *
   * @param e the event instance to be added.
   */
  public void emit(VisualizerEvent e) {
    if (log != null) {
      this.log.add(e);
    }
  }

  public void emitAddLink(int fromNodeID, int endNodeID) {
    this.emit(new AddLink(getCurrentTime(), fromNodeID, endNodeID));
  }

  public void emitRemoveLink(int fromNodeID, int endNodeID) {
    this.emit(new RemoveLink(getCurrentTime(), fromNodeID, endNodeID));
  }
}
