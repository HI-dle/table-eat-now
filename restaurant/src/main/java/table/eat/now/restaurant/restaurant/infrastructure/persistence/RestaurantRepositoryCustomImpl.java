/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import static table.eat.now.restaurant.restaurant.domain.entity.QRestaurant.restaurant;
import static table.eat.now.restaurant.restaurant.domain.entity.QRestaurantMenu.restaurantMenu;
import static table.eat.now.restaurant.restaurant.domain.entity.QRestaurantTimeSlot.restaurantTimeSlot;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria;
import table.eat.now.restaurant.restaurant.domain.dto.response.Paginated;
import table.eat.now.restaurant.restaurant.domain.entity.QRestaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.RestaurantStatus;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu.MenuStatus;
import table.eat.now.restaurant.restaurant.infrastructure.persistence.condition.RestaurantSearchCondition;

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
    }
    return builder;
  }

  @Override
  public Paginated<Restaurant> searchRestaurants(GetRestaurantsCriteria criteria) {
    QRestaurant qRestaurant = restaurant;

    // 조건 빌더 클래스를 사용하여 동적 조건 추가
    RestaurantSearchCondition searchCondition = new RestaurantSearchCondition(qRestaurant, criteria);
    BooleanBuilder builder = searchCondition.build();

    // 쿼리 실행
    List<Restaurant> result = queryFactory.selectFrom(qRestaurant)
        .where(builder)
        .orderBy(getOrderSpecifier(criteria.sortBy(), criteria.isAsc()))
        .offset(criteria.pageNumber() * criteria.pageSize())
        .limit(criteria.pageSize())
        .fetch();

    long total;
    if (result.isEmpty() || result.size() >= criteria.pageSize()) {
      total = Optional.ofNullable(
          queryFactory.select(Wildcard.count)
              .from(qRestaurant)
              .where(builder)
              .fetchOne()
      ).orElse(0L);
    } else {
      int start =  criteria.pageNumber() * criteria.pageSize();
      total = start + result.size();
    }

    int totalPages = (int) ((total + criteria.pageSize() - 1) / criteria.pageSize());

    return Paginated.of(result, total, totalPages, criteria.pageNumber(), criteria.pageSize());
  }

  public static OrderSpecifier<?> getOrderSpecifier(String sortBy, boolean isAsc) {
    if(sortBy == null) sortBy = "id";

    Order order = isAsc ? Order.ASC : Order.DESC;
    PathBuilder<?> entityPath = new PathBuilder<>(Restaurant.class, "restaurant");

    RestaurantSortField sortField = RestaurantSortField.from(sortBy);

    switch (sortField) {
      case ID -> {
        return new OrderSpecifier<>(order, entityPath.getNumber("id", Long.class));
      }
      case NAME -> {
        return new OrderSpecifier<>(order, entityPath.getString("name"));
      }
      default -> throw new IllegalArgumentException("Unsupported sort field: " + sortField);
    }
  }

  @RequiredArgsConstructor
  public enum RestaurantSortField {
    ID("id"),
    NAME("name");

    private final String field;

    public static RestaurantSortField from(String sortBy) {
      for (RestaurantSortField value : values()) {
        if (value.field.equals(sortBy)) {
          return value;
        }
      }
      throw new IllegalArgumentException("Unsupported sortBy: " + sortBy);
    }
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
