package table.eat.now.notification.domain.entity.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 14.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageParam {

  private String customerName;
  private String reservationTime;
  private String restaurantName;

  public static MessageParam of(
      String customerName, String reservationTime, String restaurantName) {
    return new MessageParam(customerName,reservationTime,restaurantName);
  }

}
