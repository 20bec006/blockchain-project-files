package pojo.events.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The type Event reader reads a stored test JSON file and returns it as string.
 */
public class EventReader {
  /**
   * Reads the JSON file stored in the test resources folder and returns it as string. The files should follow lowercase dash syntax:
   *
   * <p>example-name.json
   *
   * <p>The class files should follow camel case convention and be suffixed with test:
   *
   * <p>ExampleNameTest
   *
   * <p>The file names are derived from class names.
   *
   * @param className the class name to be used
   * @return the string
   * @throws IOException the io exception
   */
  public static String read(String className) throws IOException {
    String[] nameComponents = StringUtils.splitByCharacterTypeCamelCase(className);
    List<String> lowercaseComponents = Arrays.stream(nameComponents).map(String::toLowerCase).collect(Collectors.toList());
    lowercaseComponents.remove(lowercaseComponents.size() - 1);
    String filename = String.join("-", lowercaseComponents);

    ClassLoader.getSystemClassLoader().getResource(filename);
    File f = new File(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(filename + ".json")).getFile());
    return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
  }

}
