/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import table.eat.now.common.domain.BaseEntity;
import table.eat.now.reservation.reservation.domain.entity.json.RestaurantDetails;
import table.eat.now.reservation.reservation.domain.entity.json.RestaurantMenuDetails;
import table.eat.now.reservation.reservation.domain.entity.json.RestaurantTimeSlotDetails;
import table.eat.now.reservation.reservation.domain.entity.vo.ReservationGuestInfo;
import table.eat.now.reservation.reservation.domain.entity.vo.ReservationPaymentDetails;

@Entity
@Table(name = "p_reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "reservation_uuid", nullable = false, unique = true, length = 100)
  private String reservationUuid;

  @Column(name = "reserver_id", nullable = false)
  private Long reserverId;

  @Column(name = "restaurant_timeslot_uuid", nullable = false, length = 100)
  private String restaurantTimeSlotUuid;

  @Column(name = "restaurant_uuid", nullable = false, length = 100)
  private String restaurantId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "restaurant_timeslot_details", columnDefinition = "jsonb", nullable = false)
  private RestaurantTimeSlotDetails restaurantTimeSlotDetails;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "restaurant_details", columnDefinition = "jsonb", nullable = false)
  private RestaurantDetails restaurantDetails;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "restaurant_menu_details", columnDefinition = "jsonb", nullable = false)
  private RestaurantMenuDetails restaurantMenuDetails;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "reserverName", column = @Column(name = "reserver_name", nullable = false, length = 100)),
      @AttributeOverride(name = "reserverContact", column = @Column(name = "reserver_contact", nullable = false, length = 100)),
      @AttributeOverride(name = "guestCount", column = @Column(name = "guest_count", nullable = false))
  })
  private ReservationGuestInfo guestInfo;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ReservationStatus status;

  @Column(name = "special_request", columnDefinition = "TEXT")
  private String specialRequest;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Embedded
  private ReservationPaymentDetails paymentDetails;

  @Builder
  private Reservation(
      UUID reservationUuid,
      Long reserverId,
      UUID restaurantTimeSlotUuid,
      RestaurantTimeSlotDetails restaurantTimeSlotDetails,
      UUID restaurantId,
      RestaurantDetails restaurantDetails,
      RestaurantMenuDetails restaurantMenuDetails,
      String reserverName,
      String reserverContact,
      Integer guestCount,
      ReservationStatus status,
      String specialRequest,
      List<ReservationPaymentDetail> details
  ) {
    this.reservationUuid = reservationUuid.toString();
    this.reserverId = reserverId;
    this.restaurantTimeSlotUuid = restaurantTimeSlotUuid.toString();
    this.restaurantTimeSlotDetails = restaurantTimeSlotDetails;
    this.restaurantId = restaurantId.toString();
    this.restaurantDetails = restaurantDetails;
    this.restaurantMenuDetails = restaurantMenuDetails;
    this.guestInfo = ReservationGuestInfo.of(reserverName, reserverContact, guestCount);
    this.status = status;
    this.specialRequest = specialRequest;
    this.paymentDetails = ReservationPaymentDetails.of(details, this);
    this.totalAmount = paymentDetails.getTotalAmount();
  }

  @Getter
  @RequiredArgsConstructor
  public enum ReservationStatus {
    PENDING_PAYMENT("결제 대기"),
    CONFIRMED("확정"),
    CANCELLED("취소됨");

    private final String name;
  }
}