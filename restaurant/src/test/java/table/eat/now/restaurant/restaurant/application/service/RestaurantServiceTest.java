/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.restaurant.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.global.IntegrationTestSupport;
import table.eat.now.restaurant.global.fixture.RestaurantFixture;
import table.eat.now.restaurant.global.fixture.RestaurantMenuFixture;
import table.eat.now.restaurant.global.fixture.RestaurantTimeSlotFixture;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantErrorCode;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantTimeSlotErrorCode;
import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant.RestaurantStatus;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu.MenuStatus;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantRepository;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantTimeSlotRepository;

class RestaurantServiceTest extends IntegrationTestSupport {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private RestaurantTimeSlotRepository restaurantTimeSlotRepository;

  @Autowired
  private RestaurantService restaurantService;

  @DisplayName("식당 생성 서비스")
  @Nested
  class create {

    @DisplayName("식당을 생성한다.")
    @Test
    void success() {
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

  @DisplayName("비활성화 상태의 식당 단건 조회 서비스")
  @Nested
  class getInactiveRestaurant {

    // 식당 1
    // menus
    int restaurant1InactiveMenuSize = 3;
    int restaurant1ActiveMenuSize = 3;
    Set<RestaurantMenu> menus = Stream.concat(
            RestaurantMenuFixture.createRandomsByStatus(
                MenuStatus.INACTIVE, restaurant1InactiveMenuSize).stream(),
            RestaurantMenuFixture.createRandomsByStatus(
                MenuStatus.ACTIVE, restaurant1ActiveMenuSize).stream())
        .collect(Collectors.toSet());
    // timeslot
    Set<RestaurantTimeSlot> timeSlots = RestaurantTimeSlotFixture.createRandoms(5);
    Restaurant inactiveRestaurant1 = RestaurantFixture.createRandomByStatusAndMenusAndTimeSlots(
        RestaurantStatus.INACTIVE, menus, timeSlots);
    // 식당 2
    // menus
    Set<RestaurantMenu> menus2 = Stream.concat(
            RestaurantMenuFixture.createRandomsByStatus(MenuStatus.INACTIVE, 3).stream(),
            RestaurantMenuFixture.createRandomsByStatus(MenuStatus.ACTIVE, 3).stream())
        .collect(Collectors.toSet());
    // timeslot
    Set<RestaurantTimeSlot> timeSlots2 = RestaurantTimeSlotFixture.createRandoms(5);

    Restaurant openedRestaurant2 = RestaurantFixture.createRandomByStatusAndMenusAndTimeSlots(
        RestaurantStatus.OPENED, menus2, timeSlots2);


    @DisplayName("MASTER는 INACTIVE 상태인 식당도 볼 수 있다.")
    @Test
    void success_master() {
      // given
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.MASTER, 1L);

      // when
      GetRestaurantInfo result = restaurantService.getRestaurant(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result)
          .extracting(
              GetRestaurantInfo::name,
              GetRestaurantInfo::address,
              GetRestaurantInfo::status
          )
          .containsExactly(
              inactiveRestaurant1.getName(),
              inactiveRestaurant1.getContactInfo().getAddress(),
              inactiveRestaurant1.getStatus().toString()
          );
      assertThat(result.menus()).hasSize(menus.size());
    }

    @DisplayName("식당의 OWNER는 해당 식당의 사장이면 INACTIVE 상태인 식당도 볼 수 있다.")
    @Test
    void success_owner() {
      // given
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.OWNER, inactiveRestaurant1.getOwnerId());

      // when
      GetRestaurantInfo result = restaurantService.getRestaurant(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result)
          .extracting(
              GetRestaurantInfo::name,
              GetRestaurantInfo::address,
              GetRestaurantInfo::status
          )
          .containsExactly(
              inactiveRestaurant1.getName(),
              inactiveRestaurant1.getContactInfo().getAddress(),
              inactiveRestaurant1.getStatus().toString()
          );
      assertThat(result.menus()).hasSize(menus.size());
    }

    @DisplayName("식당의 STAFF는 해당 식당의 소속이면 INACTIVE 상태인 식당도 볼 수 있다.")
    @Test
    void success_staff() {
      // given
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.STAFF, inactiveRestaurant1.getStaffId());

