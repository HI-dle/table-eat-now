package table.eat.now.promotion.promotion.infrastructure.metric;

import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 20.
 */
@NoArgsConstructor
public class PromotionMetricName {

  public static final String PROMOTION_PARTICIPATION_SUCCESS = "promotion.participation.success.count";
  public static final String PROMOTION_PARTICIPATION_FAIL = "promotion.participation.fail.count";
  public static final String PROMOTION_PARTICIPATION_LATENCY = "promotion.participation.redis.script.latency";
  //아래 친구는 PromotionUser로 옮길 것 같아요..!
  public static final String PROMOTION_USER_BATCH_SAVE_LATENCY = "promotion.user.batch.save.latency";
}
