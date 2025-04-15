package table.eat.now.restaurant.restaurant.domain.repository;

import java.util.Optional;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;

public interface RestaurantTimeSlotRepository {

  Optional<RestaurantTimeSlot> findWithLockByRestaurantTimeslotUuid(String restaurantTimeSlotUuid);

  // test 용
  Optional<RestaurantTimeSlot> findById(Long id);

}
