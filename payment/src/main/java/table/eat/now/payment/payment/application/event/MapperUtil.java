package table.eat.now.payment.payment.application.event;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;

public class MapperUtil {

  private MapperUtil() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .registerModule(new JavaTimeModule())
      .registerModule(new SimpleModule().addSerializer(BigDecimal.class, new ToStringSerializer()));

  public static JsonNode toJsonNode(Object payload) {
    try {
      return objectMapper.valueToTree(payload);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Failed to convert object to JsonNode: " + e.getMessage(), e);
    }
  }
}