      // when
      GetRestaurantInfo result = restaurantService.getRestaurant(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result)
          .extracting(
              GetRestaurantInfo::name,
              GetRestaurantInfo::address,
              GetRestaurantInfo::status
          )
          .containsExactly(
              inactiveRestaurant1.getName(),
              inactiveRestaurant1.getContactInfo().getAddress(),
              inactiveRestaurant1.getStatus().toString()
          );
      assertThat(result.menus()).hasSize(menus.size());
    }

    @DisplayName("CUSTOMER는 INACTIVE 상태인 식당을 볼 수 없어 조회 시 예외가 발생한다.")
    @Test
    void fail_customer() {
      // given
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.CUSTOMER, 2L);

      // when & then
      assertThatThrownBy(() -> restaurantService.getRestaurant(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(
              RestaurantErrorCode.RESTAURANT_NOT_FOUND.getMessage());
    }

    @DisplayName("OWNER는 본인의 식당이 아닌 INACTIVE 상태인 식당을 볼 수 없어 조회 시 예외가 발생한다.")
    @Test
    void fail_owner() {
      // given
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.OWNER,
          inactiveRestaurant1.getOwnerId() + 1);

      // when & then
      assertThatThrownBy(() -> restaurantService.getRestaurant(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(
              RestaurantErrorCode.RESTAURANT_NOT_FOUND.getMessage());
    }

    @DisplayName("STAFF는 소속 식당이 아닌 INACTIVE 상태인 식당을 볼 수 없어 조회 시 예외가 발생한다.")
    @Test
    void fail_staff() {
      // given
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.STAFF, inactiveRestaurant1.getStaffId()+1);

      // when & then
      assertThatThrownBy(() -> restaurantService.getRestaurant(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(
              RestaurantErrorCode.RESTAURANT_NOT_FOUND.getMessage());
    }
  }

  @DisplayName("soft delete 된 식당 단건 조회 서비스")
  @Nested
  class getDeletedRestaurant {

    // 식당 1
    // menus
    int restaurant1InactiveMenuSize = 3;
    int restaurant1ActiveMenuSize = 3;
    Set<RestaurantMenu> menus = Stream.concat(
            RestaurantMenuFixture.createRandomsByStatus(
                MenuStatus.INACTIVE, restaurant1InactiveMenuSize).stream(),
            RestaurantMenuFixture.createRandomsByStatus(
                MenuStatus.ACTIVE, restaurant1ActiveMenuSize).stream())
        .collect(Collectors.toSet());
    // timeslot
    Set<RestaurantTimeSlot> timeSlots = RestaurantTimeSlotFixture.createRandoms(5);
    Restaurant inactiveRestaurant1 = RestaurantFixture.createRandomByStatusAndMenusAndTimeSlots(
        RestaurantStatus.INACTIVE, menus, timeSlots);
    // 식당 2
    // menus
    Set<RestaurantMenu> menus2 = Stream.concat(
            RestaurantMenuFixture.createRandomsByStatus(MenuStatus.INACTIVE, 3).stream(),
            RestaurantMenuFixture.createRandomsByStatus(MenuStatus.ACTIVE, 3).stream())
        .collect(Collectors.toSet());
    // timeslot
    Set<RestaurantTimeSlot> timeSlots2 = RestaurantTimeSlotFixture.createRandoms(5);

    Restaurant openedRestaurant2 = RestaurantFixture.createRandomByStatusAndMenusAndTimeSlots(
        RestaurantStatus.OPENED, menus2, timeSlots2);


    @DisplayName("MASTER는 soft delete 된 식당도 볼 수 있다.")
    @Test
    void success_master() {
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedAt", LocalDateTime.now());
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedBy", 1L);
      // given
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.MASTER, 1L);

      // when
      GetRestaurantInfo result = restaurantService.getRestaurant(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result)
          .extracting(
              GetRestaurantInfo::name,
              GetRestaurantInfo::address,
              GetRestaurantInfo::status
          )
          .containsExactly(
              inactiveRestaurant1.getName(),
              inactiveRestaurant1.getContactInfo().getAddress(),
              inactiveRestaurant1.getStatus().toString()
          );
      assertThat(result.menus()).hasSize(menus.size());
    }

    @DisplayName("식당의 OWNER는 해당 식당의 사장이면 soft delete 된  식당도 볼 수 있다.")
    @Test
    void success_owner() {
      // given
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedAt", LocalDateTime.now());
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedBy", 1L);
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.OWNER, inactiveRestaurant1.getOwnerId());

      // when
      GetRestaurantInfo result = restaurantService.getRestaurant(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result)
          .extracting(
              GetRestaurantInfo::name,
              GetRestaurantInfo::address,
              GetRestaurantInfo::status
          )
          .containsExactly(
              inactiveRestaurant1.getName(),
              inactiveRestaurant1.getContactInfo().getAddress(),
              inactiveRestaurant1.getStatus().toString()
          );
      assertThat(result.menus()).hasSize(menus.size());
    }

    @DisplayName("식당의 STAFF는 해당 식당의 소속이어도 soft delete 된 식당은 볼 수 없어서 예외가 발생한다.")
    @Test
    void fail_staff() {
      // given
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedAt", LocalDateTime.now());
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedBy", 1L);
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.STAFF, inactiveRestaurant1.getStaffId());

      // when & then
      assertThatThrownBy(() -> restaurantService.getRestaurant(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(
              RestaurantErrorCode.RESTAURANT_NOT_FOUND.getMessage());
    }

    @DisplayName("CUSTOMER는 soft delete 된 식당은 볼 수 없어서 예외가 발생한다.")
    @Test
    void fail_customer() {
      // given
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedAt", LocalDateTime.now());
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedBy", 1L);
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.CUSTOMER, 2L);

      // when & then
      assertThatThrownBy(() -> restaurantService.getRestaurant(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(
              RestaurantErrorCode.RESTAURANT_NOT_FOUND.getMessage());
    }

    @DisplayName("OWNER는 본인의 식당이 아닌 soft delete 된 식당은 볼 수 없어 조회 시 예외가 발생한다.")
    @Test
    void fail_owner() {
      // given
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedAt", LocalDateTime.now());
      ReflectionTestUtils.setField(inactiveRestaurant1, "deletedBy", 1L);
      restaurantRepository.saveAll(List.of(inactiveRestaurant1, openedRestaurant2));
      GetRestaurantCriteria criteria = GetRestaurantCriteria.from(
          inactiveRestaurant1.getRestaurantUuid(),
          UserRole.OWNER,
          inactiveRestaurant1.getOwnerId() + 1);

      // when & then
      assertThatThrownBy(() -> restaurantService.getRestaurant(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(
              RestaurantErrorCode.RESTAURANT_NOT_FOUND.getMessage());
    }

  }

  @DisplayName("활성화 상태의 식당 단건 조회 서비스")
  @Nested
  class getActiveRestaurantWithInactiveMenus {

    @DisplayName("CUSTOMER는 ACTIVE 상태인 식당을 볼 수 있고 INACTIVE 메뉴는 볼 수 없다.")
    @Test
    void success_customer() {
      // given
      // menus
      int inactiveMenuCount = 2;
      int activeMenuCount = 3;
      Set<RestaurantMenu> menus = Stream.concat(
              RestaurantMenuFixture.createRandomsByStatus(MenuStatus.INACTIVE, inactiveMenuCount)
                  .stream(),
              RestaurantMenuFixture.createRandomsByStatus(MenuStatus.ACTIVE, activeMenuCount).stream())
          .collect(Collectors.toSet());

      // timeslot
      Set<RestaurantTimeSlot> timeSlots = RestaurantTimeSlotFixture.createRandoms(5);

      Restaurant openedRestaurant = RestaurantFixture.createRandomByStatusAndMenusAndTimeSlots(
          RestaurantStatus.OPENED, menus, timeSlots);

      restaurantRepository.saveAll(List.of(openedRestaurant));
      GetRestaurantCriteria criteria = GetRestaurantCriteria
          .from(openedRestaurant.getRestaurantUuid(), UserRole.CUSTOMER, 2L);

      // when
      GetRestaurantInfo result = restaurantService.getRestaurant(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result)
          .extracting(
              GetRestaurantInfo::name,
              GetRestaurantInfo::address,
              GetRestaurantInfo::status
          )
          .containsExactly(
              openedRestaurant.getName(),
              openedRestaurant.getContactInfo().getAddress(),
              openedRestaurant.getStatus().toString()
          );
      assertThat(result.menus()).hasSize(activeMenuCount);
    }

  }

  @DisplayName("식당 타임슬롯 현재 게스트 수 수정")
  @Nested
  class modifyTimeSlotGuestCount{
    RestaurantTimeSlot timeSlot;
    Restaurant restaurant;
    int maxCapacity = 10;
    int curTotalGuestCount = 5;

    @BeforeEach
    void setUp() {
      timeSlot = RestaurantTimeSlotFixture.createRandom();
      ReflectionTestUtils.setField(timeSlot, "maxCapacity", maxCapacity);
      ReflectionTestUtils.setField(timeSlot, "curTotalGuestCount", curTotalGuestCount); // 초기값 설정
      restaurant = RestaurantFixture.createRandomByStatusAndMenusAndTimeSlots(
          RestaurantStatus.OPENED, null, Set.of(timeSlot));
      restaurantRepository.save(restaurant);
    }

    public static Stream<Arguments> provideDeltaValuesForCheckingIncreaseAndDecrease() {
      return Stream.of(
          Arguments.of(1, 6),
          Arguments.of(2, 7),
          Arguments.of(-1, 4),
          Arguments.of(-2, 3)
      );
    }

    @DisplayName("delta 값에 따라 인원수가 정확히 증가/감소한다.")
    @ParameterizedTest(name = "delta: {0}, expectedCount: {1}")
    @MethodSource("provideDeltaValuesForCheckingIncreaseAndDecrease")
    void success_increaseAndDecrease(int delta, int expectedCount) {
      // when
      restaurantService.increaseOrDecreaseTimeSlotGuestCount(
          timeSlot.getRestaurantTimeslotUuid(),
          delta
      );

      // then
      RestaurantTimeSlot updated = restaurantTimeSlotRepository.findById(timeSlot.getId()).orElseThrow();
      assertThat(updated.getCurTotalGuestCount()).isEqualTo(expectedCount);
    }

    @DisplayName("동시성 환경에서도 최대 수용 인원 수(maxCapacity)을 초과하지 않는다.")
    @Test
    void success_동시성확인() throws InterruptedException {
      // given
      int threadCount = 20;
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

      String restaurantTimeSlotUuid = timeSlot.getRestaurantTimeslotUuid();

      // when
      List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
          .mapToObj(i -> CompletableFuture.runAsync(() -> {
            try {
              restaurantService.increaseOrDecreaseTimeSlotGuestCount(
                  restaurantTimeSlotUuid, 1
              );
            } catch (CustomException e) {
              // maxCapacity 초과 시 발생 → 무시 (정상 케이스)
            }
          }, executorService))
          .collect(Collectors.toList());

      // 모든 작업이 끝날 때까지 기다림
      futures.forEach(CompletableFuture::join);

      // then
      RestaurantTimeSlot updated = restaurantTimeSlotRepository.findById(timeSlot.getId())
          .orElseThrow();

      assertThat(updated.getCurTotalGuestCount())
          .isLessThanOrEqualTo(maxCapacity)
          .isGreaterThan(0);

      executorService.shutdown();
    }

    @DisplayName("존재하지 않는 타임슬롯을 수정하려고 하면 예외가 발생한다.")
    @Test
    void fail_notFound() {
      // given
      String invalidUuid = UUID.randomUUID().toString();

      // when & then
      assertThatThrownBy(() -> restaurantService.increaseOrDecreaseTimeSlotGuestCount(
          invalidUuid,
          1
      )).isInstanceOf(CustomException.class)
          .hasMessageContaining(RestaurantTimeSlotErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("현재 인원이 음수가 되도록 만드는 (delta)를 입력하면  예외가 발생한다.")
    @Test
    void fail_negativeGuestCount() {
      // when & then
      assertThatThrownBy(() -> restaurantService.increaseOrDecreaseTimeSlotGuestCount(
          timeSlot.getRestaurantTimeslotUuid(),
          curTotalGuestCount * -1 - 1
      )).isInstanceOf(CustomException.class)
          .hasMessageContaining(RestaurantTimeSlotErrorCode.EXCEEDS_CAPACITY.getMessage());
    }

    @DisplayName("최대 수용 인원 수(maxCapacity)을 초과하도록 만드는 (delta)를 입력하면 예외가 발생한다.")
    @Test
    void fail_exceedsMaxCapacity() {
      // when & then
      assertThatThrownBy(() -> restaurantService.increaseOrDecreaseTimeSlotGuestCount(
          timeSlot.getRestaurantTimeslotUuid(),
          maxCapacity + 1
      )).isInstanceOf(CustomException.class)
          .hasMessageContaining(RestaurantTimeSlotErrorCode.EXCEEDS_CAPACITY.getMessage());
    }
  }


}
