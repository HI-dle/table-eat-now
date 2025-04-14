package table.eat.now.promotion.promotion.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.client.PromotionClient;
import table.eat.now.promotion.promotion.application.dto.client.response.GetPromotionRestaurantInfo;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.infrastructure.client.feign.PromotionRestaurantFeignClient;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionRestaurantClientImpl implements PromotionClient {

  private final PromotionRestaurantFeignClient promotionRestaurantFeignClient;
  @Override
  public GetPromotionRestaurantInfo findRestaurantsByPromotions(String restaurantUuid) {
    try {
      return promotionRestaurantFeignClient.findRestaurantsByPromotions(restaurantUuid);
    } catch (FeignException.NotFound e) {
      log.info("프로모션에 참여하는 레스토랑 정보가 없습니다.: {}", restaurantUuid);
      return null;
    } catch (FeignException e) {
      log.error("프로모션 서비스 호출 실패: {}", e.getMessage(), e);
      throw CustomException.from(PromotionErrorCode.INVALID_PROMOTION_PARTICIPATION_RESTAURANT);
    }
  }
}

