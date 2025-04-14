/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.global.ControllerTestSupport;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantInfo;

class RestaurantApiControllerTest extends ControllerTestSupport {

  @DisplayName("식당 단건 조회 admin 컨트롤러")
  @Nested
  class getOne{

    public static Stream<Arguments> provideUserRoleForCheckingGetRestaurantPermission() {
      return Stream.of(
          Arguments.of(UserRole.MASTER),
          Arguments.of(UserRole.OWNER),
          Arguments.of(UserRole.STAFF)
      );
    }

    @DisplayName("식당을 단건 조회할 수 있다.")
    @MethodSource("provideUserRoleForCheckingGetRestaurantPermission")
    @ParameterizedTest(name = "{index}: ''{0}'' 은 식당을 단건 조회할 수 있다.")
    void success(UserRole role) throws Exception {
      // given
      String restaurantUuid = UUID.randomUUID().toString();
      Long userId = 1L;

      GetRestaurantInfo.Menu menu = GetRestaurantInfo.Menu.builder()
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .name("비빔밥")
          .price(BigDecimal.valueOf(12000))
          .status("ACTIVE")
          .build();

      GetRestaurantInfo.TimeSlot timeSlot = GetRestaurantInfo.TimeSlot.builder()
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .availableDate(LocalDate.now())
          .timeslot(LocalTime.of(18, 0))
          .maxCapacity(20)
          .curTotalGuestCount(5)
          .build();

      GetRestaurantInfo response = GetRestaurantInfo.builder()
          .restaurantUuid(restaurantUuid)
          .name("맛집")
          .info("한식 전문점")
          .reviewRatingAvg(BigDecimal.valueOf(4.8))
          .maxReservationGuestCountPerTeamOnline(4)
          .contactNumber("010-1234-5678")
          .address("서울시 강남구")
          .openingAt(LocalTime.of(9, 0))
          .closingAt(LocalTime.of(22, 0))
          .status("OPENED")
          .waitingStatus("ACTIVE")
          .menus(List.of(menu))
          .timeSlots(List.of(timeSlot))
          .build();

      given(restaurantService.getRestaurant(any(GetRestaurantCriteria.class)))
          .willReturn(response);

      // when & then
      mockMvc.perform(get("/admin/v1/restaurants/{restaurantUuid}", restaurantUuid)
              .header(USER_ID_HEADER, userId)
              .header(USER_ROLE_HEADER, role)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.restaurantUuid").value(restaurantUuid))
          .andExpect(jsonPath("$.name").value("맛집"))
          .andExpect(jsonPath("$.info").value("한식 전문점"))
          .andExpect(jsonPath("$.reviewRatingAvg").value(4.8))
          .andExpect(jsonPath("$.maxReservationGuestCountPerTeamOnline").value(4))
          .andExpect(jsonPath("$.contactNumber").value("010-1234-5678"))
          .andExpect(jsonPath("$.address").value("서울시 강남구"))
          .andExpect(jsonPath("$.openingAt").value("09:00:00"))
          .andExpect(jsonPath("$.closingAt").value("22:00:00"))
          .andExpect(jsonPath("$.status").value("OPENED"))
          .andExpect(jsonPath("$.waitingStatus").value("ACTIVE"))
          .andExpect(jsonPath("$.menus[0].name").value("비빔밥"))
          .andExpect(jsonPath("$.menus[0].price").value(12000))
          .andExpect(jsonPath("$.menus[0].status").value("ACTIVE"))
          .andExpect(jsonPath("$.timeSlots[0].availableDate").value(LocalDate.now().toString()))
          .andExpect(jsonPath("$.timeSlots[0].timeslot").value("18:00:00"))
          .andExpect(jsonPath("$.timeSlots[0].maxCapacity").value(20))
          .andExpect(jsonPath("$.timeSlots[0].curTotalGuestCount").value(5));
    }
  }
}