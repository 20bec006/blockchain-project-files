package helpers;

public class SelfishMiningSimulationSettings extends SimulationSettings {
  /**
   * The percentage of the total network mining power owned by a selfish miner.
   * According to Eyal and Sirer, this is the parameter α (alpha)
   */
  public int selfishMiningPowerPercentage = 25;
}
