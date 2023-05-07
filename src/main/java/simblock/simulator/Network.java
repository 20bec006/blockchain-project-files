/*
 * Copyright 2019 Distributed Systems Group
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simblock.simulator;

import static simblock.settings.NetworkConfiguration.DEGREE_DISTRIBUTION_PATH_STRING;
import static simblock.settings.NetworkConfiguration.DOWNLOAD_BANDWIDTH;
import static simblock.settings.NetworkConfiguration.LATENCY;
import static simblock.settings.NetworkConfiguration.REGION_DISTRIBUTION;
import static simblock.settings.NetworkConfiguration.REGION_LIST;
import static simblock.settings.NetworkConfiguration.UPLOAD_BANDWIDTH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Region;
import simblock.settings.NetworkConfiguration;
import simblock.simulator.interfaces.IStochasticProcess;
import simblock.utils.NodeDegreeParser;

/**
 * The type Network represents a network split in regions, each node belonging to a region with
 * an upload bandwidth
 * and a download bandwidth. Node degrees follow a predefined degree distribution.
 */
@AllArgsConstructor
public class Network implements IStochasticProcess {

  private static final URI OUT_FILE_URI = Paths.get("dist/output").toUri();

  private static File networkInfo = new File(OUT_FILE_URI.resolve("static.json"));

  private Random random;

  private final Logger logger = LoggerFactory.getLogger(Network.class);

  /**
   * Gets latency according with 20% variance pallet distribution.
   *
   * @param from the from latency
   * @param to   the to latency
   * @return the calculated latency
   */
  public long getLatency(int from, int to) {
    long mean = LATENCY[from][to];
    double shape = 0.2 * mean;
    double scale = mean - 5;
    return Math.round(scale / Math.pow(random.nextDouble(), 1.0 / shape));
  }

  /**
   * Gets the minimum between the <em>from</em> upload bandwidth and <em>to</em> download
   * bandwidth.
   *
   * @param from the from index in the {@link NetworkConfiguration#UPLOAD_BANDWIDTH} array.
   * @param to   the to index in the {@link NetworkConfiguration#UPLOAD_BANDWIDTH} array.
   * @return the bandwidth
   */

  public long getBandwidth(int from, int to) {
    // This is constant, i.e. not randomized
    return Math.min(UPLOAD_BANDWIDTH[from], DOWNLOAD_BANDWIDTH[to]);
  }

  /**
   * Gets region list.
   *
   * @return the {@link NetworkConfiguration#REGION_LIST} list.
   */
  public List<String> getRegionList() {
    return REGION_LIST;
  }

  /**
   * Return the number of nodes in the corresponding region as a portion the number of all nodes.
   *
   * @return an array the distribution
   */
  public double[] getRegionDistribution() {
    return REGION_DISTRIBUTION;
  }

  /**
   * Gets the cumulative degree distribution array. The index of the array represents the degree
   * of the node, while the value presents the CDF value for that degree.
   *
   * @return the CDF double array
   */
  public double[] getDegreeDistribution() {
    double[] degreeDist = new double[0];
    InputStream is;
    is = ClassLoader.getSystemClassLoader().getResourceAsStream(DEGREE_DISTRIBUTION_PATH_STRING);
    try {
      NodeDegreeParser ndp = new NodeDegreeParser();
      degreeDist = Arrays.stream(ndp.parse(is)).mapToDouble(Double::doubleValue).toArray();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      //TODO move magic constant
      System.exit(1);
    }
    return degreeDist;
  }

  /**
   * Prints the currently active regions to outfile.
   */
  public void printRegion() {
    //TODO inject these constants
    List<Region> regions = new ArrayList<>(REGION_LIST.size());

    for (int i = 0; i < REGION_LIST.size(); i++) {
      regions.add(new Region(i, REGION_LIST.get(i)));
    }

    try {
      ObjectWriter writer = new ObjectMapper().writer().withRootName("region");

      FileUtils.write(networkInfo, writer.writeValueAsString(regions), StandardCharsets.UTF_8, false);
      logger.info(writer.writeValueAsString(regions));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public Random getRandomGenerator() {
    return this.random;
  }
}
