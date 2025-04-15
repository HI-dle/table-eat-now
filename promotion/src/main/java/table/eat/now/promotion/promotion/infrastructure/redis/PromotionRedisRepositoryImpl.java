package table.eat.now.promotion.promotion.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import table.eat.now.promotion.promotion.infrastructure.dto.request.PromotionUserQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Repository
@RequiredArgsConstructor
public class PromotionRedisRepositoryImpl implements PromotionRedisRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private static final String PROMOTION_KEY_PRE_FIX = "promotion:";

  @Override
  public void addUserToPromotion(String promotionName, PromotionUserQuery promotionUserQuery) {

    // 스코어는 현재 시간을 기준으로 설정
    double score = System.currentTimeMillis();

    // ZSET에 PromotionUserInfo 객체 저장
    redisTemplate.opsForZSet().add(
        PROMOTION_KEY_PRE_FIX + promotionName, promotionUserQuery, score);
  }


  @Override
  public void removeUserFromPromotion(String promotionName, Long userId) {
    redisTemplate.opsForZSet().removeRangeByScore(
        PROMOTION_KEY_PRE_FIX + promotionName, userId, userId);
  }
}