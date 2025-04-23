/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.global.fixture;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import table.eat.now.reservation.global.util.LongIdGenerator;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.Reservation.ReservationStatus;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;

public class ReservationFixture {
  public static Reservation createRandomByPaymentDetails(
      List<ReservationPaymentDetail> paymentDetails) {
    Long num = LongIdGenerator.makeLong();
    Long reserverId = num;
    String reservationUuid = UUID.randomUUID().toString();
    String name = "Test Reservation"+num;
    String restaurantTimeSlotUuid = UUID.randomUUID().toString();
    LocalDate reservationDate = LocalDate.now();
    LocalTime reservationTimeslot = LocalTime.of(18, 30);
    String restaurantId = UUID.randomUUID().toString();
    String restaurantAddress = "123 Main Street, Seoul" + num;
    LocalTime restaurantClosingTime = LocalTime.of(22, 0);
    Long ownerId = LongIdGenerator.makeLong();
    Long staffId = LongIdGenerator.makeLong();
    String restaurantContactNumber = "010-1234-5678";
    String restaurantName = "테스트 레스토랑"+num;
    LocalTime restaurantOpeningTime = LocalTime.of(11, 0);
    String menuName = "테스트 메뉴" + num;
    BigDecimal menuPrice = BigDecimal.valueOf(15000);
    Integer menuQuantity = 2;
    String reserverName = "홍길동" + num;
    String reserverContact = "010-9876-5432";
    Integer guestCount = 3;
    ReservationStatus status = ReservationStatus.PENDING_PAYMENT;
    String specialRequest = "창가 자리 부탁드립니다."+num;

    return create(
        reserverId,
        reservationUuid,
        name,
        restaurantTimeSlotUuid,
        reservationDate,
        reservationTimeslot,
        restaurantId,
        restaurantAddress,
        restaurantClosingTime,
        ownerId,
        staffId,
        restaurantContactNumber,
        restaurantName,
        restaurantOpeningTime,
        menuName,
        menuPrice,
        menuQuantity,
        reserverName,
        reserverContact,
        guestCount,
        status,
        specialRequest,
        paymentDetails
    );
  }
  public static Reservation create(
      Long reserverId,
      String reservationUuid,
      String name,
      String restaurantTimeSlotUuid,
      LocalDate reservationDate,
      LocalTime reservationTimeslot,
      String restaurantUuid,
      String restaurantAddress,
      LocalTime restaurantClosingTime,
      Long ownerId,
      Long staffId,
      String restaurantContactNumber,
      String restaurantName,
      LocalTime restaurantOpeningTime,
      String menuName,
      BigDecimal menuPrice,
      Integer menuQuantity,
      String reserverName,
      String reserverContact,
      Integer guestCount,
      ReservationStatus status,
      String specialRequest,
      List<ReservationPaymentDetail> details
  ) {

    Reservation reservation = Reservation.builder()
        .reserverId(reserverId)
        .reservationUuid(reservationUuid)
        .name(name)
        .restaurantTimeSlotUuid(restaurantTimeSlotUuid)
        .reservationDate(reservationDate)
        .reservationTimeslot(reservationTimeslot)
        .restaurantUuid(restaurantUuid)
        .restaurantName(restaurantName)
        .restaurantAddress(restaurantAddress)
        .restaurantContactNumber(restaurantContactNumber)
        .restaurantOpeningTime(restaurantOpeningTime)
        .restaurantClosingTime(restaurantClosingTime)
        .ownerId(ownerId)
        .staffId(staffId)
        .menuName(menuName)
        .menuPrice(menuPrice)
        .menuQuantity(menuQuantity)
        .reserverName(reserverName)
        .reserverContact(reserverContact)
        .guestCount(guestCount)
        .status(status)
        .specialRequest(specialRequest)
        .details(details)
        .build();

    return reservation;
  }
}
