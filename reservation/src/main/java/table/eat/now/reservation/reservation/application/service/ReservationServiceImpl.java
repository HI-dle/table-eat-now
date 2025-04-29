/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.CouponClient;
import table.eat.now.reservation.reservation.application.client.PaymentClient;
import table.eat.now.reservation.reservation.application.client.PromotionClient;
import table.eat.now.reservation.reservation.application.client.RestaurantClient;
import table.eat.now.reservation.reservation.application.client.dto.request.CreatePaymentCommand;
import table.eat.now.reservation.reservation.application.client.dto.request.GetPromotionsCriteria;
import table.eat.now.reservation.reservation.application.client.dto.request.PreemptCouponCommand;
import table.eat.now.reservation.reservation.application.client.dto.response.CreatePaymentInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo.UserCoupon;
import table.eat.now.reservation.reservation.application.event.event.CancelReservationAfterCommitEvent;
import table.eat.now.reservation.reservation.application.event.event.ConfirmReservationAfterCommitEvent;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CancelReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.ConfirmReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.application.service.dto.response.CancelReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.GetReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.reservation.reservation.application.service.validation.context.CancelReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.context.ConfirmReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.context.CreateReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.policy.CancelReservationValidationPolicy;
import table.eat.now.reservation.reservation.application.service.validation.policy.ConfirmReservationValidationPolicy;
import table.eat.now.reservation.reservation.application.service.validation.policy.CreateReservationValidationPolicy;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.repository.ReservationRepository;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final CouponClient couponClient;
  private final PaymentClient paymentClient;
  private final PromotionClient promotionClient;
  private final RestaurantClient restaurantClient;
  private final ApplicationEventPublisher eventPublisher;
  // todo: 이것도 리펙토링..
  private final CreateReservationValidationPolicy createReservationValidationPolicy;
  private final CancelReservationValidationPolicy cancelReservationValidationPolicy;
  private final ConfirmReservationValidationPolicy confirmReservationValidationPolicy;

  @Override
  @Transactional
  public CreateReservationInfo createReservation(CreateReservationCommand command) {

    // 식당 정보 조회
    GetRestaurantInfo restaurant = restaurantClient.getRestaurant(command.restaurantUuid());

    // 쿠폰 정보 조회
    Set<String> userCouponUuids = extractUserCouponUuids(command.payments());
    Map<String, UserCoupon> userCouponMap = Collections.emptyMap();
    if (!userCouponUuids.isEmpty()) {
      userCouponMap = Optional.ofNullable(couponClient.getUserCoupons(userCouponUuids))
          .map(GetUserCouponsInfo::userCouponMap)
          .orElse(Collections.emptyMap());
    }

    // 프로모션 정보 조회
    Set<String> promotionIds = extractPromotionIds(command.payments());
    Map<String, Promotion> promotionsMap = Collections.emptyMap();
    if (!promotionIds.isEmpty()) {
      promotionsMap = Optional.ofNullable(promotionClient.getPromotions(
          GetPromotionsCriteria.of(promotionIds, command.restaurantUuid())))
          .map(GetPromotionsInfo::promotions).orElse(Collections.emptyMap());
    }

    createReservationValidationPolicy.validate(
      CreateReservationValidationContext.from(command, restaurant, userCouponMap, promotionsMap)
    );

    // 예약 uuid 생성
    String reservationUuid = UUID.randomUUID().toString();

    // 쿠폰 선점 처리
    if (!userCouponUuids.isEmpty()) {
      couponClient.preemptCoupon(
          PreemptCouponCommand.from(reservationUuid, userCouponUuids.stream().toList()));
    }

    // 식당 현재 예약 인원 수정
    restaurantClient.modifyRestaurantCurTotalGuestCount(
        command.guestCount(), command.restaurantTimeslotUuid(), command.restaurantUuid());

    // 결제 생성
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

    // 예약 저장
    Reservation reservation = command
        .toEntityWithUuidAndPaymentKey(
            reservationUuid, reservationName, restaurant.ownerId(), restaurant.staffId(),  paymentInfo.idempotencyKey());
    reservationRepository.save(reservation);

    return CreateReservationInfo.of(
        reservation.getReservationUuid(), paymentInfo.idempotencyKey());
  }

  @Override
  @Transactional(readOnly = true)
  public GetReservationInfo getReservation(GetReservationCriteria criteria) {
    Reservation reservation = getReservationOrElseThrow(criteria.reservationUuid());
    if(!reservation.isAccessibleBy(criteria.userId(), criteria.role())){
      throw CustomException.from(ReservationErrorCode.NOT_FOUND);
    }
    return GetReservationInfo.from(reservation);
  }

  @Override
  @Transactional
  public void confirmReservation(ConfirmReservationCommand command) {
    Reservation reservation =
        getReservationByPaymentIdempotencyKeyOrElseThrow(command.idempotencyKey());
    confirmReservationValidationPolicy.validate(ConfirmReservationValidationContext.from(reservation));
    reservation.confirm();
    eventPublisher.publishEvent(ConfirmReservationAfterCommitEvent.from(reservation));
  }

  private Reservation getReservationByPaymentIdempotencyKeyOrElseThrow(String idempotencyKey) {
    return reservationRepository.findWithDetailsByPaymentIdempotency(idempotencyKey)
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.NOT_FOUND));
  }

  @Override
  @Transactional
  public CancelReservationInfo cancelReservation(CancelReservationCommand command) {
    Reservation reservation = getByNoDeletedReservationUuidOrElseThrow(command.reservationUuid());

    cancelReservationValidationPolicy.validate(
        CancelReservationValidationContext.from(
            reservation,
            command.cancelRequestDateTime(),
            command.requesterId(),
            command.userRole()
        )
    );

    // 상태를 CANCELED로 변경
    reservation.cancelWithReason(command.reason());

    eventPublisher.publishEvent(CancelReservationAfterCommitEvent.from(reservation));

    return CancelReservationInfo.from(reservation);
  }

  private Reservation getByNoDeletedReservationUuidOrElseThrow(String reservationUuid) {
    Reservation reservation = reservationRepository.findByReservationUuid(reservationUuid)
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.NOT_FOUND));

    if(reservation.isDeleted()) throw CustomException.from(ReservationErrorCode.NOT_FOUND);

    return reservation;
  }

  private Reservation getReservationOrElseThrow(String reservationUuid) {
    return reservationRepository.findWithDetailsByReservationUuid(reservationUuid)
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.NOT_FOUND));
  }

  private Set<String> extractUserCouponUuids(List<CreateReservationCommand.PaymentDetail> payments) {
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

    return filtered.get(0);
  }
}
