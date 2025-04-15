package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantTimeSlotRepository;

public interface JpaRestaurantTimeSlotRepository extends JpaRepository<RestaurantTimeSlot, Long>,
    RestaurantTimeSlotRepository {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT rt FROM RestaurantTimeSlot rt WHERE rt.restaurantTimeslotUuid = :uuid")
  Optional<RestaurantTimeSlot> findWithLockByRestaurantTimeslotUuid(@Param("uuid") String uuid);
}
