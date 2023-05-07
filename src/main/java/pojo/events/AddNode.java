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
public class AddNode implements VisualizerEvent {

  private String kind;
  private Content content;

  // {"kind":"add-node","content":{"timestamp":0,"node-id":1,"region-id":1}}
  public AddNode(long timestamp, int nodeID, int regionID) {
    this.kind = "add-node";
    this.content = new Content(timestamp, nodeID, regionID);
  }

  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  @ToString
  private class Content {
    private long timestamp;

    @JsonProperty("node-id")
    private int nodeID;

    @JsonProperty("region-id")
    private int regionID;

  }
}
