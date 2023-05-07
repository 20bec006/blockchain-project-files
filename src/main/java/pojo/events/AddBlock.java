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
public class AddBlock implements VisualizerEvent {

  private String kind;
  private Content content;

  public AddBlock(long timestamp, int nodeID, int blockID) {
    this.kind = "add-block";
    this.content = new Content(timestamp, nodeID, blockID);
  }

  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  @ToString
  private class Content {
    private long timestamp;
    @JsonProperty("node-id")
    private int nodeID;
    @JsonProperty("block-id")
    private int blockID;

  }
}