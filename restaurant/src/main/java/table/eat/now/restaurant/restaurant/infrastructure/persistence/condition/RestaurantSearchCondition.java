/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 26.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence.condition;

import com.querydsl.core.BooleanBuilder;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria;
import table.eat.now.restaurant.restaurant.domain.entity.QRestaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;

public class RestaurantSearchCondition {

  private final QRestaurant qRestaurant;
  private final GetRestaurantsCriteria criteria;
  private final BooleanBuilder builder;

  public RestaurantSearchCondition(QRestaurant qRestaurant, GetRestaurantsCriteria criteria) {
    this.qRestaurant = qRestaurant;
    this.criteria = criteria;
    this.builder = new BooleanBuilder();
  }

  public BooleanBuilder build() {
    addSearchTextCondition();
    addOwnerIdCondition();
    addStaffIdCondition();
    addRestaurantStatusCondition();
    addWaitingStatusCondition();
    addDeletedCondition();
    return builder;
  }

  // (비즈니스 로직이 여기 들어간 것 같아서 맘에 안드는데 이정도는 괜찮을지..)
  private void addDeletedCondition() {
    if(criteria.role() == UserRole.MASTER){
      if(!criteria.includeDeleted()) builder.and(qRestaurant.deletedAt.isNull());
      return;
    }

    if(criteria.role() == UserRole.OWNER && criteria.includeDeleted()){
      builder.and(
          qRestaurant.deletedAt.isNull()
              .or(
                  qRestaurant.deletedAt.isNotNull().and(qRestaurant.ownerId.eq(criteria.userId()))
              )
      );
      return;
    }

    builder.and(qRestaurant.deletedAt.isNull());
  }

  private void addSearchTextCondition() {
    if (criteria.searchText() != null && !criteria.searchText().isEmpty()) {
      builder.and(qRestaurant.name.contains(criteria.searchText())
          .or(qRestaurant.info.contains(criteria.searchText())));
    }
  }

  private void addOwnerIdCondition() {
    if (criteria.ownerId() != null) {
      builder.and(qRestaurant.ownerId.eq(criteria.ownerId()));
    }
  }

  private void addStaffIdCondition() {
    if (criteria.staffId() != null) {
      builder.and(qRestaurant.staffId.eq(criteria.staffId()));
    }
  }

  private void addRestaurantStatusCondition() {
    if (criteria.restaurantStatus() != null) {
      builder.and(qRestaurant.status.eq(Restaurant.RestaurantStatus.valueOf(criteria.restaurantStatus().name())));
    }
  }

  private void addWaitingStatusCondition() {
    if (criteria.waitingStatus() != null) {
      builder.and(qRestaurant.waitingStatus.eq(Restaurant.WaitingStatus.valueOf(criteria.waitingStatus().name())));
    }
  }
}