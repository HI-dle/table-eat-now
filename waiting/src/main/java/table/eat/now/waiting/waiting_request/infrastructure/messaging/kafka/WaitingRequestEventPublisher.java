package table.eat.now.waiting.waiting_request.infrastructure.messaging.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.waiting.waiting_request.application.event.EventPublisher;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.infrastructure.messaging.kafka.config.WaitingRequestTopicConfig;

@Component
@RequiredArgsConstructor
public class WaitingRequestEventPublisher implements EventPublisher<WaitingRequestEvent> {

  private final KafkaTemplate<String, WaitingRequestEvent> kafkaTemplate;

  @Override
  public void publish(WaitingRequestEvent event) {
    kafkaTemplate.send(WaitingRequestTopicConfig.TOPIC_NAME, event.waitingRequestUuid(), event);
  }
}
