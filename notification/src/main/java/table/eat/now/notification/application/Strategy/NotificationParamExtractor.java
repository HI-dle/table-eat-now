package table.eat.now.notification.application.Strategy;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.stereotype.Component;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.vo.MessageParam;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Component
public class NotificationParamExtractor {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public Map<String, String> extract(Notification notification) {
    MessageParam param = notification.getMessageParam();

    return Map.of(
        "customerName", param.getCustomerName(),
        "reservationTime", formatReservationTime(param),
        "restaurantName", param.getRestaurantName()
    );
  }

  private String formatReservationTime(MessageParam param) {
    return param.getReservationTime() == null ? "" : param.getReservationTime().format(FORMATTER);
  }
}

