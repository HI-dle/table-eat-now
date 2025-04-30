package table.eat.now.coupon.coupon.application.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MapperProvider {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
    //OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return OBJECT_MAPPER.convertValue(fromValue, toValueType);
  }

  public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
    return OBJECT_MAPPER.convertValue(fromValue, toValueTypeRef);
  }
}
