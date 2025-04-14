package table.eat.now.notification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import table.eat.now.common.domain.BaseEntity;
import table.eat.now.notification.domain.entity.vo.MessageParam;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Entity
@Table(name = "p_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100, name = "notification_uuid")
  private String notificationUuid;

  @Column(nullable = false, name = "user_id")
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "notification_type")
  private NotificationType notificationType;


  @Embedded
  private MessageParam messageParam;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "notification_method")
  private NotificationMethod notificationMethod;

  @Column(name = "scheduled_time")
  private LocalDateTime scheduledTime;

  private Notification(Long userId, NotificationType notificationType,
      String customerName, LocalDateTime reservationTime, String restaurantName,
      NotificationStatus status, NotificationMethod notificationMethod,
      LocalDateTime scheduledTime) {
    this.notificationUuid = UUID.randomUUID().toString();
    this.userId = userId;
    this.notificationType = notificationType;
    this.messageParam = MessageParam.of(customerName, reservationTime, restaurantName);
    this.status = status;
    this.notificationMethod = notificationMethod;
    this.scheduledTime = scheduledTime;
  }

  public static Notification of(Long userId, NotificationType notificationType,
      String customerName, LocalDateTime reservationTime, String restaurantName,
      NotificationStatus status, NotificationMethod notificationMethod,
      LocalDateTime scheduledTime) {
    return new Notification(
        userId, notificationType, customerName,reservationTime, restaurantName, status,
        notificationMethod, scheduledTime);
  }

  public void modifyNotification(Long userId, String notificationType,
      String customerName, LocalDateTime reservationTime, String restaurantName,
      String status, String notificationMethod,
      LocalDateTime scheduledTime) {
    this.userId = userId;
    this.notificationType = NotificationType.valueOf(notificationType);
    this.messageParam = MessageParam.of(customerName, reservationTime, restaurantName);
    this.status = NotificationStatus.valueOf(status);
    this.notificationMethod = NotificationMethod.valueOf(notificationMethod);
    this.scheduledTime = scheduledTime;
  }

}
