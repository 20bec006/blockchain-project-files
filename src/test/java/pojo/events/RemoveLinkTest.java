package pojo.events;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import pojo.events.helpers.EventTester;

class RemoveLinkTest extends EventTester {


  @Test
  // {"kind":"remove-link","content":{"timestamp":0,"begin-node-id":4,"end-node-id":10}}
  void serialize() throws IOException {
    this.event = new RemoveLink(0, 4, 10);
    this.simpleClassName = this.getClass().getSimpleName();
    test();
  }
}