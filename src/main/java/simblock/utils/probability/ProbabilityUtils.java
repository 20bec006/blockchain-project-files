package simblock.utils.probability;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;


/**
 * The type Probability utils extends {@link SimblockDistribution} functionality by adding cdf to
 * pmf conversion and function calls to vendor libraries for distribution management.
 */
@Getter
@NoArgsConstructor
public class ProbabilityUtils implements SimblockDistribution {


  private EnumeratedDistribution<Integer> distribution;

  /**
   * Assumes a sorted list of pairs of a discrete cumulative distribution function F(x).
   * It generates a discrete probability mass function pmf(x) via:
   *
   * <p>pmf(x) = F(x) - F(x-1)
   *
   * @param cdf a list of pair values of the cdf(x)
   * @return a list of pair values of the pdf(x)
   */
  public List<Pair<Integer, Double>> pmfFromCdf(List<Pair<Integer, Double>> cdf) {
    List<Pair<Integer, java.lang.Double>> pmf = new ArrayList<>(cdf.size());
    for (int i = 0; i < cdf.size(); i++) {
      if (i == 0) {
        pmf.add(cdf.get(0));

      } else {
        Pair<Integer, Double> current = cdf.get(i);
        Pair<Integer, Double> prev = cdf.get(i - 1);

        pmf.add(new Pair<>(current.getKey(), current.getSecond() - prev.getValue()));
      }

    }

    return pmf;
  }


  /**
   * Generates a distribution abstraction from the given probability mass function.
   *
   * @param pmf the pmf list, a pair representing a occurrence and its pmf value.
   */
  public void fromPmf(List<Pair<Integer, Double>> pmf) {
    this.distribution = new EnumeratedDistribution<>(pmf);
  }

  @Override
  public void fromPmf(double[] pmf) {
    fromPmf(transformArray(pmf));

  }

  /**
   * Generates a distribution abstraction from the given cumulative distribution function.
   *
   * @param cdf the cdf list, a pair representing a occurrence and its cdf value.
   */
  public void fromCdf(List<Pair<Integer, Double>> cdf) {
    this.distribution = new EnumeratedDistribution<>(pmfFromCdf(cdf));
  }


  @Override
  public void fromCdf(double[] cdf) {
    fromCdf(transformArray(cdf));
  }

  @Override
  public ArrayList<Integer> generatePopulation(int size, long seed) {
    this.distribution.reseedRandomGenerator(seed);
    ArrayList<Integer> population = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      population.add(distribution.sample());
    }

    return population;
  }

  /*
   * Transforms the given array to a list used by
   * org.apache.commons.math3.distribution.EnumeratedDistribution;
   */
  private List<Pair<Integer, Double>> transformArray(double[] probabilityArray) {
    List<Pair<Integer, Double>> distributionList = new ArrayList<>(probabilityArray.length);
    for (int i = 0; i < probabilityArray.length; i++) {
      distributionList.add(new Pair<>(i, probabilityArray[i]));
    }

    return distributionList;

  }
}