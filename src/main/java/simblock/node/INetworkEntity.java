package simblock.node;

public interface INetworkEntity {
  long getBandwidth(int toRegionID);

  long getLatency(int toRegionID);

  long getBlockSize();

  long getProcessingTime();

}
