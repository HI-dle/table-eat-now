/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.context;

import java.util.Map;
import lombok.Builder;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo.UserCoupon;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.response.GetRestaurantInfo;

@Builder
public record CreateReservationValidationContext(
    CreateReservationCommand command,
    GetRestaurantInfo restaurant,
    Map<String, UserCoupon> couponMap,
    Map<String, Promotion> promotionMap
) {
  public static CreateReservationValidationContext from(
      CreateReservationCommand command,
      GetRestaurantInfo restaurant,
      Map<String, UserCoupon> couponMap,
      Map<String, Promotion> promotionMap
  ) {
    return CreateReservationValidationContext.builder()
        .command(command)
        .restaurant(restaurant)
        .couponMap(couponMap)
        .promotionMap(promotionMap)
        .build();
  }
}
