package pojo.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import visualizer.VisualizerEvent;

@JsonPropertyOrder({"kind", "content"})
@Getter
@EqualsAndHashCode
@ToString
public class RemoveLink implements VisualizerEvent {

  private String kind;
  private Content content;

  // {"kind":"remove-link","content":{"timestamp":0,"begin-node-id":4,"end-node-id":10}}
  public RemoveLink(long timestamp, int beginNodeID, int endNodeID) {
    this.kind = "remove-link";
    this.content = new Content(timestamp, beginNodeID, endNodeID);
  }

  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  @ToString
  private class Content {
    private long timestamp;
    @JsonProperty("begin-node-id")
    private int beginNodeID;
    @JsonProperty("end-node-id")
    private int endNodeID;

  }
}
