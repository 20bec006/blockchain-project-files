package pojo.events;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import pojo.events.helpers.EventTester;

class AddLinkTest extends EventTester {

  @Test
  void serialize() throws IOException {
    // {"kind":"add-link","content":{"timestamp":0,"begin-node-id":4,"end-node-id":10}}
    this.event = new AddLink(0, 4, 10);
    this.simpleClassName = this.getClass().getSimpleName();
    test();
  }
}