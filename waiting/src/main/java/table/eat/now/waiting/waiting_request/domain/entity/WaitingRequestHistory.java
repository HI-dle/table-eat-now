package table.eat.now.waiting.waiting_request.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.HistoryBaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WaitingRequestHistory extends HistoryBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "waiting_request_uuid", referencedColumnName = "waiting_request_uuid")
  private WaitingRequest waitingRequest;

  @Column(nullable = false)
  private WaitingStatus status;

  @Builder
  private WaitingRequestHistory(WaitingStatus status) {
    this.status = status;
  }

  public static WaitingRequestHistory of(WaitingStatus status) {
    return new WaitingRequestHistory(status);
  }

  public void registerWaitingRequest(WaitingRequest waitingRequest) {
    this.waitingRequest = waitingRequest;
  }
}
