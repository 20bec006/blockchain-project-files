package pojo.events.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pojo.events.helpers.EventReader.read;

import java.io.IOException;
import visualizer.JsonSerializer;
import visualizer.VisualizerEvent;

public abstract class EventTester {
  public VisualizerEvent event;
  public String simpleClassName;

  public void test() throws IOException {
    assertEquals(read(simpleClassName), new JsonSerializer().serialize(event));

  }
}
