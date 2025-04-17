/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.global.fixture;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.restaurant.global.util.LongIdGenerator;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu.MenuStatus;

public class RestaurantMenuFixture {

  public static Set<RestaurantMenu> createRandomsByStatus(MenuStatus status, int length){
    return IntStream.range(0, length)
        .mapToObj(i -> RestaurantMenuFixture.createRandomByStatus(status))
        .collect(Collectors.toSet());
  }

  public static RestaurantMenu createRandomByStatus(MenuStatus status){
    Long num = LongIdGenerator.makeLong();
    return RestaurantMenuFixture.create(
        UUID.randomUUID().toString(),
        "name" + num,
        BigDecimal.valueOf(num * 1000),
        null,
        status
    );
  }

  public static RestaurantMenu create(
      String restaurantMenuUuid,
      String name,
      BigDecimal price,
      Restaurant restaurant,
      MenuStatus status
  ) {

    RestaurantMenu menu = RestaurantMenu.fullBuilder()
        .restaurant(restaurant)
        .name(name)
        .price(price)
        .restaurantMenuUuid(UUID.randomUUID().toString())
        .menuStatus(status)
        .build();

    ReflectionTestUtils.setField(menu, "restaurantMenuUuid", restaurantMenuUuid);

    return menu;
  }
}
