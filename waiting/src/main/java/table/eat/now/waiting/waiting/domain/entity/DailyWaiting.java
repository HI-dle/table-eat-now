package table.eat.now.waiting.waiting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DailyWaiting extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, length = 100, unique = true)
  private String dailyWaitingUuid;

  @Column(nullable = false, length = 100)
  private String restaurantUuid;

  @Column(nullable = false, length = 200)
  private String restaurantName;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private WaitingStatus status;

  @Column(nullable = false)
  private LocalDate waitingDate;

  @Column(nullable = false)
  private long avgWaitingSec;

  private Long totalSequence;

  private DailyWaiting(String restaurantUuid, String restaurantName, WaitingStatus status,
      LocalDate waitingDate, long avgWaitingSec) {
    this.dailyWaitingUuid = UUID.randomUUID().toString();
    this.restaurantUuid = restaurantUuid;
    this.restaurantName = restaurantName;
    this.status = status;
    this.waitingDate = waitingDate;
    this.avgWaitingSec = avgWaitingSec;
  }

  public static DailyWaiting of(String restaurantUuid, String restaurantName, String status,
      LocalDate waitingDate, long avgWaitingSec) {
    return new DailyWaiting(restaurantUuid, restaurantName, WaitingStatus.valueOf(status), waitingDate, avgWaitingSec);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public enum WaitingStatus {
    AVAILABLE,
    UNAVAILABLE,
  }
}
