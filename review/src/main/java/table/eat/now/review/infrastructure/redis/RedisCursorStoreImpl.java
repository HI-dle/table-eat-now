package table.eat.now.review.infrastructure.redis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import table.eat.now.review.application.batch.Cursor;
import table.eat.now.review.application.batch.CursorStore;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisCursorStoreImpl implements CursorStore {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String FIELD_UPDATED_AT = "lastProcessedUpdatedAt";
  private static final String FIELD_RESTAURANT_ID = "lastProcessedRestaurantId";

  @Override
  public void saveCursor(String key, Cursor cursor) {
    Map<String, String> cursorMap = new HashMap<>();

    if (cursor.lastProcessedUpdatedAt() != null) {
      cursorMap.put(FIELD_UPDATED_AT, cursor.lastProcessedUpdatedAt().toString());
    }

    if (cursor.lastProcessedRestaurantId() != null) {
      cursorMap.put(FIELD_RESTAURANT_ID, cursor.lastProcessedRestaurantId());
    }

    redisTemplate.opsForHash().putAll(key, cursorMap);
  }

  @Override
  public Cursor getCursor(String key) {
    Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

    if (entries.isEmpty()) {
      return Cursor.empty();
    }

    Object updatedAtValue = entries.get(FIELD_UPDATED_AT);
    Object restaurantIdValue = entries.get(FIELD_RESTAURANT_ID);

    if (updatedAtValue == null || restaurantIdValue == null) {
      return Cursor.empty();
    }

    return Cursor.of(
        LocalDateTime.parse(updatedAtValue.toString()),
        restaurantIdValue.toString()
    );
  }
}
