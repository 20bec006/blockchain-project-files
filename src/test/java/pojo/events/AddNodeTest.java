package pojo.events;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import pojo.events.helpers.EventTester;

class AddNodeTest extends EventTester {

  @Test
  void serialize() throws IOException {
    this.event = new AddNode(0, 1, 1);
    this.simpleClassName = this.getClass().getSimpleName();
    test();
  }
}