package table.eat.now.promotion.promotionRestaurant.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotionRestaurant.application.dto.excepton.PromotionRestaurantErrorCode;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.UpdatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.UpdatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionRestaurant.domain.repository.PromotionRestaurantRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
public class PromotionRestaurantServiceImpl implements PromotionRestaurantService{

  private final PromotionRestaurantRepository promotionRestaurantRepository;

  @Override
  @Transactional
  public CreatePromotionRestaurantInfo createPromotionRestaurant(
      CreatePromotionRestaurantCommand command) {
    return CreatePromotionRestaurantInfo
        .from(promotionRestaurantRepository.save(command.toEntity()));
  }

  @Override
  @Transactional
  public UpdatePromotionRestaurantInfo updatePromotionRestaurant(
      UpdatePromotionRestaurantCommand command, String promotionRestaurantUuid) {
    PromotionRestaurant promotionRestaurant = findByPromotionRestaurant(promotionRestaurantUuid);
    promotionRestaurant.modifyPromotionRestaurant(
        command.promotionUuid(),
        command.restaurantUuid());
    return UpdatePromotionRestaurantInfo.from(promotionRestaurant);
  }

  public PromotionRestaurant findByPromotionRestaurant(String promotionRestaurantUuid) {
    return promotionRestaurantRepository.findByPromotionRestaurantUuidAndDeletedAtIsNull(
        promotionRestaurantUuid).orElseThrow(() ->
        CustomException.from(PromotionRestaurantErrorCode.INVALID_PROMOTION_RESTAURANT_UUID));
  }


}
