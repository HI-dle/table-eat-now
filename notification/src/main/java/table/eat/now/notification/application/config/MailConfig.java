package table.eat.now.notification.application.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
@Configuration
public class MailConfig {

  @Value("${spring.mail.host}")
  private String host;
  @Value("${spring.mail.port}")
  private int port;
  @Value("${spring.mail.username}")
  private String username;
  @Value("${spring.mail.password}")
  private String password;

  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);

    mailSender.setUsername(username);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }
}
