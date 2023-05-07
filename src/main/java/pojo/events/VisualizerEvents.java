package pojo.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import observable.interfaces.IVisualizerEventObserver;
import observable.interfaces.IVisualizerEventSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import visualizer.JsonSerializer;
import visualizer.VisualizerEvent;

/**
 * The type Event log contains a series of events transpired during a simulation instance. It is able to serialize these events.
 */
public class VisualizerEvents implements IVisualizerEventSubject {

  // The logger
  private static final Logger LOGGER = LoggerFactory.getLogger(VisualizerEvents.class);

  @Getter
  private List<VisualizerEvent> eventLog;

  private JsonSerializer serializer;
  private List<IVisualizerEventObserver> observers;

  /**
   * Instantiates a new Event log.
   */
  public VisualizerEvents() {
    this.eventLog = new LinkedList<>();
    this.serializer = new JsonSerializer();
    this.observers = new ArrayList<>();
  }

  /**
   * Adds an event to the event log.
   *
   * @param e the event to be added.
   */
  public void add(VisualizerEvent e) {

    String serializedEvent = serializer.serialize(e);
    LOGGER.info(serializedEvent);
    eventLog.add(e);
    notifyObserver(e);
  }


  @Override
  public void register(IVisualizerEventObserver o) {
    this.observers.add(o);

  }

  @Override
  public void unregister(IVisualizerEventObserver o) {
    this.observers.remove(o);

  }

  @Override
  public void notifyObserver(VisualizerEvent e) {
    for (IVisualizerEventObserver o : observers) {
      o.update(e);
    }

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VisualizerEvents that = (VisualizerEvents) o;
    return eventLog.equals(that.eventLog);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventLog);
  }
}
