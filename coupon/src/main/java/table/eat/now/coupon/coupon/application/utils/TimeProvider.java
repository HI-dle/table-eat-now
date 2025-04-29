package table.eat.now.coupon.coupon.application.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeProvider {
  private TimeProvider() {
    throw new IllegalStateException("Utility class");
  }

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  public static String getToday() {
    return LocalDate.now().format(DATE_FORMATTER);
  }

  public static String getDate(LocalDate date) {
    return date.format(DATE_FORMATTER);
  }

  public static Duration getDuration(LocalDateTime to, int additionalMins) {
    LocalDateTime from = LocalDateTime.now();
    return Duration.between(from, to)
        .plusMinutes(additionalMins);
  }

  public static Long getEpochMillis(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}
