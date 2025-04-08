package table.eat.now.notification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Entity
@Table(name = "p_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
  private UUID notificationUuid;

  @Column(nullable = false, name = "user_id")
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "notification_type")
  private NotificationType notificationType;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "notification_method")
  private NotificationMethod notificationMethod;

  @Column(name = "scheduled_time")
  private LocalDateTime scheduledTime;

  private Notification(Long userId, NotificationType notificationType,
      String message, NotificationStatus status, NotificationMethod notificationMethod,
      LocalDateTime scheduledTime) {
    this.notificationUuid = UUID.randomUUID();
    this.userId = userId;
    this.notificationType = notificationType;
    this.message = message;
    this.status = status;
    this.notificationMethod = notificationMethod;
    this.scheduledTime = scheduledTime;
  }

  public static Notification of(Long userId, NotificationType notificationType,
      String message, NotificationStatus status, NotificationMethod notificationMethod,
      LocalDateTime scheduledTime) {
    return new Notification(
        userId, notificationType, message, status,
        notificationMethod, scheduledTime);
  }
}
