package pojo.events;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import pojo.events.helpers.EventTester;

public class AddBlockTest extends EventTester {
  @Test
  void serialize() throws IOException {
    // {"kind":"add-block","content":{"timestamp":1515817,"node-id":10,"block-id":2}}

    this.event = new AddBlock(1515817, 10, 2);
    this.simpleClassName = this.getClass().getSimpleName();
    test();

  }
}
