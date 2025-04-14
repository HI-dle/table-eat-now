package table.eat.now.promotion.promotion.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.dto.client.response.GetPromotionRestaurantInfo;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.domain.entity.Promotion;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@Builder
public record GetPromotionsClientInfo(List<ReservationInfo> reservationRequests) {

  @Builder
  public record ReservationInfo(Long promotionId,
                                String promotionUuid,
                                String promotionName,
                                LocalDateTime startTime,
                                LocalDateTime endTime,
                                String description,
                                BigDecimal discountPrice,
                                String promotionStatus,
                                String promotionType,
                                String promotionRestaurantUuid,
                                String restaurantUuid) {
  }

  public static GetPromotionsClientInfo from(
      List<GetPromotionRestaurantInfo> restaurantInfos,
      List<Promotion> promotions
  ) {
    List<ReservationInfo> reservationInfos = promotions.stream()
        .map(promotion -> {
          GetPromotionRestaurantInfo matchedInfo = restaurantInfos.stream()
              .filter(info -> info.promotionUuid().equals(promotion.getPromotionUuid()))
              .findFirst()
              .orElseThrow(() ->
                  CustomException.from(
                      PromotionErrorCode.INVALID_PROMOTION_PARTICIPATION_RESTAURANT));

          return ReservationInfo.builder()
              .promotionId(promotion.getId())
              .promotionUuid(promotion.getPromotionUuid())
              .promotionName(promotion.getDetails().getPromotionName())
              .description(promotion.getDetails().getDescription())
              .startTime(promotion.getPeriod().getStartTime())
              .endTime(promotion.getPeriod().getEndTime())
              .discountPrice(promotion.getDiscountPrice().getDiscountAmount())
              .promotionStatus(promotion.getPromotionStatus().name())
              .promotionType(promotion.getPromotionType().name())
              .promotionRestaurantUuid(matchedInfo.promotionRestaurantUuid())
              .restaurantUuid(matchedInfo.restaurantUuid())
              .build();
        })
        .toList();

    return new GetPromotionsClientInfo(reservationInfos);
  }


}
