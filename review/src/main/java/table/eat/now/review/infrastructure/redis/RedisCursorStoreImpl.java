package table.eat.now.review.infrastructure.redis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import table.eat.now.review.application.batch.Cursor;
import table.eat.now.review.application.batch.CursorStore;

@Repository
@RequiredArgsConstructor
public class RedisCursorStoreImpl implements CursorStore {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String FIELD_UPDATED_AT = "lastProcessedUpdatedAt";
  private static final String FIELD_RESTAURANT_ID = "lastProcessedRestaurantId";

  @Override
  public void saveCursor(String key, Cursor cursor) {
    Map<String, String> cursorMap = new HashMap<>();
    cursorMap.put(FIELD_UPDATED_AT, cursor.lastProcessedUpdatedAt().toString());
    cursorMap.put(FIELD_RESTAURANT_ID, cursor.lastProcessedRestaurantId());

    redisTemplate.opsForHash().putAll(key, cursorMap);
  }

  @Override
  public Cursor getCursor(String key) {
    Object updatedAtValue = redisTemplate.opsForHash().get(key, FIELD_UPDATED_AT);
    Object restaurantIdValue = redisTemplate.opsForHash().get(key, FIELD_RESTAURANT_ID);

    if (updatedAtValue == null || restaurantIdValue == null) {
      return Cursor.empty();
    }

    return Cursor.of(
        LocalDateTime.parse(updatedAtValue.toString()),
        restaurantIdValue.toString()
    );
  }
}
