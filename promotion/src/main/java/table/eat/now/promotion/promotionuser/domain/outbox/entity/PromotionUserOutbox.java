package table.eat.now.promotion.promotionuser.domain.outbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
@Entity
@Table(name = "promotion_user_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionUserOutbox {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String aggregateId; // promotionUserUuid

  @Enumerated(EnumType.STRING)
  private OutboxStatus status = OutboxStatus.PENDING;

  @Lob
  private String payload; // JSON으로 PromotionUser 알림 데이터

  private int retryCount = 0;

  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt = LocalDateTime.now();

  private PromotionUserOutbox(String aggregateId, String payload) {
    this.aggregateId = aggregateId;
    this.payload = payload;
  }

  public static PromotionUserOutbox create(String aggregateId, String payload) {
    return new PromotionUserOutbox(aggregateId, payload);
  }

  public void modifyStatusSuccess() {
    this.status = OutboxStatus.SUCCESS;
    this.updatedAt = LocalDateTime.now();
  }
  public void modifyStatusFailed() {
    this.status = OutboxStatus.FAILED;
    this.updatedAt = LocalDateTime.now();
  }

  public void incrementRetry() {
    this.retryCount++;
    this.updatedAt = LocalDateTime.now();
  }
}

