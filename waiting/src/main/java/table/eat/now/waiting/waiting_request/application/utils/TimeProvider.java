package table.eat.now.waiting.waiting_request.application.utils;

import java.time.ZonedDateTime;

public class TimeProvider {
  private TimeProvider() {
    throw new IllegalStateException("Utility class");
  }

  public static long currentTimeMillis() {
    return ZonedDateTime.now().toInstant().toEpochMilli();
  }
}
