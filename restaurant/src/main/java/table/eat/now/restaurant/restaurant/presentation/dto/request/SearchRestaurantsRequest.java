/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 26.
 */
package table.eat.now.restaurant.restaurant.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria.RestaurantStatus;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria.WaitingStatus;

public record SearchRestaurantsRequest(
    String searchText,
    Long ownerId,
    Long staffId,
    @Pattern(
        regexp = "^(OPENED|CLOSED|BREAK|HOLIDAY|INACTIVE)$",
        message = "식당 상태는 OPENED, CLOSED, BREAK, HOLIDAY, INACTIVE 중 하나여야 합니다."
    )
    String restaurantStatus,
    @Pattern(
        regexp = "^(ACTIVE|INACTIVE)$",
        message = "웨이팅 상태는 ACTIVE 또는 INACTIVE 이어야 합니다."
    )
    String waitingStatus,
    Integer pageNumber,
    Integer pageSize,
    Boolean isAsc,
    @Pattern(
        regexp = "^(id|name)$",
        message = "정렬 기준은 id 또는 name만 가능합니다."
    )
    String sortBy,

    Boolean includeDeleted
) {
  public SearchRestaurantsRequest {
    if (sortBy == null) sortBy = "id";
    if (pageSize == null || pageSize <= 0) pageSize = 10;
    if (pageNumber == null || pageNumber < 0) pageNumber = 0;
    if (includeDeleted == null) includeDeleted = false;
    if (isAsc == null) isAsc = false;
  }

  public GetRestaurantsCriteria toCriteria(CurrentUserInfoDto userInfo) {
    return GetRestaurantsCriteria.builder()
        .role(userInfo.role())
        .userId(userInfo.userId())
        .searchText(searchText)
        .ownerId(ownerId)
        .staffId(staffId)
        .restaurantStatus(restaurantStatus != null ? RestaurantStatus.valueOf(restaurantStatus) : null)
        .waitingStatus(waitingStatus != null ? WaitingStatus.valueOf(waitingStatus) : null)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .isAsc(isAsc)
        .sortBy(sortBy)
        .includeDeleted(includeDeleted)
        .build();
  }

}
