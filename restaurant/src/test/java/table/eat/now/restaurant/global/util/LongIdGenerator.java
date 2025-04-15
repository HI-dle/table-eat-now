/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.global.util;

import java.util.concurrent.atomic.AtomicLong;

public class LongIdGenerator {
  private static final AtomicLong counter = new AtomicLong(1);

  public static Long makeLong(){
    return counter.getAndIncrement();
  }

}
