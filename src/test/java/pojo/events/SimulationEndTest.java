package pojo.events;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import pojo.events.helpers.EventTester;

class SimulationEndTest extends EventTester {

  @Test
  void serialize() throws IOException {
    this.event = new SimulationEnd(2016493);
    this.simpleClassName = this.getClass().getSimpleName();
    test();
  }
}