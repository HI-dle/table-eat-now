package table.eat.now.waiting.waiting_request.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WaitingRequest extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name="waiting_request_uuid", nullable = false, length = 100, unique = true)
  private String waitingRequestUuid;

  @Column(nullable = false, length = 100)
  private String dailyWaitingUuid;

  @Column
  private Long userId;

  @Column(nullable = false)
  private Integer sequence;

  @Column(nullable = false, length = 15)
  private String phone;

  @Column(nullable = false, length = 200)
  private String slackId;

  @Column(nullable = false)
  private Integer seatSize;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  private WaitingStatus status;

  @OneToMany(mappedBy = "waitingRequest", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WaitingRequestHistory> histories;

  @Builder
  private WaitingRequest(String waitingRequestUuid, String dailyWaitingUuid, Long userId,
      Integer sequence, String phone, String slackId, Integer seatSize) {

    this.waitingRequestUuid = waitingRequestUuid;
    this.dailyWaitingUuid = dailyWaitingUuid;
    this.userId = userId;
    this.sequence = sequence;
    this.phone = phone;
    this.slackId = slackId;
    this.seatSize = seatSize;
    this.status = WaitingStatus.WAITING;
    this.histories = new ArrayList<>();
  }

  public static WaitingRequest of(String waitingRequestUuid, String dailyWaitingUuid, Long userId,
      Integer sequence, String phone, String slackId, Integer seatSize) {
    return new WaitingRequest(waitingRequestUuid, dailyWaitingUuid, userId, sequence, phone, slackId, seatSize);
  }

  public void addHistory(WaitingRequestHistory history) {
    history.registerWaitingRequest(this);
    this.histories.add(history);
  }
}
