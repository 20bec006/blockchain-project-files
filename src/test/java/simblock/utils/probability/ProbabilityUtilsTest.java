package simblock.utils.probability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProbabilityUtilsTest {

  private static final ArrayList<Integer> elements = new ArrayList<>(Arrays.asList(0, 1, 2));
  private List<Pair<Integer, Double>> pmf;
  private List<Pair<Integer, Double>> cdf;
  private ProbabilityUtils probUtils;

  @BeforeEach
  void setup() {
    probUtils = new ProbabilityUtils();
    pmf = new ArrayList<>(Arrays.asList(
        new Pair<>(elements.get(0), 0.25d),
        new Pair<>(elements.get(1), 0.5d),
        new Pair<>(elements.get(2), 0.25d)
    ));

    cdf = new ArrayList<>(Arrays.asList(
        new Pair<>(elements.get(0), 0.25d),
        new Pair<>(elements.get(1), 0.75d),
        new Pair<>(elements.get(2), 1d)
    ));

  }

  @Test
  void pdfFromCdf() {
    List<Pair<Integer, Double>> actual = probUtils.pmfFromCdf(cdf);
    assertEquals(pmf, actual);
  }

  @Test
  void fromPmf() {
    probUtils.fromPmf(pmf);
    ArrayList<Integer> population = probUtils.generatePopulation(10);
    assertEquals(10, population.size());

    for (Integer el : population) {
      assertTrue(elements.contains(el));
    }

  }

  @Test
  void fromPmfArray() {
    probUtils.fromPmf(toArray(pmf));
    ArrayList<Integer> population = probUtils.generatePopulation(10);
    assertEquals(10, population.size());

    for (Integer el : population) {
      assertTrue(elements.contains(el));
    }

  }

  @Test
  void fromCdf() {
    probUtils.fromCdf(cdf);
    ArrayList<Integer> population = probUtils.generatePopulation(10);
    assertEquals(10, population.size());

    for (Integer el : population) {
      assertTrue(elements.contains(el));
    }

  }

  @Test
  void fromCdfArray() {
    probUtils.fromCdf(toArray(cdf));
    ArrayList<Integer> population = probUtils.generatePopulation(10);
    assertEquals(10, population.size());

    for (Integer el : population) {
      assertTrue(elements.contains(el));
    }

  }

  private double[] toArray(List<Pair<Integer, Double>> list) {
    double[] array = new double[list.size()];
    int i = 0;
    for (Pair<Integer, Double> pair : list) {
      array[i] = pair.getValue();
      i++;
    }

    return array;
  }

  @Test
  void defaultSeedGeneratesDeterministicPopulation() {
    probUtils.fromCdf(cdf);
    ArrayList<Integer> population1 = probUtils.generatePopulation(10);
    ArrayList<Integer> population2 = probUtils.generatePopulation(10);

    assertEquals(population1, population2);

  }

  @Test
  void providedSeedGeneratesDeterministicPopulation() {
    probUtils.fromCdf(cdf);
    ArrayList<Integer> population1 = probUtils.generatePopulation(10, 1);
    ArrayList<Integer> population2 = probUtils.generatePopulation(10, 1);

    assertEquals(population1, population2);

  }

  @Test
  void differentSeedGeneratesDifferentPopulation() {
    probUtils.fromCdf(cdf);
    ArrayList<Integer> population1 = probUtils.generatePopulation(10, 1);
    ArrayList<Integer> population2 = probUtils.generatePopulation(10, 2);

    assertNotEquals(population1, population2);

  }
}