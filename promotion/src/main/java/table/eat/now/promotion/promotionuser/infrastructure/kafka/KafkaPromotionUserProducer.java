package table.eat.now.promotion.promotionuser.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotionuser.infrastructure.kafka.dto.PromotionSendEvent;
import table.eat.now.promotion.promotionuser.infrastructure.kafka.dto.PromotionSendPayload;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPromotionUserProducer {

  private final KafkaTemplate<String, PromotionSendEvent> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String notificationTopic;
  private final String notificationTopicDlt;



  public void publish(String aggregateId, String payload) {
    try {
      PromotionSendPayload promotionSendPayload = objectMapper.readValue(payload,
          PromotionSendPayload.class);
      kafkaTemplate.send(notificationTopic, aggregateId, PromotionSendEvent.from(promotionSendPayload));
    }catch (JsonProcessingException e) {
      throw new RuntimeException("Outbox payload 변환 실패", e);
    }

  }

}