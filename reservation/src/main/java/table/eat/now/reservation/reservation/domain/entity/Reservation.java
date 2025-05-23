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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import table.eat.now.common.domain.BaseEntity;
import table.eat.now.common.resolver.dto.UserRole;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "reservation_uuid", nullable = false, unique = true, length = 100)
  private String reservationUuid;

  @Column(name = "reservation_name", nullable = false, length = 100)
  private String name;

  @Column(name = "reserver_id", nullable = false)
  private Long reserverId;

  @Column(name = "restaurant_timeslot_uuid", nullable = false, length = 100)
  private String restaurantTimeSlotUuid;

  @Column(name = "restaurant_uuid", nullable = false, length = 100)
  private String restaurantUuid;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "restaurant_timeslot_details", columnDefinition = "json", nullable = false)
  private RestaurantTimeSlotDetails restaurantTimeSlotDetails;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "restaurant_details", columnDefinition = "json", nullable = false)
  private RestaurantDetails restaurantDetails;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "restaurant_menu_details", columnDefinition = "json", nullable = false)
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

  @Column(name = "cancel_reason", nullable = true, length = 200)
  private String cancelReason;

  @Embedded
  private ReservationPaymentDetails paymentDetails;

  @Builder
  private Reservation(
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
    this.reservationUuid = reservationUuid;
    this.reserverId = reserverId;
    this.name = name;
    this.restaurantTimeSlotUuid = restaurantTimeSlotUuid;
    this.restaurantTimeSlotDetails =
        RestaurantTimeSlotDetails.of(reservationDate, reservationTimeslot);
    this.restaurantUuid = restaurantUuid;
    this.restaurantDetails =
        RestaurantDetails.of(
            restaurantName,
            restaurantAddress,
            ownerId,
            staffId,
            restaurantContactNumber,
            restaurantOpeningTime,
            restaurantClosingTime
        );
    this.restaurantMenuDetails = RestaurantMenuDetails.of(menuName, menuPrice, menuQuantity);
    this.guestInfo = ReservationGuestInfo.of(reserverName, reserverContact, guestCount);
    this.status = status;
    this.specialRequest = specialRequest;
    this.paymentDetails = ReservationPaymentDetails.of(details, this);
    this.totalAmount = paymentDetails.getTotalAmount();
  }

  public boolean isAccessibleBy(Long userId, UserRole role) {
    if(role.isMaster()) return true;

    if(role.isOwner()
        && restaurantDetails.getOwnerId().equals(userId)
        && getDeletedAt() == null) return true;

    if(role.isStaff()
        && restaurantDetails.getStaffId().equals(userId)
        && getDeletedAt() == null) return true;

    if(role.isCustomer()
        && reserverId.equals(userId)
        && getDeletedAt() == null) return true;

    return false;
  }

  public boolean isValidStatusForConfirmation() {
    return this.status == ReservationStatus.PENDING_PAYMENT;
  }

  public boolean isEditableBy(Long userId, UserRole role) {
    return isAccessibleBy(userId, role);
  }

  public boolean isCanceled() {
    return this.status == ReservationStatus.CANCELLED;
  }

  public void cancelWithReason(String reason) {
    this.status = ReservationStatus.CANCELLED;
    this.cancelReason = reason;
  }

  public void confirm() {
    this.status = ReservationStatus.CONFIRMED;
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