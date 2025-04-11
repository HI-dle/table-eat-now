/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response;

import table.eat.now.reservation.reservation.application.client.dto.response.ModifyRestaurantCurTotalGuestCountInfo;

public record ModifyRestaurantCurTotalGuestCountResponse(
    String restaurantTimeslotUuid,
    Integer curTotalGuestCount
) {
  public ModifyRestaurantCurTotalGuestCountInfo toInfo(){
    return new ModifyRestaurantCurTotalGuestCountInfo(restaurantTimeslotUuid, curTotalGuestCount);
  }
}
