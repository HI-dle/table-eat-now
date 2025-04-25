/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.request;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record GetRestaurantsCriteria(
    UserRole role,
    Long userId,
    String searchText,
    Long ownerId,
    Long staffId,
    RestaurantStatus restaurantStatus,
    WaitingStatus waitingStatus,
    int pageNumber,
    int pageSize,
    boolean isAsc,
    String sortBy,
    boolean includeDeleted
){

  @RequiredArgsConstructor
  public enum RestaurantStatus {
    OPENED("운영중"),
    CLOSED("마감된"),
    BREAK("브레이크 타임"),
    HOLIDAY("휴일"),
    INACTIVE("비활성화"),
    ;
    private final String name;
  }

  @RequiredArgsConstructor
  public enum WaitingStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    ;
    private final String name;
  }
}
