package simblock;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import simblock.block.IBlockIdGenerator;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AutoIncrementingGenerator implements IBlockIdGenerator {
  @Getter
  private int latestBlockID;

  @Override
  public int createNextBlockID() {
    return latestBlockID++;
  }
}
