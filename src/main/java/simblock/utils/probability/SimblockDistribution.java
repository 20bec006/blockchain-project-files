package simblock.utils.probability;

import java.util.ArrayList;

/**
 * The interface Simblock distribution acts as an adapter for the legacy codebase. It is able to
 * generate discrete distribution abstractions and to generate a sample population.
 */
public interface SimblockDistribution {

  long DEFAULT_SEED = 0L;

  /**
   * Generates a distribution abstraction from the given probability mass function.
   *
   * @param distribution the pmf array, index represents the occurrence, value represents
   *                     the probability.
   */
  void fromPmf(double[] distribution);

  /**
   * Generates a distribution abstraction from the given cumulative distribution function.
   *
   * @param distribution the cdf array, index represents the occurrence, value represents
   *                     the probability.
   */
  void fromCdf(double[] distribution);

  /**
   * Generate population array list. Consecutive calls to this method are always deterministic.
   *
   * @param size the size
   * @return the array list
   */
  default ArrayList<Integer> generatePopulation(int size) {
    return generatePopulation(size, DEFAULT_SEED);
  }

  /**
   * Generate population array list using the given seed.
   *
   * @param size the size
   * @param seed the seed
   * @return the array list
   */
  ArrayList<Integer> generatePopulation(int size, long seed);
}
