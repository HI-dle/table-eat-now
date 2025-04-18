/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ReservationGuestInfo {

  private String reserverName;
  private String reserverContact;
  private Integer guestCount;

  public static ReservationGuestInfo of(String reserverName, String reserverContact, Integer guestCount) {
    return new ReservationGuestInfo(reserverName, reserverContact, guestCount);
  }
}
