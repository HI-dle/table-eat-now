package table.eat.now.payment.payment.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentTest {

  @Nested
  class create_는 {

    @Test
    void 유효한_입력값으로_Payment를_생성할_수_있다() {
      //given
      PaymentReference validReference = PaymentReference.create(
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString(),
          123L,
          "예약 이름");
      PaymentAmount validAmount = PaymentAmount.create(BigDecimal.valueOf(10000));

      //when & then
      Payment payment = assertDoesNotThrow(() -> Payment.create(validReference, validAmount));

      assertThat(payment).isNotNull();
      assertThat(payment.getReference()).isEqualTo(validReference);
      assertThat(payment.getAmount()).isEqualTo(validAmount);
      assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CREATED);
      assertThat(payment.getIdentifier()).isNotNull();
      assertThat(payment.getPaymentKey()).isNull();
      assertThat(payment.getApprovedAt()).isNull();
    }

    @Test
    void reference가_null이면_IllegalArgumentException을_던진다() {
      //given
      PaymentAmount validAmount = PaymentAmount.create(BigDecimal.valueOf(10000));

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> Payment.create(null, validAmount));

      assertThat(exception.getMessage()).contains("null일 수 없습니다");
    }

    @Test
    void amount가_null이면_IllegalArgumentException을_던진다() {
      //given
      PaymentReference validReference = PaymentReference.create(
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString(),
          123L,
          "예약 이름");

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> Payment.create(validReference, null));

      assertThat(exception.getMessage()).contains("null일 수 없습니다");
    }
  }

  @Nested
  class confirm_은 {

    @Test
    void 유효한_입력값으로_결제를_확정할_수_있다() {
      //given
      Payment payment = createValidPayment();
      String paymentKey = "payment_key_123456";
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      BigDecimal totalAmount = BigDecimal.valueOf(9000);
      ConfirmPayment confirmCommand =
          new ConfirmPayment(paymentKey, discountAmount, totalAmount, LocalDateTime.now());

      //when
      assertDoesNotThrow(() -> payment.confirm(confirmCommand));

      //then
      assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
      assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.APPROVED);
      assertThat(payment.getApprovedAt()).isNotNull();
    }

    @Test
    void paymentKey가_null이면_IllegalArgumentException을_던진다() {
      //given
      Payment payment = createValidPayment();
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      BigDecimal totalAmount = BigDecimal.valueOf(9000);
      ConfirmPayment confirmCommand =
          new ConfirmPayment(null, discountAmount, totalAmount, LocalDateTime.now());

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () ->
              payment.confirm(confirmCommand));

      assertThat(exception.getMessage()).contains("null이거나 빈 값일 수 없습니다");
    }

    @Test
    void paymentKey가_빈_문자열이면_IllegalArgumentException을_던진다() {
      //given
      Payment payment = createValidPayment();
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      BigDecimal totalAmount = BigDecimal.valueOf(9000);
      ConfirmPayment confirmCommand =
          new ConfirmPayment("", discountAmount, totalAmount, LocalDateTime.now());

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () ->
              payment.confirm(confirmCommand));

      assertThat(exception.getMessage()).contains("null이거나 빈 값일 수 없습니다");
    }

    @Test
    void 할인금액과_총액이_PaymentAmount에서_검증된다() {
      //given
      Payment payment = createValidPayment();
      String paymentKey = "payment_key_123456";
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      BigDecimal totalAmount = BigDecimal.valueOf(8000);
      ConfirmPayment confirmCommand =
          new ConfirmPayment(paymentKey, discountAmount, totalAmount, LocalDateTime.now());

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () ->
              payment.confirm(confirmCommand));

      assertThat(exception.getMessage()).contains("총 금액이 일치하지 않습니다.");
    }
  }

  private Payment createValidPayment() {
    PaymentReference validReference = PaymentReference.create(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        123L,
        "예약 이름");
    PaymentAmount validAmount = PaymentAmount.create(BigDecimal.valueOf(10000));
    return Payment.create(validReference, validAmount);
  }
}