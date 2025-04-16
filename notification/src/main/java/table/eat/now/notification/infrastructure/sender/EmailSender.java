package table.eat.now.notification.infrastructure.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import table.eat.now.notification.application.strategy.NotificationTemplate;
import table.eat.now.notification.application.strategy.send.NotificationSenderStrategy;
import table.eat.now.notification.domain.entity.NotificationMethod;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Component
@RequiredArgsConstructor
public class EmailSender implements NotificationSenderStrategy {

  private final JavaMailSender mailSender;

  @Override
  public NotificationMethod getMethod() {
    return NotificationMethod.EMAIL;
  }

  @Override
  public void send(Long userId, NotificationTemplate template) {
    SimpleMailMessage message = new SimpleMailMessage();
    //이메일은 차후 받을 방법을 찾아보도록 하겠습니다...
    message.setTo("이메일");
    message.setSubject(template.title());
    message.setText(template.body());

    mailSender.send(message);
  }
}
