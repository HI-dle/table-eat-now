/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.reservation.global.IntegrationTestSupport;
import table.eat.now.reservation.global.fixture.ReservationFixture;
import table.eat.now.reservation.global.fixture.ReservationPaymentDetailFixture;
import table.eat.now.reservation.global.util.UuidMaker;
import table.eat.now.reservation.reservation.application.client.dto.response.CreatePaymentInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion.PromotionStatus;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CancelReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.RestaurantDetails;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.RestaurantMenuDetails;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.RestaurantTimeSlotDetails;
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.application.service.dto.response.CancelReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.GetReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.Reservation.ReservationStatus;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;
import table.eat.now.reservation.reservation.domain.repository.ReservationRepository;

class ReservationServiceTest extends IntegrationTestSupport {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private ReservationService reservationService;

  @DisplayName("예약 생성 서비스: 총 금액 검증")
  @Nested
  class create_valid_totalPrice {

    @DisplayName("결제 상세의 총 합이 제공된 totalPrice와 같지 않으면 예외가 발생한다.")
    @Test
    void fail_invalidPaymentDetailsTotalAmount() {
      // given
      UUID couponReferenceId = UuidMaker.makeUuid();

      // 제공된 총 금액
      int providedTotalPrice = 10000;

      // 결제 내용
      int couponDiscountAmount = 1000;
      int paymentPrice = 8000;

      // 메뉴가격
      int menuPrice = 10000;
      int menuQuantity = 1;

      CreateReservationCommand command = createCommandWithOneCouponAndOnePaymentAndTotalPriceAndMenuInfo(
          providedTotalPrice,
          couponReferenceId,
          couponDiscountAmount,
          paymentPrice,
          menuPrice,
          menuQuantity
      );

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(
              ReservationErrorCode.INVALID_PAYMENT_DETAILS_TOTAL_AMOUNT.getMessage());
    }

    @DisplayName("메뉴의 계산된 값이 제공된 totalPrice와 같지 않으면 예외가 발생한다.")
    @Test
    void fail_invalidMenuTotalAmount() {
      // given
      UUID couponReferenceId = UuidMaker.makeUuid();

      // 제공된 총 금액
      int providedTotalPrice = 10000;

      // 결제 내용
      int couponDiscountAmount = 2000;
      int paymentPrice = 8000;

      // 메뉴가격
      int menuPrice = 90000;
      int menuQuantity = 1;

      CreateReservationCommand command = createCommandWithOneCouponAndOnePaymentAndTotalPriceAndMenuInfo(
          providedTotalPrice,
          couponReferenceId,
          couponDiscountAmount,
          paymentPrice,
          menuPrice,
          menuQuantity
      );

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.INVALID_MENU_TOTAL_AMOUNT.getMessage());
    }

