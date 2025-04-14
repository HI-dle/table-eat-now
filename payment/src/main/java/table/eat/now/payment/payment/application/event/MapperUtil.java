package table.eat.now.payment.payment.application.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class MapperUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
      .registerModule(new JavaTimeModule());

  public static JsonNode toJsonNode(Object payload) {
    try {
      return objectMapper.valueToTree(payload);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Failed to convert object to JsonNode", e);
    }
  }
}
