/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.global.fixture;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.restaurant.global.util.LongIdGenerator;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;

public class RestaurantTimeSlotFixture {

  public static Set<RestaurantTimeSlot> createRandoms(int length){
    if(length <= 0) return null;
    return IntStream.range(0, length)
        .mapToObj(i -> RestaurantTimeSlotFixture.createRandom())
        .collect(Collectors.toSet());
  }

  public static RestaurantTimeSlot createRandom(){
    Long num = LongIdGenerator.makeLong();
    return RestaurantTimeSlotFixture.create(
        UUID.randomUUID().toString(),
        null,
        LocalDate.of(2025, num.intValue()%12+1, num.intValue()%28+1),
        LocalTime.of(num.intValue()%23+1, 0),
        num.intValue(),
        0
    );
  }

  public static RestaurantTimeSlot create(
      String restaurantTimeslotUuid,
      Restaurant restaurant,
      LocalDate availableDate,
      LocalTime timeslot,
      Integer maxCapacity,
      Integer curTotalGuestCount
  ){
    RestaurantTimeSlot timeSlot = RestaurantTimeSlot.baseBuilder()
        .availableDate(availableDate)
        .maxCapacity(maxCapacity)
        .restaurant(restaurant)
        .timeslot(timeslot)
        .build();
    ReflectionTestUtils.setField(timeSlot, "restaurantTimeslotUuid", restaurantTimeslotUuid);
    ReflectionTestUtils.setField(timeSlot, "curTotalGuestCount", curTotalGuestCount);
    return timeSlot;
  }

}
