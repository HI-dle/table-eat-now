/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.global.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UuidMaker {
  private static final AtomicLong counter = new AtomicLong(1);

  public static UUID makeUuid() {
    long count = counter.getAndIncrement();
    return UUID.nameUUIDFromBytes(String.valueOf(count).getBytes(StandardCharsets.UTF_8));
  }
}
