package simblock.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


class NodeDegreeParserTest {

  @TempDir
  static Path classTempDir;

  private static final String CSV = "NodeDegree,CumulativeProbability"
      + "\n"
      + "1,0.000893313883552286"
      + "\n"
      + "2,0.0110688628926378";
  private File csvFile;
  private static final String fileNameString = "degree.csv";

  @BeforeEach
  void setup() throws IOException {
    csvFile = classTempDir.resolve(fileNameString).toFile();
    FileUtils.write(csvFile, CSV, StandardCharsets.UTF_8, false);
  }

  @Test
  void parsePath() throws IOException {
    NodeDegreeParser ndp = new NodeDegreeParser();
    Double[] cdf = ndp.parse(new FileInputStream(csvFile));
    assertEquals(cdf[0], 0.000893313883552286d);
    assertEquals(cdf[1], 0.0110688628926378d);

  }

}