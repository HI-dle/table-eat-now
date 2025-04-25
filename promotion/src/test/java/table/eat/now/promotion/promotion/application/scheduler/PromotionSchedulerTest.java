package table.eat.now.promotion.promotion.application.scheduler;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEventPublisher;
import table.eat.now.promotion.promotion.application.event.produce.PromotionScheduleEvent;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 25.
 */
@ExtendWith(MockitoExtension.class)
class PromotionSchedulerTest {

  @Mock
  PromotionRepository promotionRepository;

  @Mock
  PromotionEventPublisher publisher;

  @InjectMocks
  PromotionScheduler promotionScheduler;

  @Test
  @DisplayName("스케줄러가 Redis에 저장된 promotionUuid를 꺼내 PromotionScheduleEvent를 발행한다.")
  void promotion_scheduler_success() {
    // given
    String promotionUuid1 = "uuid-1";
    String promotionUuid2 = "uuid-2";

    List<String> uuidList = List.of(promotionUuid1, promotionUuid2);

    when(promotionRepository.pollScheduleQueue())
        .thenReturn(uuidList);

    Promotion promotion1 = Promotion.of(
        "coupon-uuid-1", "이벤트1", "할인1",
        LocalDateTime.now(), LocalDateTime.now().plusDays(1),
        BigDecimal.valueOf(5000), PromotionStatus.READY, PromotionType.COUPON, 1000
    );

    Promotion promotion2 = Promotion.of(
        "coupon-uuid-2", "이벤트2", "할인2",
        LocalDateTime.now(), LocalDateTime.now().plusDays(2),
        BigDecimal.valueOf(3000), PromotionStatus.RUNNING, PromotionType.COUPON, 1000
    );

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid1))
        .thenReturn(Optional.of(promotion1));
    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid2))
        .thenReturn(Optional.of(promotion2));

    ArgumentCaptor<PromotionScheduleEvent> eventCaptor = ArgumentCaptor.forClass(PromotionScheduleEvent.class);

    // when
    promotionScheduler.sendKafkaPromotionChangeStatus();

    // then
    verify(promotionRepository).pollScheduleQueue();
    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid1);
    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid2);

    verify(publisher, times(2)).publish(eventCaptor.capture());

    List<PromotionScheduleEvent> publishedEvents = eventCaptor.getAllValues();

    assertThat(publishedEvents).hasSize(2);

    PromotionScheduleEvent event1 = publishedEvents.get(0);
    assertThat(event1.payload().promotionName()).isEqualTo("이벤트1");
    assertThat(event1.payload().promotionStatus()).isEqualTo(PromotionStatus.READY.toString());

    PromotionScheduleEvent event2 = publishedEvents.get(1);
    assertThat(event2.payload().promotionName()).isEqualTo("이벤트2");
    assertThat(event2.payload().promotionStatus()).isEqualTo(PromotionStatus.RUNNING.toString());

  }

  @Test
  @DisplayName("유효하지 않은 promotionUuid가 있을 경우 예외가 발생한다.")
  void promotion_scheduler_invalid_promotionUuid() {
    // given
    String invalidPromotionUuid = "invalid-uuid";

    List<String> uuidList = List.of(invalidPromotionUuid);

    when(promotionRepository.pollScheduleQueue())
        .thenReturn(uuidList);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(invalidPromotionUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThrows(CustomException.class, () -> {
      promotionScheduler.sendKafkaPromotionChangeStatus();
    });
  }
  @Test
  @DisplayName("PromotionStatus가 READY와 RUNNING이 아닐 경우 예외가 발생한다.")
  void promotion_schedule_event_invalid_status() {
    // given
    Promotion promotionWithInvalidStatus = Promotion.of(
        "coupon-uuid-3", "이벤트3", "할인3",
        LocalDateTime.now(), LocalDateTime.now().plusDays(3),
        BigDecimal.valueOf(7000), PromotionStatus.CLOSED, PromotionType.COUPON, 1000
    );

    // when & then
    assertThrows(CustomException.class, () -> {
      PromotionScheduleEvent.from(promotionWithInvalidStatus);
    });
  }

  @Test
  @DisplayName("PromotionScheduleEvent가 정상적으로 발행된다.")
  void promotion_schedule_event_success() {
    // given
    Promotion promotion1 = Promotion.of(
        "coupon-uuid-1", "이벤트1", "할인1",
        LocalDateTime.now(), LocalDateTime.now().plusDays(1),
        BigDecimal.valueOf(5000), PromotionStatus.READY, PromotionType.COUPON, 1000
    );

    // when
    PromotionScheduleEvent event = PromotionScheduleEvent.from(promotion1);

    // then
    assertThat(event.eventType()).isEqualTo(EventType.START);
    assertThat(event.payload().promotionName()).isEqualTo("이벤트1");
    assertThat(event.payload().promotionStatus()).isEqualTo(PromotionStatus.READY.toString());
  }



}
