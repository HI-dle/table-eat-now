package table.eat.now.notification.application.Strategy.send;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import table.eat.now.notification.application.Strategy.NotificationTemplate;
import table.eat.now.notification.domain.entity.NotificationMethod;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SlackSender implements NotificationSenderStrategy {

  private final WebClient webClient;

  @Value("${slack.webhook-url}")
  private String webhookUrl;

  @Override
  public NotificationMethod getMethod() {
    return NotificationMethod.SLACK;
  }

  @Override
  public void send(Long userId, NotificationTemplate template) {
    String message = String.format("*[%s]*\n%s", template.title(), template.body());

    webClient.post()
        .uri(webhookUrl)
        .bodyValue(Map.of("text", message))
        .retrieve()
        .bodyToMono(String.class)
        .onErrorResume(e -> {
          slackError(e);
          return Mono.empty();
        })
        .doOnTerminate(() -> log.info("Slack 메시지 전송 완료"))
        .subscribe();
  }

  public void slackError(Throwable e) {
    log.error("Slack 메시지 전송 실패: " + e.getMessage());
  }
}
