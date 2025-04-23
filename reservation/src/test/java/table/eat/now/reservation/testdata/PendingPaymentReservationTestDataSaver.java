/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.testdata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.Reservation.ReservationStatus;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;
import table.eat.now.reservation.reservation.domain.repository.ReservationRepository;

@DisplayName("편의로 만든 클래스 입니다. Disabled 주석 처리하고 돌려서 데이터 만들기!")
@Disabled
public class PendingPaymentReservationTestDataSaver extends IntegrationTestDataSupport{

  @Autowired
  private ReservationRepository reservationRepository;

  @DisplayName("결제 전 예약")
  @Nested
  class create_paymentBeforeReservation {

    @DisplayName("정률 할인 쿠폰 1개, 예약")
    @Test
    void percentCoupon1AndPayment1() {
      // given
      List<Reservation> list = new ArrayList<>();
      for(int i=0 ; i<50 ; i++){
        Reservation reservation = Reservation.builder()
            .reserverId(1L)
            .reservationUuid("00000000-0000-0000-0000-"+ String.format("%012d",i))
            .name("맛있는 식당(비빔밥 1건)")
            .restaurantTimeSlotUuid("timeslot-0000-0000-0000-"+ String.format("%012d",i))
            .reservationDate(LocalDate.now().plusDays(30))
            .reservationTimeslot(LocalTime.of(12, 0))
            .restaurantUuid("restaura-0000-0000-0000-"+ String.format("%012d",i))
            .restaurantName("맛있는 식당")
            .restaurantAddress("서울시 강남구")
            .restaurantContactNumber("02-000-0000")
            .restaurantOpeningTime(LocalTime.of(9, 0))
            .restaurantClosingTime(LocalTime.of(21, 0))
            .menuName("비빔밥")
            .menuQuantity(1)
            .menuPrice(BigDecimal.valueOf(10000))
            .reserverName("홍길동")
            .reserverContact("010-0000-0000")
            .guestCount(2)
            .status(ReservationStatus.PENDING_PAYMENT)
            .specialRequest("창가 자리")
            .details(List.of(
                ReservationPaymentDetail.builder()
                    .type(ReservationPaymentDetail.PaymentType.PROMOTION_COUPON)
                    .detailReferenceId("coupon00-0000-0000-0000-"+ String.format("%012d",i))
                    .amount(BigDecimal.valueOf(3000))
                    .build(),
                ReservationPaymentDetail.builder()
                    .type(ReservationPaymentDetail.PaymentType.PAYMENT)
                    .detailReferenceId("payment0-0000-0000-0000-"+ String.format("%012d",i))
                    .amount(BigDecimal.valueOf(7000))
                    .build()
            ))
            .build();
        list.add(reservation);
      }

      reservationRepository.saveAll(list);
    }
  }
}
