package visualizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * The type Json serializer serializes Lombok annotated POJOS.
 */
public class JsonSerializer {

  private ObjectMapper mapper;

  /**
   * Instantiates a new Json serializer.
   */
  public JsonSerializer() {
    this.mapper = new ObjectMapper();
  }

  /**
   * Serializes the provided event as JSON.
   *
   * @param event the event
   * @return the serialized event
   */
  @SneakyThrows(JsonProcessingException.class) //TODO contemplate this
  public String serialize(VisualizerEvent event) {
    return mapper.writeValueAsString(event);
  }

}
