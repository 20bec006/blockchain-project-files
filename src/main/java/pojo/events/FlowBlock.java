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
public class FlowBlock implements VisualizerEvent {

  private String kind;
  private Content content;

  // {"kind":"flow-block","content":{"transmission-timestamp":981676,"reception-timestamp":982025,"begin-node-id":6,"end-node-id":9,"block-id":1}}
  public FlowBlock(long transmissionTimestamp, long receptionTimestamp, int beginNodeID, int endNodeID, int blockID) {
    this.kind = "flow-block";
    this.content = new Content(transmissionTimestamp, receptionTimestamp, beginNodeID, endNodeID, blockID);
  }

  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  @ToString
  private class Content {
    @JsonProperty("transmission-timestamp")
    private long transmissionTimestamp;

    @JsonProperty("reception-timestamp")
    private long receptionTimestamp;

    @JsonProperty("begin-node-id")
    private int beginNodeID;

    @JsonProperty("end-node-id")
    private int endNodeID;

    @JsonProperty("block-id")
    private int blockID;

  }
}
