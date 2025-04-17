package table.eat.now.promotion.promotion.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotion.application.service.PromotionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionEventListener {

  private final PromotionService promotionService;

}