    private CreateReservationCommand createCommandWithOneCouponAndOnePaymentAndTotalPriceAndMenuInfo(
        int providedTotalPrice,
        UUID couponReferenceId,
        int couponDiscountAmount,
        int paymentPrice,
        int menuPrice,
        int menuQuantity
    ) {
      return CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(UUID.randomUUID().toString())
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .guestCount(2)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(providedTotalPrice))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name("비빔밥")
              .price(BigDecimal.valueOf(menuPrice))
              .quantity(menuQuantity)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(LocalDate.now())
              .timeslot(LocalTime.NOON)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(List.of(
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_COUPON,
                  couponReferenceId.toString(),
                  BigDecimal.valueOf(couponDiscountAmount)),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PAYMENT,
                  null,
                  BigDecimal.valueOf(paymentPrice))
          ))
          .build();
    }
  }

  @DisplayName("예약 생성 서비스: 식당 검증")
  @Nested
  class create_valid_restaurant {

    @DisplayName("제공된 restaurantTimeslotUuid와 일치하는 식당의 타임슬롯 정보가 없으면 예외가 발생한다.")
    @Test
    void fail_invalidTimeslot() {
      // given
      String providedRestaurantUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantTimeslotUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantMenuUuid = UuidMaker.makeUuid().toString();
      LocalDate providedAvailableDate = LocalDate.now();
      LocalTime providedTimeslot = LocalTime.NOON;
      int providedGuestCount = 2;
      String menuName = "비빔밥";
      BigDecimal menuPrice = BigDecimal.valueOf(10000);

      CreateReservationCommand command = createCommandWithRequestRestaurantInfo(
          providedRestaurantUuid,
          providedRestaurantTimeslotUuid,
          providedRestaurantMenuUuid,
          providedAvailableDate,
          providedTimeslot,
          providedGuestCount,
          menuName,
          menuPrice
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of())
          .timeslots(List.of()) // 일치하는 타임슬롯 없음
          .build();

      when(restaurantClient.getRestaurant(eq(providedRestaurantUuid))).thenReturn(mockRestaurant);

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.INVALID_TIMESLOT.getMessage());
    }

    @DisplayName("제공된 restaurantTimeslotUuid와 일치하는 타임슬롯의 날짜와 제공된 availableDate와 일치하지 않으면 예외가 발생한다.")
    @Test
    void fail_invalidReservationDate() {
      // given
      String providedRestaurantUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantTimeslotUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantMenuUuid = UuidMaker.makeUuid().toString();
      LocalDate providedAvailableDate = LocalDate.now();
      LocalTime providedTimeslot = LocalTime.NOON;
      int providedGuestCount = 2;
      String menuName = "비빔밥";
      BigDecimal menuPrice = BigDecimal.valueOf(10000);

      CreateReservationCommand command = createCommandWithRequestRestaurantInfo(
          providedRestaurantUuid,
          providedRestaurantTimeslotUuid,
          providedRestaurantMenuUuid,
          providedAvailableDate,
          providedTimeslot,
          providedGuestCount,
          menuName,
          menuPrice
      );

      GetRestaurantInfo.Timeslot wrongDateTimeslot = new GetRestaurantInfo.Timeslot(
          providedRestaurantTimeslotUuid,
          providedAvailableDate.plusDays(1), // 날짜 다름
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of())
          .timeslots(List.of(wrongDateTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.INVALID_RESERVATION_DATE.getMessage());
    }

    @DisplayName("제공된 restaurantTimeslotUuid와 일치하는 타임슬롯의 시간와 제공된 timeslot이 일치하지 않으면 예외가 발생한다.")
    @Test
    void fail_invalidReservationTime() {
      // given
      String providedRestaurantUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantTimeslotUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantMenuUuid = UuidMaker.makeUuid().toString();
      LocalDate providedAvailableDate = LocalDate.now();
      LocalTime providedTimeslot = LocalTime.NOON;
      int providedGuestCount = 2;
      String menuName = "비빔밥";
      BigDecimal menuPrice = BigDecimal.valueOf(10000);

      CreateReservationCommand command = createCommandWithRequestRestaurantInfo(
          providedRestaurantUuid,
          providedRestaurantTimeslotUuid,
          providedRestaurantMenuUuid,
          providedAvailableDate,
          providedTimeslot,
          providedGuestCount,
          menuName,
          menuPrice
      );

      GetRestaurantInfo.Timeslot wrongTimeTimeslot = new GetRestaurantInfo.Timeslot(
          providedRestaurantTimeslotUuid,
          providedAvailableDate,
          10,
          0,
          LocalTime.of(18, 0) // 시간 다름
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of())
          .timeslots(List.of(wrongTimeTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.INVALID_RESERVATION_TIME.getMessage());
    }

    @DisplayName("해당 타임슬롯의 (현재 인원 수와 신청 인원수)보다 최대 인원수보다 크면 예외가 발생한다.")
    @Test
    void fail_exceedsMaxGuestCapacity() {
      // given
      String providedRestaurantUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantTimeslotUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantMenuUuid = UuidMaker.makeUuid().toString();
      LocalDate providedAvailableDate = LocalDate.now();
      LocalTime providedTimeslot = LocalTime.NOON;
      int providedGuestCount = 2;
      String menuName = "비빔밥";
      BigDecimal menuPrice = BigDecimal.valueOf(10000);

      CreateReservationCommand command = createCommandWithRequestRestaurantInfo(
          providedRestaurantUuid,
          providedRestaurantTimeslotUuid,
          providedRestaurantMenuUuid,
          providedAvailableDate,
          providedTimeslot,
          providedGuestCount,
          menuName,
          menuPrice
      );

      GetRestaurantInfo.Timeslot fullTimeslot = new GetRestaurantInfo.Timeslot(
          providedRestaurantTimeslotUuid,
          providedAvailableDate,
          3, // 최대 수용 인원
          2, // 현재 인원
          providedTimeslot
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of())
          .timeslots(List.of(fullTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.EXCEEDS_MAX_GUEST_CAPACITY.getMessage());
    }

    @DisplayName("식당에 예약 신청한 메뉴와 일치하는 정보가 없으면 예외가 발생한다.")
    @Test
    void fail_invalidMenuSelection() {
      // given
      String providedRestaurantUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantTimeslotUuid = UuidMaker.makeUuid().toString();
      String providedRestaurantMenuUuid = UuidMaker.makeUuid().toString();
      LocalDate providedAvailableDate = LocalDate.now();
      LocalTime providedTimeslot = LocalTime.NOON;
      int providedGuestCount = 2;
      String menuName = "비빔밥";
      BigDecimal menuPrice = BigDecimal.valueOf(10000);

      CreateReservationCommand command = createCommandWithRequestRestaurantInfo(
          providedRestaurantUuid,
          providedRestaurantTimeslotUuid,
          providedRestaurantMenuUuid,
          providedAvailableDate,
          providedTimeslot,
          providedGuestCount,
          menuName,
          menuPrice
      );

      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          providedRestaurantTimeslotUuid,
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      // 이름이나 price가 다르게 설정됨
      GetRestaurantInfo.Menu mismatchedMenu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          "된장찌개", // 이름 다름
          BigDecimal.valueOf(8000),     // 가격 다름
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(mismatchedMenu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.INVALID_MENU_SELECTION.getMessage());
    }

    private CreateReservationCommand createCommandWithRequestRestaurantInfo(
        String restaurantUuid,
        String restaurantTimeslotUuid,
        String restaurantMenuUuid,
        LocalDate availableDate,
        LocalTime timeslot,
        int guestCount,
        String menuName,
        BigDecimal menuPrice
    ) {
      return CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(restaurantUuid)
          .restaurantTimeslotUuid(restaurantTimeslotUuid)
          .restaurantMenuUuid(restaurantMenuUuid)
          .guestCount(guestCount)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(10000))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name(menuName)
              .price(menuPrice)
              .quantity(1)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(availableDate)
              .timeslot(timeslot)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(List.of(
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_COUPON,
                  UUID.randomUUID().toString(),
                  BigDecimal.valueOf(1000)),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_EVENT,
                  UUID.randomUUID().toString(),
                  BigDecimal.valueOf(2000)),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PAYMENT,
                  null,
                  BigDecimal.valueOf(7000))
          ))
          .build();
    }
  }

  @DisplayName("예약 생성 서비스: 할인 정책 검증")
  @Nested
  class create_valid_discount {

    @DisplayName("쿠폰 사용 개수가 2개를 초과하면 예외가 발생한다.")
    @Test
    void fail_couponUsageLimitExceeded() {
      // given
      CreateReservationCommand command = createCommandWithCoupons(List.of(
          UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()
          // 총 3개로 초과
      ));

      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu validMenu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(validMenu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.COUPON_USAGE_LIMIT_EXCEEDED.getMessage());
    }

    @DisplayName("프로모션 할인 개수가 1개를 초과하면 예외가 발생한다.")
    @Test
    void fail_promotionUsageLimitExceeded() {
      // given
      CreateReservationCommand command = createCommandWithPromotions(List.of(
          UUID.randomUUID().toString(), UUID.randomUUID().toString() // 총 2개로 초과
      ));

      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu validMenu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(validMenu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.PROMOTION_USAGE_LIMIT_EXCEEDED.getMessage());
    }

    @DisplayName("유효하지 않는 쿠폰이면 예외가 발생한다.")
    @Test
    void fail_couponNotFound() {
      // given
      CreateReservationCommand command = createCommandCouponPaymentInfo(
          UUID.randomUUID().toString(), BigDecimal.valueOf(3000)
      );

      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu mismatchedMenu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(mismatchedMenu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 정보는 없다고 가정 (비어 있는 맵)
      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap(Collections.emptyMap())
              .build());

      // 프로모션 정보도 없다고 가정 (비어 있는 맵)
      when(promotionClient.getPromotions(any()))
          .thenReturn(new GetPromotionsInfo(Collections.emptyMap()));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.COUPON_NOT_FOUND.getMessage());
    }

    @DisplayName("예약일이 쿠폰 행사 시작일 전이면 예외가 발생한다.")
    @Test
    void fail_couponInvalidPeriod_isBeforeStartAt() {
      // given
      String couponUuid = UUID.randomUUID().toString();
      CreateReservationCommand command = createCommandCouponPaymentInfo(
          couponUuid, BigDecimal.valueOf(3000));

      // 예약일
      LocalDateTime reservationDateTime = command.restaurantTimeSlotDetails().availableDate()
          .atTime(command.restaurantTimeSlotDetails().timeslot());

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 정보
      GetCouponsInfo.Coupon invalidPeriodCoupon = GetCouponsInfo.Coupon.builder()
          .couponUuid(couponUuid)
          .type(GetCouponsInfo.Coupon.CouponType.FIXED_DISCOUNT)
          .startAt(reservationDateTime.plusDays(1)) // 시작일: 예약일보다 미래
          .endAt(reservationDateTime.plusDays(1))    // 종료일
          .minPurchaseAmount(10000)
          .amount(3000)
          .percent(null)
          .maxDiscountAmount(null)
          .build();

      Map<String, Coupon> couponMap = Map.of(
          couponUuid, invalidPeriodCoupon
      );

      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap(couponMap)
              .build());

      // 프로모션은 없어도 됨
      when(promotionClient.getPromotions(any()))
          .thenReturn(new GetPromotionsInfo(Collections.emptyMap()));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.COUPON_INVALID_PERIOD.getMessage());
    }

    @DisplayName("예약일이 쿠폰 행사 마감일 후이면 예외가 발생한다.")
    @Test
    void fail_couponInvalidPeriod_isAfterEndAt() {
      // given
      String couponUuid = UUID.randomUUID().toString();
      CreateReservationCommand command = createCommandCouponPaymentInfo(
          couponUuid, BigDecimal.valueOf(3000));

      // 예약일
      LocalDateTime reservationDateTime = command.restaurantTimeSlotDetails().availableDate()
          .atTime(command.restaurantTimeSlotDetails().timeslot());

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 정보
      GetCouponsInfo.Coupon invalidPeriodCoupon = GetCouponsInfo.Coupon.builder()
          .couponUuid(couponUuid)
          .type(GetCouponsInfo.Coupon.CouponType.FIXED_DISCOUNT)
          .startAt(reservationDateTime.minusDays(10)) // 시작일
          .endAt(reservationDateTime.minusDays(1))    // 종료일: 예약일보다 과거
          .minPurchaseAmount(10000)
          .amount(3000)
          .percent(null)
          .maxDiscountAmount(null)
          .build();

      Map<String, Coupon> couponMap = Map.of(
          couponUuid, invalidPeriodCoupon
      );

      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap(couponMap)
              .build());

      // 프로모션은 없어도 됨
      when(promotionClient.getPromotions(any()))
          .thenReturn(new GetPromotionsInfo(Collections.emptyMap()));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.COUPON_INVALID_PERIOD.getMessage());
    }

    @DisplayName("쿠폰의 최소 구매 금액보다 totalPrice가 작으면 예외가 발생한다.")
    @Test
    void fail_couponMinPurchaseNotMet() {
      // given
      String couponUuid = UUID.randomUUID().toString();
      CreateReservationCommand command = createCommandCouponPaymentInfo(
          couponUuid, BigDecimal.valueOf(3000));

      // 예약일
      LocalDateTime reservationDateTime = command.restaurantTimeSlotDetails().availableDate()
          .atTime(command.restaurantTimeSlotDetails().timeslot());

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 정보
      GetCouponsInfo.Coupon invalidMinPurchaseAmountCoupon = GetCouponsInfo.Coupon.builder()
          .couponUuid(couponUuid)
          .type(GetCouponsInfo.Coupon.CouponType.FIXED_DISCOUNT)
          .startAt(reservationDateTime.minusDays(10))
          .endAt(reservationDateTime.plusDays(1))
          .minPurchaseAmount(10001) // total 금액이 더 작다.
          .amount(3000)
          .percent(null)
          .maxDiscountAmount(null)
          .build();

      Map<String, Coupon> couponMap = Map.of(
          couponUuid, invalidMinPurchaseAmountCoupon
      );

      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap(couponMap)
              .build());

      // 프로모션은 없어도 됨
      when(promotionClient.getPromotions(any()))
          .thenReturn(new GetPromotionsInfo(Collections.emptyMap()));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.COUPON_MIN_PURCHASE_NOT_MET.getMessage());
    }

    @DisplayName("적용된 쿠폰 할인 금액이 실제 할인 금액과 일치하지 않으면 예외가 발생한다.")
    @Test
    void fail_invalidCouponDiscount() {
      // given
      String couponUuid = UUID.randomUUID().toString();
      CreateReservationCommand command = createCommandCouponPaymentInfo(
          couponUuid, BigDecimal.valueOf(3000));

      // 예약일
      LocalDateTime reservationDateTime = command.restaurantTimeSlotDetails().availableDate()
          .atTime(command.restaurantTimeSlotDetails().timeslot());

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 정보
      GetCouponsInfo.Coupon invalidAmountCoupon = GetCouponsInfo.Coupon.builder()
          .couponUuid(couponUuid)
          .type(GetCouponsInfo.Coupon.CouponType.FIXED_DISCOUNT)
          .startAt(reservationDateTime.minusDays(10))
          .endAt(reservationDateTime.plusDays(1))
          .minPurchaseAmount(9999)
          .amount(4000)
          .percent(null)
          .maxDiscountAmount(null)
          .build();

      Map<String, Coupon> couponMap = Map.of(
          couponUuid, invalidAmountCoupon
      );

      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap(couponMap)
              .build());

      // 프로모션은 없어도 됨
      when(promotionClient.getPromotions(any()))
          .thenReturn(new GetPromotionsInfo(Collections.emptyMap()));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.INVALID_COUPON_DISCOUNT.getMessage());
    }

    @DisplayName("유효하지 않은 프로모션이면 예외가 발생한다.")
    @Test
    void fail_promotionNotFound() {
      // given
      String promotionUuid = UUID.randomUUID().toString();
      CreateReservationCommand command = createPromotionPaymentInfo(
          promotionUuid, BigDecimal.valueOf(3000));

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 없어도 됨
      when(couponClient.getCoupons(any())).thenReturn(GetCouponsInfo.builder()
          .couponMap((Collections.emptyMap()))
          .build());
      when(promotionClient.getPromotions(any())).thenReturn(
          new GetPromotionsInfo(Collections.emptyMap()));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.PROMOTION_NOT_FOUND.getMessage());
    }

    @DisplayName("진행중인 프로모션이 아니면 예외가 발생한다.")
    @Test
    void fail_promotionInvalidRunning() {
      // given
      String promotionUuid = UUID.randomUUID().toString();
      CreateReservationCommand command = createPromotionPaymentInfo(
          promotionUuid, BigDecimal.valueOf(3000));

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);
      // 프로모션 정보
      GetPromotionsInfo.Promotion promotion = GetPromotionsInfo.Promotion.builder()
          .promotionUuid(promotionUuid)
          .discountPrice(BigDecimal.valueOf(3000))
          .promotionStatus(PromotionStatus.READY)
          .promotionRestaurantUuid(command.restaurantUuid())
          .build();

      Map<String, Promotion> promotionMap = Map.of(promotionUuid, promotion);

      // 쿠폰 없어도 됨
      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap((Collections.emptyMap()))
              .build());

      when(promotionClient.getPromotions(any())).thenReturn(new GetPromotionsInfo(promotionMap));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.PROMOTION_INVALID_RUNNING.getMessage());
    }

    @DisplayName("적용된 프로모션 할인 금액이 실제 할인 금액과 일치하지 않으면 예외가 발생한다.")
    @Test
    void fail_invalidPromotionDiscount() {
      // given
      String promotionUuid = UUID.randomUUID().toString();
      CreateReservationCommand command = createPromotionPaymentInfo(
          promotionUuid, BigDecimal.valueOf(3000));

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);
      // 프로모션 정보
      GetPromotionsInfo.Promotion promotion = GetPromotionsInfo.Promotion.builder()
          .promotionUuid(promotionUuid)
          .discountPrice(BigDecimal.valueOf(4000))
          .promotionStatus(PromotionStatus.RUNNING)
          .promotionRestaurantUuid(command.restaurantUuid())
          .build();

      Map<String, Promotion> promotionMap = Map.of(promotionUuid, promotion);

      // 쿠폰 없어도 됨
      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap((Collections.emptyMap()))
              .build());

      when(promotionClient.getPromotions(any())).thenReturn(new GetPromotionsInfo(promotionMap));

      // when & then
      assertThatThrownBy(() -> reservationService.createReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.INVALID_PROMOTION_DISCOUNT.getMessage());
    }

    private CreateReservationCommand createCommandWithPromotions(List<String> promotionIds) {
      List<CreateReservationCommand.PaymentDetail> payments = new ArrayList<>();

      for (String promotionId : promotionIds) {
        payments.add(new CreateReservationCommand.PaymentDetail(
            PaymentType.PROMOTION_EVENT,
            promotionId,
            BigDecimal.valueOf(1000)
        ));
      }

      // 나머지는 일반 결제 처리
      payments.add(new CreateReservationCommand.PaymentDetail(
          PaymentType.PAYMENT,
          null,
          BigDecimal.valueOf(10000 - (1000L * promotionIds.size()))
      ));

      return baseCommand(payments);
    }

    private CreateReservationCommand createCommandWithCoupons(List<String> couponUuids) {
      List<CreateReservationCommand.PaymentDetail> payments = new ArrayList<>();

      for (String couponUuid : couponUuids) {
        payments.add(new CreateReservationCommand.PaymentDetail(
            PaymentType.PROMOTION_COUPON,
            couponUuid,
            BigDecimal.valueOf(1000)
        ));
      }

      // 나머지는 일반 결제 처리
      payments.add(new CreateReservationCommand.PaymentDetail(
          PaymentType.PAYMENT,
          null,
          BigDecimal.valueOf(10000 - (1000L * couponUuids.size()))
      ));

      return baseCommand(payments);
    }

    private CreateReservationCommand createCommandCouponPaymentInfo(String couponUuid,
        BigDecimal amount) {
      return CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(UUID.randomUUID().toString())
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .guestCount(2)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(10000))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name("비빔밥")
              .price(BigDecimal.valueOf(10000))
              .quantity(1)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(LocalDate.now())
              .timeslot(LocalTime.NOON)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(List.of(
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_COUPON,
                  couponUuid,
                  amount),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PAYMENT,
                  null,
                  BigDecimal.valueOf(7000))
          ))
          .build();
    }

    private CreateReservationCommand createPromotionPaymentInfo(String promotionUuid,
        BigDecimal amount) {
      return CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(UUID.randomUUID().toString())
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .guestCount(2)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(10000))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name("비빔밥")
              .price(BigDecimal.valueOf(10000))
              .quantity(1)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(LocalDate.now())
              .timeslot(LocalTime.NOON)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(List.of(
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_EVENT,
                  promotionUuid,
                  amount),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PAYMENT,
                  null,
                  BigDecimal.valueOf(7000))
          ))
          .build();
    }

    private CreateReservationCommand baseCommand(
        List<CreateReservationCommand.PaymentDetail> payments) {
      return CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(UUID.randomUUID().toString())
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .guestCount(2)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(10000))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name("비빔밥")
              .price(BigDecimal.valueOf(10000))
              .quantity(1)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(LocalDate.now())
              .timeslot(LocalTime.NOON)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(payments)
          .build();
    }

  }

  @DisplayName("예약 생성 서비스: 성공")
  @Nested
  class create_success {

    @DisplayName("홍길동이 식당에 정액 할인 쿠폰 1개를 사용하여 비빔밥 1개 예약을 신청한다.")
    @Test
    void success_fixedDiscountCoupon1() {
      // given
      String couponUuid = UUID.randomUUID().toString();
      BigDecimal paymentAmount = BigDecimal.valueOf(7000);
      BigDecimal couponAmount = BigDecimal.valueOf(3000);
      CreateReservationCommand command = CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(UUID.randomUUID().toString())
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .guestCount(2)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(10000))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name("비빔밥")
              .price(BigDecimal.valueOf(10000))
              .quantity(1)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(LocalDate.now())
              .timeslot(LocalTime.NOON)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(List.of(
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_COUPON,
                  couponUuid,
                  couponAmount),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PAYMENT,
                  null,
                  paymentAmount)
          ))
          .build();

      // 예약일
      LocalDateTime reservationDateTime = command.restaurantTimeSlotDetails().availableDate()
          .atTime(command.restaurantTimeSlotDetails().timeslot());

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 정보
      GetCouponsInfo.Coupon invalidAmountCoupon = GetCouponsInfo.Coupon.builder()
          .couponUuid(couponUuid)
          .type(GetCouponsInfo.Coupon.CouponType.FIXED_DISCOUNT)
          .startAt(reservationDateTime.minusDays(10))
          .endAt(reservationDateTime.plusDays(1))
          .minPurchaseAmount(9999)
          .amount(3000)
          .percent(null)
          .maxDiscountAmount(null)
          .build();

      Map<String, Coupon> couponMap = Map.of(
          couponUuid, invalidAmountCoupon
      );

      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap(couponMap)
              .build());

      // 프로모션은 없어도 됨
      when(promotionClient.getPromotions(any()))
          .thenReturn(new GetPromotionsInfo(Collections.emptyMap()));

      CreatePaymentInfo paymentInfo = new CreatePaymentInfo(
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString()
      );

      when(paymentClient.createPayment(any()))
          .thenReturn(paymentInfo);

      // when
      reservationService.createReservation(command);

      // then
      List<Reservation> all = reservationRepository.findAll();
      assertThat(all).isNotNull();
      assertThat(all).isNotEmpty();
      Reservation result = all.get(0);
      // 기본 정보 검증
      assertThat(result)
          .extracting(
              Reservation::getName,
              Reservation::getReserverId,
              Reservation::getRestaurantTimeSlotUuid,
              Reservation::getRestaurantId,
              Reservation::getStatus,
              Reservation::getSpecialRequest
          )
          .containsExactlyInAnyOrder(
              command.getReservationName(),
              command.reserverId(),
              command.restaurantTimeslotUuid(),
              command.restaurantUuid(),
              ReservationStatus.PENDING_PAYMENT,
              command.specialRequest()
          );
      assertThat(result.getRestaurantMenuDetails().getPrice())
          .usingComparator(BigDecimal::compareTo)
          .isEqualTo(command.totalPrice());

      // 메뉴 정보 검증
      assertThat(result.getRestaurantMenuDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getName,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getPrice,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getQuantity
          )
          .containsExactly(
              command.restaurantMenuDetails().name(),
              command.restaurantMenuDetails().price(),
              command.restaurantMenuDetails().quantity()
          );

      // 레스토랑 정보 검증
      assertThat(result.getRestaurantDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getName,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getAddress,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getContactNumber,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getOpeningTime,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getClosingTime
          )
          .containsExactly(
              command.restaurantDetails().name(),
              command.restaurantDetails().address(),
              command.restaurantDetails().contactNumber(),
              command.restaurantDetails().openingTime(),
              command.restaurantDetails().closingTime()
          );

      // 타임슬롯 정보 검증
      assertThat(result.getRestaurantTimeSlotDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantTimeSlotDetails::getAvailableDate,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantTimeSlotDetails::getTimeslot
          )
          .containsExactly(
              command.restaurantTimeSlotDetails().availableDate(),
              command.restaurantTimeSlotDetails().timeslot()
          );
    }

    @DisplayName("홍길동이 식당에 정률 할인 쿠폰 1개를 사용하여 비빔밥 1개 예약을 신청한다.")
    @Test
    void success_PercentDiscount1() {
      // given
      String couponUuid = UUID.randomUUID().toString();
      BigDecimal paymentAmount = BigDecimal.valueOf(7000);
      BigDecimal couponAmount = BigDecimal.valueOf(3000);
      CreateReservationCommand command = CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(UUID.randomUUID().toString())
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .guestCount(2)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(10000))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name("비빔밥")
              .price(BigDecimal.valueOf(10000))
              .quantity(1)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(LocalDate.now())
              .timeslot(LocalTime.NOON)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(List.of(
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_COUPON,
                  couponUuid,
                  couponAmount),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PAYMENT,
                  null,
                  paymentAmount)
          ))
          .build();

      // 예약일
      LocalDateTime reservationDateTime = command.restaurantTimeSlotDetails().availableDate()
          .atTime(command.restaurantTimeSlotDetails().timeslot());

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);

      // 쿠폰 정보
      GetCouponsInfo.Coupon invalidAmountCoupon = GetCouponsInfo.Coupon.builder()
          .couponUuid(couponUuid)
          .type(GetCouponsInfo.Coupon.CouponType.PERCENT_DISCOUNT)
          .startAt(reservationDateTime.minusDays(10))
          .endAt(reservationDateTime.plusDays(1))
          .minPurchaseAmount(9999)
          .amount(0)
          .percent(30)
          .maxDiscountAmount(3000)
          .build();

      Map<String, Coupon> couponMap = Map.of(
          couponUuid, invalidAmountCoupon
      );

      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap(couponMap)
              .build());

      // 프로모션은 없어도 됨
      when(promotionClient.getPromotions(any()))
          .thenReturn(new GetPromotionsInfo(Collections.emptyMap()));

      CreatePaymentInfo paymentInfo = new CreatePaymentInfo(
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString()
      );

      when(paymentClient.createPayment(any()))
          .thenReturn(paymentInfo);

      // when
      reservationService.createReservation(command);

      // then
      List<Reservation> all = reservationRepository.findAll();
      assertThat(all).isNotNull();
      assertThat(all).isNotEmpty();
      Reservation result = all.get(0);
      // 기본 정보 검증
      assertThat(result)
          .extracting(
              Reservation::getName,
              Reservation::getReserverId,
              Reservation::getRestaurantTimeSlotUuid,
              Reservation::getRestaurantId,
              Reservation::getStatus,
              Reservation::getSpecialRequest
          )
          .containsExactlyInAnyOrder(
              command.getReservationName(),
              command.reserverId(),
              command.restaurantTimeslotUuid(),
              command.restaurantUuid(),
              ReservationStatus.PENDING_PAYMENT,
              command.specialRequest()
          );
      assertThat(result.getRestaurantMenuDetails().getPrice())
          .usingComparator(BigDecimal::compareTo)
          .isEqualTo(command.totalPrice());

      // 메뉴 정보 검증
      assertThat(result.getRestaurantMenuDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getName,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getPrice,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getQuantity
          )
          .containsExactly(
              command.restaurantMenuDetails().name(),
              command.restaurantMenuDetails().price(),
              command.restaurantMenuDetails().quantity()
          );

      // 레스토랑 정보 검증
      assertThat(result.getRestaurantDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getName,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getAddress,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getContactNumber,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getOpeningTime,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getClosingTime
          )
          .containsExactly(
              command.restaurantDetails().name(),
              command.restaurantDetails().address(),
              command.restaurantDetails().contactNumber(),
              command.restaurantDetails().openingTime(),
              command.restaurantDetails().closingTime()
          );

      // 타임슬롯 정보 검증
      assertThat(result.getRestaurantTimeSlotDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantTimeSlotDetails::getAvailableDate,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantTimeSlotDetails::getTimeslot
          )
          .containsExactly(
              command.restaurantTimeSlotDetails().availableDate(),
              command.restaurantTimeSlotDetails().timeslot()
          );
    }

    @DisplayName("홍길동이 식당에 식당 프로모션 이벤트 할인을 이용하여 비빔밥 1개 예약을 신청한다.")
    @Test
    void fail_promotionInvalidRunning() {
      // given
      String promotionUuid = UUID.randomUUID().toString();
      BigDecimal paymentAmount = BigDecimal.valueOf(7000);
      BigDecimal promotionAmount = BigDecimal.valueOf(3000);
      CreateReservationCommand command = CreateReservationCommand.builder()
          .reserverId(1L)
          .reserverName("홍길동")
          .reserverContact("010-0000-0000")
          .restaurantUuid(UUID.randomUUID().toString())
          .restaurantTimeslotUuid(UUID.randomUUID().toString())
          .restaurantMenuUuid(UUID.randomUUID().toString())
          .guestCount(2)
          .specialRequest("창가 자리")
          .totalPrice(BigDecimal.valueOf(10000))
          .restaurantMenuDetails(RestaurantMenuDetails.builder()
              .name("비빔밥")
              .price(BigDecimal.valueOf(10000))
              .quantity(1)
              .build())
          .restaurantDetails(RestaurantDetails.builder()
              .name("맛있는 식당")
              .address("서울시 강남구")
              .contactNumber("02-000-0000")
              .openingTime(LocalTime.of(9, 0))
              .closingTime(LocalTime.of(21, 0))
              .build())
          .restaurantTimeSlotDetails(RestaurantTimeSlotDetails.builder()
              .availableDate(LocalDate.now())
              .timeslot(LocalTime.NOON)
              .build())
          .reservationDate(LocalDateTime.now())
          .payments(List.of(
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PROMOTION_EVENT,
                  promotionUuid,
                  promotionAmount
              ),
              new CreateReservationCommand.PaymentDetail(
                  PaymentType.PAYMENT,
                  null,
                  paymentAmount)
          ))
          .build();

      // 예약일
      LocalDateTime reservationDateTime = command.restaurantTimeSlotDetails().availableDate()
          .atTime(command.restaurantTimeSlotDetails().timeslot());

      // 식당 정보 설정
      GetRestaurantInfo.Timeslot validTimeslot = new GetRestaurantInfo.Timeslot(
          command.restaurantTimeslotUuid(),
          command.restaurantTimeSlotDetails().availableDate(),
          10,
          0,
          command.restaurantTimeSlotDetails().timeslot()
      );

      GetRestaurantInfo.Menu menu = new GetRestaurantInfo.Menu(
          command.restaurantMenuUuid(),
          command.restaurantMenuDetails().name(),
          command.restaurantMenuDetails().price(),
          "AVAILABLE"
      );

      GetRestaurantInfo mockRestaurant = GetRestaurantInfo.builder()
          .restaurantUuid(command.restaurantUuid())
          .menus(List.of(menu))
          .timeslots(List.of(validTimeslot))
          .build();

      when(restaurantClient.getRestaurant(any())).thenReturn(mockRestaurant);
      // 프로모션 정보
      GetPromotionsInfo.Promotion promotion = GetPromotionsInfo.Promotion.builder()
          .promotionUuid(promotionUuid)
          .discountPrice(BigDecimal.valueOf(3000))
          .promotionStatus(PromotionStatus.RUNNING)
          .promotionRestaurantUuid(command.restaurantUuid())
          .build();

      Map<String, Promotion> promotionMap = Map.of(promotionUuid, promotion);

      // 쿠폰 없어도 됨
      when(couponClient.getCoupons(any()))
          .thenReturn(GetCouponsInfo.builder()
              .couponMap((Collections.emptyMap()))
              .build());

      when(promotionClient.getPromotions(any())).thenReturn(new GetPromotionsInfo(promotionMap));

      CreatePaymentInfo paymentInfo = new CreatePaymentInfo(
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString()
      );

      when(paymentClient.createPayment(any()))
          .thenReturn(paymentInfo);

      // when
      reservationService.createReservation(command);

      // then
      List<Reservation> all = reservationRepository.findAll();
      assertThat(all).isNotNull();
      assertThat(all).isNotEmpty();
      Reservation result = all.get(0);
      // 기본 정보 검증
      assertThat(result)
          .extracting(
              Reservation::getName,
              Reservation::getReserverId,
              Reservation::getRestaurantTimeSlotUuid,
              Reservation::getRestaurantId,
              Reservation::getStatus,
              Reservation::getSpecialRequest
          )
          .containsExactlyInAnyOrder(
              command.getReservationName(),
              command.reserverId(),
              command.restaurantTimeslotUuid(),
              command.restaurantUuid(),
              ReservationStatus.PENDING_PAYMENT,
              command.specialRequest()
          );
      assertThat(result.getRestaurantMenuDetails().getPrice())
          .usingComparator(BigDecimal::compareTo)
          .isEqualTo(command.totalPrice());

      // 메뉴 정보 검증
      assertThat(result.getRestaurantMenuDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getName,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getPrice,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails::getQuantity
          )
          .containsExactly(
              command.restaurantMenuDetails().name(),
              command.restaurantMenuDetails().price(),
              command.restaurantMenuDetails().quantity()
          );

      // 레스토랑 정보 검증
      assertThat(result.getRestaurantDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getName,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getAddress,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getContactNumber,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getOpeningTime,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails::getClosingTime
          )
          .containsExactly(
              command.restaurantDetails().name(),
              command.restaurantDetails().address(),
              command.restaurantDetails().contactNumber(),
              command.restaurantDetails().openingTime(),
              command.restaurantDetails().closingTime()
          );

      // 타임슬롯 정보 검증
      assertThat(result.getRestaurantTimeSlotDetails())
          .extracting(
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantTimeSlotDetails::getAvailableDate,
              table.eat.now.reservation.reservation.domain.entity.json.RestaurantTimeSlotDetails::getTimeslot
          )
          .containsExactly(
              command.restaurantTimeSlotDetails().availableDate(),
              command.restaurantTimeSlotDetails().timeslot()
          );
    }
  }

  @DisplayName("예약 단건 조회 서비스 : 삭제 처리되지 않은 예약")
  @Nested
  class getReservation {

    Reservation reservation;
    Reservation deletedReservation;

    @BeforeEach
    void setUp() {
      reservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));
      ReflectionTestUtils.setField(reservation, "status", ReservationStatus.CONFIRMED);

      deletedReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));

      ReflectionTestUtils.setField(deletedReservation, "status", ReservationStatus.CANCELLED);
      ReflectionTestUtils.setField(deletedReservation, "deletedAt",
          LocalDateTime.now().minusDays(1));
      ReflectionTestUtils.setField(deletedReservation, "deletedBy", 1L);
      reservationRepository.saveAll(List.of(reservation, deletedReservation));
    }

    @DisplayName("마스터는 모든 예약을 볼 수 있다.")
    @Test
    void success_Reservation_master() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          reservation.getReservationUuid(),
          1L,
          UserRole.MASTER);

      // when
      GetReservationInfo result = reservationService.getReservation(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reservationUuid()).isEqualTo(reservation.getReservationUuid());
    }

    @DisplayName("OWNER 는 본인의 가게 예약을 볼 수 있다.")
    @Test
    void success_reservation_owner() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          reservation.getReservationUuid(),
          reservation.getRestaurantDetails().getOwnerId(),
          UserRole.OWNER);

      // when
      GetReservationInfo result = reservationService.getReservation(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reservationUuid()).isEqualTo(reservation.getReservationUuid());
    }

    @DisplayName("STAFF 는 본인의 가게 예약을 볼 수 있다.")
    @Test
    void success_reservation_staff() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          reservation.getReservationUuid(),
          reservation.getRestaurantDetails().getStaffId(),
          UserRole.STAFF);

      // when
      GetReservationInfo result = reservationService.getReservation(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reservationUuid()).isEqualTo(reservation.getReservationUuid());
    }

    @DisplayName("CUSTOMER 는 본인의 예약을 볼 수 있다.")
    @Test
    void success_customer() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          reservation.getReservationUuid(),
          reservation.getReserverId(),
          UserRole.CUSTOMER);

      // when
      GetReservationInfo result = reservationService.getReservation(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reservationUuid()).isEqualTo(reservation.getReservationUuid());
    }

    @DisplayName("없는 예약을 조회할 시 예외가 발생한다.")
    @Test
    void test() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria("invalid-uuid",
          1L,
          UserRole.MASTER);

      // when & then
      assertThatThrownBy(() -> reservationService.getReservation(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("OWNER 는 본인의 가게 예약이 아닌 예약 조회시 예외가 발생한다.")
    @Test
    void fail_owner() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          reservation.getReservationUuid(),
          reservation.getRestaurantDetails().getOwnerId() + 1,
          UserRole.OWNER);

      // when & then
      assertThatThrownBy(() -> reservationService.getReservation(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("STAFF 는 본인의 가게 예약이 아닌 예약 조회시 예외가 발생한다.")
    @Test
    void fail_staff() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          reservation.getReservationUuid(),
          reservation.getRestaurantDetails().getStaffId() + 1,
          UserRole.STAFF);

      // when & then
      assertThatThrownBy(() -> reservationService.getReservation(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("CUSTOMER 는 본인의 예약이 아닌 예약 조회시 예외가 발생한다.")
    @Test
    void fail_customer() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          reservation.getReservationUuid(),
          reservation.getReserverId() + 1,
          UserRole.CUSTOMER);

      // when & then
      assertThatThrownBy(() -> reservationService.getReservation(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }
  }

  @DisplayName("예약 단건 조회 서비스 : 삭제된 예약")
  @Nested
  class getReservation_deletedReservation {

    Reservation reservation;
    Reservation deletedReservation;

    @BeforeEach
    void setUp() {
      reservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));
      ReflectionTestUtils.setField(reservation, "status", ReservationStatus.CONFIRMED);

      deletedReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));

      ReflectionTestUtils.setField(deletedReservation, "status", ReservationStatus.CANCELLED);
      ReflectionTestUtils.setField(deletedReservation, "deletedAt",
          LocalDateTime.now().minusDays(1));
      ReflectionTestUtils.setField(deletedReservation, "deletedBy", 1L);
      reservationRepository.saveAll(List.of(reservation, deletedReservation));
    }

    @DisplayName("마스터는 삭제된 예약도 볼 수 있다.")
    @Test
    void success_master() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          deletedReservation.getReservationUuid(),
          1L,
          UserRole.MASTER);

      // when
      GetReservationInfo result = reservationService.getReservation(criteria);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reservationUuid()).isEqualTo(deletedReservation.getReservationUuid());
    }

    @DisplayName("OWNER 는 삭제된 예약을 보지 못한다.")
    @Test
    void fail_owner() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          deletedReservation.getReservationUuid(),
          deletedReservation.getRestaurantDetails().getOwnerId(),
          UserRole.OWNER);

      // when & then
      assertThatThrownBy(() -> reservationService.getReservation(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("STAFF 는 삭제된 예약을 보지 못한다.")
    @Test
    void fail_staff() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          deletedReservation.getReservationUuid(),
          deletedReservation.getRestaurantDetails().getStaffId(),
          UserRole.STAFF);

      // when & then
      assertThatThrownBy(() -> reservationService.getReservation(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("CUSTOMER 는 삭제된 예약을 보지 못한다.")
    @Test
    void fail_customer() {
      // given
      GetReservationCriteria criteria = new GetReservationCriteria(
          deletedReservation.getReservationUuid(),
          deletedReservation.getReserverId(),
          UserRole.CUSTOMER);

      // when & then
      assertThatThrownBy(() -> reservationService.getReservation(criteria))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }
  }

  @DisplayName("예약 취소 서비스: 성공")
  @Nested
  class cancel_success {

    Reservation confirmedReservation;
    Reservation canceledReservation;
    Reservation deletedReservation;

    @BeforeEach
    void setUp() {
      confirmedReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));
      ReflectionTestUtils.setField(confirmedReservation, "status", ReservationStatus.CONFIRMED);

      canceledReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));

      ReflectionTestUtils.setField(canceledReservation, "status", ReservationStatus.CANCELLED);

      deletedReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));

      ReflectionTestUtils.setField(deletedReservation, "status", ReservationStatus.CANCELLED);
      ReflectionTestUtils.setField(deletedReservation, "deletedAt",
          LocalDateTime.now().minusDays(1));
      ReflectionTestUtils.setField(deletedReservation, "deletedBy", 1L);
      reservationRepository.saveAll(List.of(confirmedReservation, canceledReservation, deletedReservation));
    }

    @DisplayName("MASTER 는 모든 예약을 취소할 수 있다.")
    @Test
    void success_master() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(1L)
          .userRole(UserRole.MASTER)
          .reason("한지훈의 단순변심")
          .build();

      // when
      CancelReservationInfo result = reservationService.cancelReservation(command);

      // then
      assertThat(result.reservationUuid()).isEqualTo(confirmedReservation.getReservationUuid());
      assertThat(result.status()).isEqualTo(ReservationStatus.CANCELLED.toString());
    }

    @DisplayName("OWNER 는 본인의 가게의 예약을 취소할 수 있다.")
    @Test
    void success_owner() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(confirmedReservation.getRestaurantDetails().getOwnerId())
          .userRole(UserRole.OWNER)
          .reason("황하온의 변덕")
          .build();

      // when
      CancelReservationInfo result = reservationService.cancelReservation(command);

      // then
      assertThat(result.reservationUuid()).isEqualTo(confirmedReservation.getReservationUuid());
      assertThat(result.status()).isEqualTo(ReservationStatus.CANCELLED.toString());
    }

    @DisplayName("STAFF 는 본인의 가게의 예약을 취소할 수 있다.")
    @Test
    void success_staff() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(confirmedReservation.getRestaurantDetails().getStaffId())
          .userRole(UserRole.STAFF)
          .reason("강혜주의 바쁜 그녀")
          .build();

      // when
      CancelReservationInfo result = reservationService.cancelReservation(command);

      // then
      assertThat(result.reservationUuid()).isEqualTo(confirmedReservation.getReservationUuid());
      assertThat(result.status()).isEqualTo(ReservationStatus.CANCELLED.toString());
    }

    @DisplayName("CUSTOMER 는 본인의 예약을 취소 할 수 있다.")
    @Test
    void success_customer() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(confirmedReservation.getReserverId())
          .userRole(UserRole.CUSTOMER)
          .reason("박지은의 잠옴")
          .build();

      // when
      CancelReservationInfo result = reservationService.cancelReservation(command);

      // then
      assertThat(result.reservationUuid()).isEqualTo(confirmedReservation.getReservationUuid());
      assertThat(result.status()).isEqualTo(ReservationStatus.CANCELLED.toString());
    }

  }

  @DisplayName("예약 취소 서비스: 실패")
  @Nested
  class cancel_fail {

    Reservation confirmedReservation;
    Reservation canceledReservation;
    Reservation deletedReservation;

    @BeforeEach
    void setUp() {
      confirmedReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));
      ReflectionTestUtils.setField(confirmedReservation, "status", ReservationStatus.CONFIRMED);

      canceledReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));

      ReflectionTestUtils.setField(canceledReservation, "status", ReservationStatus.CANCELLED);

      deletedReservation = ReservationFixture.createRandomByPaymentDetails(List.of(
          ReservationPaymentDetailFixture.createRandomByType(
              ReservationPaymentDetail.PaymentType.PAYMENT)
      ));

      ReflectionTestUtils.setField(deletedReservation, "status", ReservationStatus.CANCELLED);
      ReflectionTestUtils.setField(deletedReservation, "deletedAt",
          LocalDateTime.now().minusDays(1));
      ReflectionTestUtils.setField(deletedReservation, "deletedBy", 1L);
      reservationRepository.saveAll(List.of(confirmedReservation, canceledReservation, deletedReservation));
    }

    @DisplayName("없는 예약을 취소하려고 하면 찾을 수 없어 예외가 발생한다.")
    @Test
    void fail_notFound() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid("not-found-uuid")
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(1L)
          .userRole(UserRole.MASTER)
          .reason("한지훈의 단순변심")
          .build();

      // when & then
      assertThatThrownBy(() -> reservationService.cancelReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("이미 삭제된 예약을 취소하려고 하면 찾을 수 없어 예외가 발생한다.")
    @Test
    void fail_notFound_alreadyDeleted() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(deletedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(1L)
          .userRole(UserRole.MASTER)
          .reason("한지훈의 단순변심")
          .build();

      // when & then
      assertThatThrownBy(() -> reservationService.cancelReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NOT_FOUND.getMessage());
    }

    @DisplayName("이미 취소된 예약을 취소하려고 하면 예외가 발생한다.")
    @Test
    void fail_alreadyCanceled() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(canceledReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(1L)
          .userRole(UserRole.MASTER)
          .reason("한지훈의 단순변심")
          .build();

      // when & then
      assertThatThrownBy(() -> reservationService.cancelReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.ALREADY_CANCELED.getMessage());
    }

    @DisplayName("취소 마감 시간이 지나서 취소를 하려고 하면 예외가 발생한다.")
    @Test
    void fail_cancellationDeadlinePassed() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusHours(2)) // 3시간 조건 위배
          .requesterId(confirmedReservation.getRestaurantDetails().getOwnerId())
          .userRole(UserRole.OWNER)
          .reason("황하온의 변덕")
          .build();

      // when & then
      assertThatThrownBy(() -> reservationService.cancelReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.CANCELLATION_DEADLINE_PASSED.getMessage());
    }

    @DisplayName("OWNER 가 본인의 가게가 아닌 예약을 취소하려고 하면 예외가 발생한다.")
    @Test
    void fail_owner_permission() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(confirmedReservation.getRestaurantDetails().getOwnerId() - 1) // 잘못된 ID
          .userRole(UserRole.OWNER)
          .reason("황하온의 변덕")
          .build();

      // when & then
      assertThatThrownBy(() -> reservationService.cancelReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NO_CANCEL_PERMISSION.getMessage());
    }

    @DisplayName("STAFF 가 본인의 가게가 아닌 예약을 취소하려고 하면 예외가 발생한다.")
    @Test
    void fail_staff() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(confirmedReservation.getRestaurantDetails().getStaffId() - 1) // 잘못된 ID
          .userRole(UserRole.STAFF)
          .reason("강혜주의 바쁜 그녀")
          .build();

      // when & then
      assertThatThrownBy(() -> reservationService.cancelReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NO_CANCEL_PERMISSION.getMessage());
    }

    @DisplayName("CUSTOMER 는 본인의 예약이 아닌 예약을 취소하려고 하면 예외가 발생한다.")
    @Test
    void fail_customer() {
      // given
      CancelReservationCommand command = CancelReservationCommand.builder()
          .reservationUuid(confirmedReservation.getReservationUuid())
          .cancelRequestDateTime(
              confirmedReservation.getRestaurantTimeSlotDetails().reservationDateTime().minusDays(1))
          .requesterId(confirmedReservation.getReserverId() - 1) // 잘못된 ID
          .userRole(UserRole.CUSTOMER)
          .reason("박지은의 잠옴")
          .build();

      // when & then
      assertThatThrownBy(() -> reservationService.cancelReservation(command))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining(ReservationErrorCode.NO_CANCEL_PERMISSION.getMessage());
    }

  }
}