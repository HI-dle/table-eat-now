/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import static table.eat.now.restaurant.restaurant.domain.entity.QRestaurant.restaurant;
import static table.eat.now.restaurant.restaurant.domain.entity.QRestaurantMenu.restaurantMenu;
import static table.eat.now.restaurant.restaurant.domain.entity.QRestaurantTimeSlot.restaurantTimeSlot;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.RestaurantStatus;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu.MenuStatus;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Restaurant> findByDynamicCondition(
      String restaurantUuid, boolean includeDeleted, boolean includeInactive) {
    BooleanBuilder booleanbuilder =
        getFindRestarantBooleanBuilder(restaurantUuid, includeDeleted, includeInactive);

    return Optional.ofNullable(queryFactory
        .selectFrom(restaurant)
        .leftJoin(restaurant.menus, restaurantMenu).fetchJoin()
        .leftJoin(restaurant.timeSlots, restaurantTimeSlot).fetchJoin()
        .where(booleanbuilder)
        .fetchOne());
  }

  private static BooleanBuilder getFindRestarantBooleanBuilder(
      String uuid, boolean includeDeleted, boolean includeInactive) {
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(restaurant.restaurantUuid.eq(uuid));

    if (!includeDeleted) {
      builder.and(restaurant.deletedAt.isNull());
    }

    if (!includeInactive) {
      builder.and(restaurant.status.ne(RestaurantStatus.INACTIVE))
          .and(restaurantMenu.status.ne(MenuStatus.INACTIVE));
//      builder.and(
//          JPAExpressions
//              .selectOne()
//              .from(restaurantMenu)
//              .where(
//                  restaurantMenu.restaurant.eq(restaurant),
//                  restaurantMenu.status.eq(RestaurantMenu.MenuStatus.INACTIVE)
//              )
//              .notExists()
//      );
    }
    return builder;
  }

  @Override
  public boolean isOwnerByUserIdAndRestaurantUuid(Long userId, String restaurantUuid) {
    return queryFactory
        .selectOne()
        .from(restaurant)
        .where(
            restaurant.restaurantUuid.eq(restaurantUuid),
            restaurant.ownerId.eq(userId)
        )
        .fetchFirst() != null;
  }

  @Override
  public boolean isStaffByUserIdAndRestaurantUuid(Long userId, String restaurantUuid) {
    return queryFactory
        .selectOne()
        .from(restaurant)
        .where(
            restaurant.restaurantUuid.eq(restaurantUuid),
            restaurant.staffId.eq(userId)
        )
        .fetchFirst() != null;
  }
}
