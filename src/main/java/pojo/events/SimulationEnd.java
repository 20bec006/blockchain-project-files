package pojo.events;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import visualizer.VisualizerEvent;

@Getter
@EqualsAndHashCode
@ToString
public class SimulationEnd implements VisualizerEvent {

  private String kind;
  private Content content;

  //{"kind":"simulation-end","content":{"timestamp":1841038}}]
  public SimulationEnd(long timestamp) {
    this.kind = "simulation-end";
    this.content = new Content(timestamp);
  }

  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  @ToString
  private class Content {
    private long timestamp;
  }

}
