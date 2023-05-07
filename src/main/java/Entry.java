import helpers.SimulationFactory;
import helpers.SimulationType;

public class Entry {

  /**
   * Main entry point for simulation.
   * @param args unused
   */
  public static void main(String[] args) {
    SimulationFactory.getInstance(SimulationType.SIMPLE_SELFISH_MINING).run();
  }
}
