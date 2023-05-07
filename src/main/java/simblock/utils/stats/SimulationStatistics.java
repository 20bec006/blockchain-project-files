package simblock.utils.stats;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class SimulationStatistics {

  /**
   * The measured block interval in milliseconds.
   */
  private double blockInterval;

  /**
   * The median of the propagation of every generated block to every simulated node.
   */
  private double medianBlockPropagationTime;

  /**
   * The percentage of orphaned blocks in the amount of all blocks.
   */
  private double forkRate;


  @Override
  public String toString() {
    return "SimulationStatistics{"
        + "blockInterval=" + TimeUnit.MILLISECONDS.toMinutes((long) blockInterval) + " minutes"
        + ", medianBlockPropagationTime=" + TimeUnit.MILLISECONDS.toSeconds((long) medianBlockPropagationTime) + " seconds"
        + ", forkRate=" + forkRate + " percent"
        + '}';
  }
}
