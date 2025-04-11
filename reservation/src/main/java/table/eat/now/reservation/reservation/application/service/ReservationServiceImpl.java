/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.CouponClient;
import table.eat.now.reservation.reservation.application.client.PaymentClient;
import table.eat.now.reservation.reservation.application.client.PromotionClient;
import table.eat.now.reservation.reservation.application.client.RestaurantClient;
import table.eat.now.reservation.reservation.application.client.dto.request.CreatePaymentCommand;
import table.eat.now.reservation.reservation.application.client.dto.request.GetPromotionsCriteria;
import table.eat.now.reservation.reservation.application.client.dto.response.CreatePaymentInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.discount.DiscountStrategy;
import table.eat.now.reservation.reservation.application.service.discount.DiscountStrategyFactory;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.infrastructure.persistence.JpaReservationRepository;
import table.eat.now.reservation.reservation.presentation.dto.response.GetRestaurantInfo;
import table.eat.now.reservation.reservation.presentation.dto.response.GetRestaurantInfo.Timeslot;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final JpaReservationRepository reservationRepository;
  private final CouponClient couponClient;
  private final PaymentClient paymentClient;
  private final PromotionClient promotionClient;
  private final RestaurantClient restaurantClient;

  @Override
  @Transactional
  public CreateReservationInfo createRestaurant(CreateReservationCommand command) {
    // 1. 총 결제 금액 검증
    validateTotalPrice(command);

    // 2. 식당 정보 조회 및 검증 (시간/날짜/인원/메뉴 등)
    GetRestaurantInfo restaurant = restaurantClient.getRestaurant(command.restaurantUuid());
    validateRestaurantAvailability(restaurant, command);

    // 3. 할인 정책 검증
    Set<String> couponUuids = extractCouponUuids(command.payments());
    Set<String> promotionIds = extractPromotionIds(command.payments());
    validAllDiscounts(
        couponUuids,
        promotionIds,
        command.restaurantUuid(),
        command.totalPrice(),
        command.payments(),
        command.reservationDate()
    );

    // 4. 쿠폰 선점 처리
    for (String userCouponUuid : couponUuids) {
      couponClient.preemptCoupon(userCouponUuid, command.restaurantUuid());
    }

    // 6. 식당 현재 예약 인원 수정
    restaurantClient.modifyRestaurantCurTotalGuestCount(
        command.guestCount(), command.restaurantTimeslotUuid(), command.restaurantUuid());

    // 예약 uuid 생성
    String reservationUuid = UUID.randomUUID().toString();

    // 7. 결제 생성
    String reservationName = command.getReservationName();
    CreatePaymentCommand paymentCommand =
        CreatePaymentCommand.from(
            reservationUuid,
            restaurant.restaurantUuid(),
            command.reserverId(),
            reservationName,
            extractPaymentWithValid(command.payments()).amount()
        );
    CreatePaymentInfo paymentInfo = paymentClient.createPayment(paymentCommand);

    // 8. 예약 저장
    Reservation reservation = command
        .toEntityWithUuidAndPaymentKey(
            reservationUuid, reservationName, paymentInfo.idempotencyKey());
    reservationRepository.save(reservation);

    return CreateReservationInfo.of(reservation.getReservationUuid(), paymentInfo.idempotencyKey());
  }

  private void validateTotalPrice(CreateReservationCommand command) {
    BigDecimal menuTotalPrice = command.restaurantMenuDetails().price()
        .multiply(BigDecimal.valueOf(command.restaurantMenuDetails().quantity()));
    BigDecimal expectedTotal = calculateExpectedTotalAmount(command.payments());
    BigDecimal providedTotal = command.totalPrice();

    boolean isInvalidAmount =
        !expectedTotal.equals(menuTotalPrice) || !expectedTotal.equals(providedTotal);

    if (isInvalidAmount) {
      throw CustomException.from(ReservationErrorCode.INVALID_TOTAL_AMOUNT);
    }
  }

  private BigDecimal calculateExpectedTotalAmount(
      List<CreateReservationCommand.PaymentDetail> payments) {
    return payments.stream()
        .map(CreateReservationCommand.PaymentDetail::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void validateRestaurantAvailability(
      GetRestaurantInfo restaurant, CreateReservationCommand command) {

    Map<String, Timeslot> timeslotMap = restaurant.timeslots().stream()
        .collect(Collectors.toMap(Timeslot::restaurantTimeslotUuid, timeslot -> timeslot));

    Timeslot timeslot = timeslotMap.get(command.restaurantTimeslotUuid());

    if (timeslot == null) {
      throw CustomException.from(ReservationErrorCode.INVALID_TIMESLOT);
    }

    // 날짜 확인
    boolean isDateMatch = timeslot.availableDate()
        .equals(command.restaurantTimeSlotDetails().availableDate());
    if (!isDateMatch) {
      throw CustomException.from(ReservationErrorCode.INVALID_RESERVATION_DATE);
    }

    // 시간 확인
    boolean isTimeMatch = timeslot.timeslot()
        .equals(command.restaurantTimeSlotDetails().timeslot());
    if (!isTimeMatch) {
      throw CustomException.from(ReservationErrorCode.INVALID_RESERVATION_TIME);
    }

    // 인원 수 확인
    boolean exceedsCapacity =
        command.guestCount() + timeslot.curTotalGuestCount() > timeslot.maxCapacity();
    if (exceedsCapacity) {
      throw CustomException.from(ReservationErrorCode.EXCEEDS_MAX_GUEST_CAPACITY);
    }

    // 메뉴 확인
    boolean validMenu = restaurant.menus().stream().anyMatch(menu ->
        menu.restaurantMenuUuid().equals(command.restaurantMenuUuid()) &&
            menu.name().equals(command.restaurantMenuDetails().name()) &&
            menu.price().equals(command.restaurantMenuDetails().price())
    );
    if (!validMenu) {
      throw CustomException.from(ReservationErrorCode.INVALID_MENU_SELECTION);
    }
  }

  private void validAllDiscounts(
      Set<String> userCouponUuids,
      Set<String> promotionIds,
      String restaurantUuid,
      BigDecimal totalPrice,
      List<PaymentDetail> payments,
      LocalDateTime reservationDate) {
    // 사용 쿠폰 수 제한
    if (userCouponUuids.size() > 2) {
      throw CustomException.from(ReservationErrorCode.COUPON_USAGE_LIMIT_EXCEEDED);
    }
    // 사용 프로모션 수 제한
    if (promotionIds.size() > 1) {
      throw CustomException.from(ReservationErrorCode.PROMOTION_USAGE_LIMIT_EXCEEDED);
    }

    // 3-1. 쿠폰 정보 조회
    Map<String, Coupon> couponMap = null;
    if (!userCouponUuids.isEmpty()) {
      couponMap = couponClient.getCoupons(userCouponUuids).couponMap();
    }

    // 3-2. 프로모션 정보 조회
    Map<String, Promotion> promotionsMap = null;
    if (!promotionIds.isEmpty()) {
      promotionsMap = promotionClient.getPromotions(
          GetPromotionsCriteria.of(promotionIds, restaurantUuid)).promotions();
    }

    // 검증
    validateAllDiscounts(couponMap, promotionsMap, totalPrice, payments, reservationDate);
  }

  private void validateAllDiscounts(
      Map<String, GetCouponsInfo.Coupon> couponMap,
      Map<String, GetPromotionsInfo.Promotion> promotions,
      BigDecimal totalPrice,
      List<PaymentDetail> payments,
      LocalDateTime reservationDate
  ) {
    DiscountStrategyFactory factory = new DiscountStrategyFactory(couponMap, promotions);

    for (PaymentDetail paymentDetail : payments) {
      DiscountStrategy strategy = factory.getStrategy(paymentDetail);
      strategy.validate(totalPrice, paymentDetail, reservationDate);
    }
  }

  private Set<String> extractCouponUuids(List<CreateReservationCommand.PaymentDetail> payments) {
    return payments.stream()
        .filter(
            p -> p.type() == CreateReservationCommand.PaymentDetail.PaymentType.PROMOTION_COUPON)
        .map(CreateReservationCommand.PaymentDetail::detailReferenceId)
        .collect(Collectors.toSet());
  }

  private Set<String> extractPromotionIds(List<CreateReservationCommand.PaymentDetail> payments) {
    return payments.stream()
        .filter(p -> p.type() == CreateReservationCommand.PaymentDetail.PaymentType.PROMOTION_EVENT)
        .map(CreateReservationCommand.PaymentDetail::detailReferenceId)
        .collect(Collectors.toSet());
  }

  private PaymentDetail extractPaymentWithValid(List<PaymentDetail> payments) {
    List<PaymentDetail> filtered = payments.stream()
        .filter(p -> p.type() == PaymentType.PAYMENT)
        .toList();

    if (filtered.size() != 1) {
      throw CustomException.from(ReservationErrorCode.PAYMENT_LIMIT_EXCEEDED);
    }

    return filtered.get(0);
  }
}
