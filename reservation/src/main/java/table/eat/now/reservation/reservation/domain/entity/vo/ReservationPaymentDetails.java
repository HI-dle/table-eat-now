/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.vo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationPaymentDetails {

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReservationPaymentDetail> values = new ArrayList<>();

  private ReservationPaymentDetails(List<ReservationPaymentDetail> details, Reservation reservation) {
    details.forEach(detail -> detail.modifyReservation(reservation));
    this.values.addAll(details);
  }

  public List<ReservationPaymentDetail> getValues() {
    return values.stream().toList();
  }

  public static ReservationPaymentDetails of(List<ReservationPaymentDetail> details,
      Reservation reservation) {
    return new ReservationPaymentDetails(details, reservation);
  }

  public void add(ReservationPaymentDetail detail, Reservation reservation) {
    detail.modifyReservation(reservation);
    this.values.add(detail);
  }

  public BigDecimal getTotalAmount() {
    return values.stream()
        .map(ReservationPaymentDetail::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
