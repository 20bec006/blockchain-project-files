package pojo.events;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import pojo.events.helpers.EventTester;

class FlowBlockTest extends EventTester {

  @Test
  //{"kind":"flow-block","content":{"transmission-timestamp":981676,"reception-timestamp":982025,"begin-node-id":6,"end-node-id":9,"block-id":1}}
  void serialize() throws IOException {
    this.event = new FlowBlock(981676, 982025, 6, 9, 1);
    this.simpleClassName = this.getClass().getSimpleName();
    test();
  }

}