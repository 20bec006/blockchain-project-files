package visualizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Paths;
import observable.interfaces.IVisualizerEventObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Json writer observes the event log and serializes them to JSON file.
 */
public class VisualizerJsonWriter implements IVisualizerEventObserver {

  // The logger
  private static final Logger LOGGER = LoggerFactory.getLogger(VisualizerJsonWriter.class);

  /**
   * The output file name.
   */
  public static final String OUT_FILE_NAME = "output.json";
  public static final String OUT_FILE_URI = "dist/output/";

  private PrintWriter outWriter;
  private URI outFileUri;
  private boolean empty;
  private JsonSerializer serializer;


  /**
   * Instantiates a new Json writer.
   */
  public VisualizerJsonWriter() {

    this.outFileUri = Paths.get(OUT_FILE_URI).toUri();
    this.empty = true;
    this.serializer = new JsonSerializer();

  }

  /**
   * Opens the file for writing and initializes the JSON format.
   *
   * @throws IOException while opening a file
   */
  public void open() throws IOException {
    outWriter = new PrintWriter(
        new BufferedWriter(
            new FileWriter(new File(outFileUri.resolve(OUT_FILE_NAME)))
        )
    );

    LOGGER.info("Event log started.");
    outWriter.write("[");
    outWriter.flush();

  }

  @Override
  public void update(VisualizerEvent e) {
    if (!empty) {
      outWriter.write(",");
    }
    empty = false;
    outWriter.write(serializer.serialize(e));
    outWriter.flush();
  }

  /**
   * Closes the output JSON file.
   */
  public void close() {
    outWriter.write("]");
    outWriter.flush();
    LOGGER.info("Event log closed.");

  }

}
