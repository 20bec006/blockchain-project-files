package observable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Getter
public class BlockSighting {
  private int nodeID;
  private int blockID;
  private long propagationTime;

  public static final int PROPIGATION_INDEX = 2;

  public String toCsvString() {
    return nodeID + ", " + blockID + ", " + propagationTime;
  }
}