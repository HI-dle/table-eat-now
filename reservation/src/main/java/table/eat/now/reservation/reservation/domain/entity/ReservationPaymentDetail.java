/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Entity
@Table(name = "p_reservation_payment_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationPaymentDetail extends BaseEntity {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  @Column(name = "reservation_payment_detail_uuid", nullable = false, unique = true, length = 100)
  private String reservationPaymentDetailUuid;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private PaymentType type;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "detail_reference_id", length = 100, nullable = false)
  private String detailReferenceId;

  @Builder
  private ReservationPaymentDetail(
      BigDecimal amount,
      UUID detailReferenceId,
      Reservation reservation,
      UUID reservationPaymentDetailUuid,
      PaymentType type) {
    this.amount = amount;
    this.detailReferenceId = detailReferenceId.toString();
    this.reservation = reservation;
    this.reservationPaymentDetailUuid = reservationPaymentDetailUuid.toString();
    this.type = type;
  }

  public void modifyReservation(Reservation reservation) {
    this.reservation = reservation;
  }

  @Getter
  @RequiredArgsConstructor
  public enum PaymentType {
    PAYMENT("결제"),
    PROMOTION_COUPON("쿠폰"),
    PROMOTION_EVENT("이벤트"),
    ;

    private final String name;
  }
}