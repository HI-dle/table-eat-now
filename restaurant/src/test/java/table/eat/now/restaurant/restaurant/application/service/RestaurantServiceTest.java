/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.restaurant.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.restaurant.global.IntegrationTestSupport;
import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantRepository;

class RestaurantServiceTest extends IntegrationTestSupport {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private RestaurantService restaurantService;

  @DisplayName("식당 생성 서비스")
  @Nested
  class create{

    @DisplayName("식당을 생성한다.")
    @Test
    void success(){
      // given
      CreateRestaurantCommand command = CreateRestaurantCommand.builder()
          .ownerId(1L)
          .name("스시 오마카세")
          .info("신선한 해산물과 숙련된 셰프의 스시를 즐길 수 있는 오마카세 전문점")
          .maxReservationGuestCountPerTeamOnline(20)
          .contactNumber("010-1234-5678")
          .address("address")
          .openingAt(LocalTime.of(8, 0))
          .closingAt(LocalTime.of(18, 0))
          .build();

      // when
      CreateRestaurantInfo restaurant = restaurantService.createRestaurant(command);

      List<Restaurant> all = restaurantRepository.findAll();

      // then
      assertThat(all).isNotNull();
      Restaurant result = all.get(0);
      assertThat(result).isNotNull();
      assertThat(result.getId()).isNotNull();
      assertThat(result)
          .extracting("name", "ownerId", "operatingTime.openingAt")
          .containsExactly(command.name(), command.ownerId(), command.openingAt());
    }
  }
}