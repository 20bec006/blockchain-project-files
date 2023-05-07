package simblock.simulator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import simblock.simulator.interfaces.ITimeAware;

@AllArgsConstructor
@NoArgsConstructor
public class TimerCarrier implements ITimeAware {
  @Getter
  @Setter
  private Timer timer;

  @Override
  public long getCurrentTime() {
    return timer.getCurrentTime();
  }

}
