package table.eat.now.waiting.waiting_request.infrastructure.event.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.waiting.waiting_request.application.event.EventPublisher;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.infrastructure.event.kafka.config.TopicConfig;

@Component
@RequiredArgsConstructor
public class WaitingRequestEventPublisher implements EventPublisher<WaitingRequestEvent> {

  private final KafkaTemplate<String, WaitingRequestEvent> kafkaTemplate;

  @Override
  public void publish(WaitingRequestEvent event) {
    kafkaTemplate.send(TopicConfig.TOPIC_NAME, event.waitingRequestUuid() ,event);
  }
}
