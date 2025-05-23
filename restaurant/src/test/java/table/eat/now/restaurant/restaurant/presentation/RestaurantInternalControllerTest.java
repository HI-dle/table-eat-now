/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.global.ControllerTestSupport;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantErrorCode;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantTimeSlotErrorCode;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantSimpleInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.PaginatedInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.SearchRestaurantsInfo;

class RestaurantInternalControllerTest extends ControllerTestSupport {

  private final String baseUrl = "/internal/v1/restaurants";

  @DisplayName("식당 단건 조회 internal 컨트롤러")
  @Nested
  class getOne{

    public static Stream<Arguments> provideUserRoleForCheckingGetRestaurantPermission() {
      return Stream.of(
          Arguments.of(UserRole.MASTER),
          Arguments.of(UserRole.OWNER),
          Arguments.of(UserRole.STAFF),
          Arguments.of(UserRole.CUSTOMER)
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
      mockMvc.perform(get(baseUrl+ "/{restaurantUuid}", restaurantUuid)
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

  @DisplayName("식당 목록 조회 컨트롤러")
  @Nested
  class SearchRestaurants {

    public static Stream<Arguments> provideUserRoleForSearchingRestaurants() {
      return Stream.of(
          Arguments.of(UserRole.MASTER),
          Arguments.of(UserRole.OWNER),
          Arguments.of(UserRole.STAFF),
          Arguments.of(UserRole.CUSTOMER)
      );
    }

    @DisplayName("식당 목록을 조회할 수 있다.")
    @MethodSource("provideUserRoleForSearchingRestaurants")
    @ParameterizedTest(name = "{index}: ''{0}'' 는 식당 목록을 조회할 수 있다.")
    void success(UserRole role) throws Exception {
      // given
      Long userId = 1L;

      SearchRestaurantsInfo info = SearchRestaurantsInfo.builder()
          .restaurantUuid(UUID.randomUUID().toString())
          .ownerId(1L)
          .staffId(2L)
          .name("맛있는 식당")
          .reviewRatingAvg(BigDecimal.valueOf(4.5))
          .info("깔끔한 분위기의 한식당")
          .maxReservationGuestCountPerTeamOnline(4)
          .waitingStatus("ACTIVE")
          .status("OPENED")
          .contactNumber("010-1234-5678")
          .address("서울시 강남구")
          .openingAt(LocalTime.of(9, 0))
          .closingAt(LocalTime.of(21, 0))
          .build();

      PaginatedInfo<SearchRestaurantsInfo> paginatedInfo = new PaginatedInfo<>(
          List.of(info),
          10,
          10,
          1,
          10
      );

      given(restaurantService.searchRestaurants(any()))
          .willReturn(paginatedInfo);

      // when & then
      mockMvc.perform(get(baseUrl)
              .header(USER_ID_HEADER, userId)
              .header(USER_ROLE_HEADER, role)
              .contentType(MediaType.APPLICATION_JSON)
              .queryParam("pageNumber", "0")
              .queryParam("sortBy", "id")
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.contents[0].name").value("맛있는 식당"))
          .andExpect(jsonPath("$.pageNumber").value(paginatedInfo.pageNumber()))
          .andExpect(jsonPath("$.pageSize").value(paginatedInfo.pageSize()))
          .andExpect(jsonPath("$.totalElements").value(paginatedInfo.totalElements()))
          .andExpect(jsonPath("$.totalPages").value(paginatedInfo.totalPages()));
    }

    @Test
    @DisplayName("검색어를 사용하여 식당을 검색할 수 있다.")
    void searchWithText() throws Exception {
      // given
      String searchText = "맛있는";
      Long userId = 1L;

      SearchRestaurantsInfo info = SearchRestaurantsInfo.builder()
          .restaurantUuid(UUID.randomUUID().toString())
          .name("맛있는 식당")
          .build(); // 나머지 필드는 간결성을 위해 생략

      PaginatedInfo<SearchRestaurantsInfo> paginatedInfo = new PaginatedInfo<>(
          List.of(info), 1, 10, 1, 1
      );

      given(restaurantService.searchRestaurants(argThat(criteria ->
          criteria.searchText().equals(searchText))))
          .willReturn(paginatedInfo);

      // when & then
      mockMvc.perform(get(baseUrl)
              .header(USER_ID_HEADER, userId)
              .header(USER_ROLE_HEADER, UserRole.CUSTOMER)
              .queryParam("searchText", searchText)
              .queryParam("pageNumber", "0")
              .queryParam("sortBy", "id")
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.contents[0].name").value("맛있는 식당"));
    }

    @Test
    @DisplayName("잘못된 정렬 기준으로 요청 시 적절한 오류를 반환한다.")
    void searchWithInvalidSortBy() throws Exception {
      // given
      Long userId = 1L;
      String invalidSortBy = "invalid_field";

      given(restaurantService.searchRestaurants(any()))
          .willThrow(new IllegalArgumentException("Invalid sort field: " + invalidSortBy));

      // when & then
      mockMvc.perform(get(baseUrl)
              .header(USER_ID_HEADER, userId)
              .header(USER_ROLE_HEADER, UserRole.CUSTOMER)
              .queryParam("sortBy", invalidSortBy)
          )
          .andExpect(status().isBadRequest());
    }

  }


  @DisplayName("식당 타임슬롯 현재 인원 수 수정 컨트롤러")
  @Nested
  class modifyGuestCount{
    @Test
    @DisplayName("타임슬롯 인원 수정 요청이 성공적으로 처리된다.")
    void modifyGuestCount_success() throws Exception {
      // given
      String restaurantUuid = UUID.randomUUID().toString();
      String restaurantTimeSlotUuid = UUID.randomUUID().toString();
      int delta = 1;

      // restaurantService 호출이 정상 수행되도록 설정
      doNothing().when(restaurantService)
          .increaseOrDecreaseTimeSlotGuestCount(eq(restaurantTimeSlotUuid), eq(delta));

      // when & then
      Map<String, Integer> requestBody = new HashMap<>();
      requestBody.put("delta", delta);

      mockMvc.perform(patch(baseUrl + "/{restaurantUuid}/timeslot/{restaurantTimeSlotUuid}/cur-total-guest-count", restaurantUuid, restaurantTimeSlotUuid)
              .content(String.valueOf(delta))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("타임슬롯 인원 수정 시 예외가 발생하면 400을 반환한다.")
    void modifyGuestCount_failure_customException() throws Exception {
      // given
      String restaurantUuid = UUID.randomUUID().toString();
      String timeSlotUuid = UUID.randomUUID().toString();
      int delta = 100; // maxCapacity 초과를 유도

      doThrow(new CustomException(RestaurantTimeSlotErrorCode.EXCEEDS_CAPACITY))
          .when(restaurantService)
          .increaseOrDecreaseTimeSlotGuestCount(eq(timeSlotUuid), eq(delta));

      // when & then
      mockMvc.perform(patch(baseUrl + "/{restaurantUuid}/timeslot/{timeSlotUuid}/cur-total-guest-count", restaurantUuid, timeSlotUuid)
              .content(String.valueOf(delta))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }

  @DisplayName("직원 ID로 식당 조회 internal 컨트롤러")
  @Nested
  class getRestaurantByStaffId {

    @DisplayName("직원 ID로 식당을 조회할 수 있다.")
    @Test
    void success() throws Exception {
      // given
      Long staffId = 1L;
      String restaurantUuid = UUID.randomUUID().toString();

      GetRestaurantSimpleInfo response = GetRestaurantSimpleInfo.builder()
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
          .build();

      given(restaurantService.getRestaurantByStaffId(staffId))
          .willReturn(response);

      // when & then
      mockMvc.perform(get(baseUrl+"/my-restaurant")
              .header(USER_ID_HEADER, staffId)
              .header(USER_ROLE_HEADER, UserRole.STAFF)
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
          .andExpect(jsonPath("$.waitingStatus").value("ACTIVE"));
    }

    @DisplayName("존재하지 않는 직원 ID로 조회 시 404 응답을 반환한다.")
    @Test
    void fail_notFound() throws Exception {
      // given
      Long nonExistentStaffId = 999L;

      given(restaurantService.getRestaurantByStaffId(nonExistentStaffId))
          .willThrow(new CustomException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

      // when & then
      mockMvc.perform(get(baseUrl+"/my-restaurant")
              .header(USER_ID_HEADER, nonExistentStaffId)
              .header(USER_ROLE_HEADER, UserRole.STAFF)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }
}