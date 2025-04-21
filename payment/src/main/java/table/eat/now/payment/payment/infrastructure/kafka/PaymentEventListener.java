package table.eat.now.payment.payment.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.infrastructure.kafka.event.ReservationCancelledEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

  private final PaymentService paymentService;

  @KafkaListener(
      topics = "reservation-event",
      containerFactory = "cancelledEventKafkaListenerContainerFactory"
  )
  public void handleReservationCanceled(ReservationCancelledEvent cancelEvent) {
    paymentService.cancelPayment(cancelEvent.payload().toCommand(), cancelEvent.userInfo());
  }
}