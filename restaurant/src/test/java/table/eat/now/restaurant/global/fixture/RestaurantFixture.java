/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.global.fixture;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.restaurant.global.util.LongIdGenerator;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.RestaurantStatus;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.WaitingStatus;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;

public class RestaurantFixture {

  public static Restaurant createOpenedRandomByUuid(String uuid){
    Restaurant restaurant = createRandomByStatusAndMenusAndTimeSlots(
        RestaurantStatus.OPENED, null, null);
    ReflectionTestUtils.setField(restaurant, "restaurantUuid", uuid);
    return restaurant;
  }

  public static Restaurant createRandomByStatusAndMenusAndTimeSlots(
      RestaurantStatus status, Set<RestaurantMenu> menus, Set<RestaurantTimeSlot> timeSlots) {
    Long num = LongIdGenerator.makeLong();
    UUID.nameUUIDFromBytes(String.valueOf(num).getBytes(StandardCharsets.UTF_8));
    return createRestaurant(
        LongIdGenerator.makeLong(),
        LongIdGenerator.makeLong(),
        "name" + num,
        null,
        "info",
        num.intValue(),
        "010",
        "address",
        LocalTime.of(9, 0),
        LocalTime.of(17, 0),
        WaitingStatus.ACTIVE,
        status,
        menus,
        timeSlots
    );
  }

  public static Restaurant createRestaurant(
      Long ownerId,
      Long staffId,
      String name,
      BigDecimal reviewRatingAvg,
      String info,
      Integer maxReservationGuestCountPerTeamOnline,
      String contactNumber,
      String address,
      LocalTime openingAt,
      LocalTime closingAt,
      WaitingStatus waitingStatus,
      RestaurantStatus status,
      Set<RestaurantMenu> menus,
      Set<RestaurantTimeSlot> timeSlots
  ) {

    Restaurant restaurant = Restaurant.fullBuilder()
        .ownerId(ownerId)
        .staffId(staffId)
        .name(name)
        .reviewRatingAvg(reviewRatingAvg)
        .info(info)
        .maxReservationGuestCountPerTeamOnline(maxReservationGuestCountPerTeamOnline)
        .address(address)
        .contactNumber(contactNumber)
        .openingAt(openingAt)
        .closingAt(closingAt)
        .waitingStatus(waitingStatus)
        .status(status)
        .menus(new HashSet<>())
        .timeSlots(new HashSet<>())
        .build();

    restaurant.addMenus(menus);
    restaurant.addTimeSlots(timeSlots);
    return restaurant;
  }

  public static Restaurant createRandomByStatusAndWaitingStatusOwnerId(RestaurantStatus restaurantStatus, WaitingStatus waitingStatus, long ownerId) {
    Restaurant restaurant = createRandomByStatusAndMenusAndTimeSlots(
        restaurantStatus, Set.of(RestaurantMenuFixture.createRandom()),
        Set.of(RestaurantTimeSlotFixture.createRandom()));
    ReflectionTestUtils.setField(restaurant, "waitingStatus", waitingStatus);
    ReflectionTestUtils.setField(restaurant, "ownerId", ownerId);
    return restaurant;
  }

  public static Restaurant createRandomByStatusAndStaffId(RestaurantStatus restaurantStatus, WaitingStatus waitingStatus, long staffId) {
    Restaurant restaurant = createRandomByStatusAndMenusAndTimeSlots(
        restaurantStatus, Set.of(RestaurantMenuFixture.createRandom()),
        Set.of(RestaurantTimeSlotFixture.createRandom()));
    ReflectionTestUtils.setField(restaurant, "waitingStatus", waitingStatus);
    ReflectionTestUtils.setField(restaurant, "staffId", staffId);
    return restaurant;
  }
}
