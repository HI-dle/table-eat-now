package table.eat.now.notification.application.event;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
public interface NotificationEventPublisher {
  void publish(NotificationEvent event);

}
