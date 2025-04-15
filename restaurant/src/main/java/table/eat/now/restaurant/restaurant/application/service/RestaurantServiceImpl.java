/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantErrorCode;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantTimeSlotErrorCode;
import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantRepository;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantTimeSlotRepository;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;

  private final RestaurantTimeSlotRepository restaurantTimeSlotRepository;

  @Override
  public CreateRestaurantInfo createRestaurant(CreateRestaurantCommand command) {
    return CreateRestaurantInfo.from(restaurantRepository.save(command.toEntity()));
  }

  @Override
  @Transactional(readOnly = true)
  public GetRestaurantInfo getRestaurant(GetRestaurantCriteria criteria) {
    boolean includeDeleted = false;
    boolean includeInactive = false;

    switch (criteria.role()) {
      case MASTER -> {
        includeDeleted = true;
        includeInactive = true;
      }
      case OWNER -> {
        boolean isOwner = restaurantRepository.isOwner(criteria.userId(),
            criteria.restaurantUuid());
        if (isOwner) {
          includeDeleted = true;
          includeInactive = true;
        }
      }
      case STAFF -> {
        boolean isStaff = restaurantRepository.isStaff(criteria.userId(),
            criteria.restaurantUuid());
        if (isStaff) {
          includeInactive = true;
        }
      }
    }

    Restaurant restaurant = restaurantRepository.findByDynamicCondition(
        criteria.restaurantUuid(), includeDeleted, includeInactive
    ).orElseThrow(() -> CustomException.from(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

    return GetRestaurantInfo.from(restaurant);
  }

  @Override
  @Transactional
  public void increaseOrDecreaseTimeSlotGuestCount(String restaurantTimeSlotUuid, int delta) {
    RestaurantTimeSlot timeSlot = restaurantTimeSlotRepository
        .findWithLockByRestaurantTimeslotUuid(restaurantTimeSlotUuid)
        .orElseThrow(() -> new CustomException(RestaurantTimeSlotErrorCode.NOT_FOUND));

    int newCount = timeSlot.getCurTotalGuestCount() + delta;

    if (newCount < 0 || newCount > timeSlot.getMaxCapacity()) {
      throw new CustomException(RestaurantTimeSlotErrorCode.EXCEEDS_CAPACITY);
    }

    timeSlot.modifyCurTotalGuestCount(newCount);
  }

}
