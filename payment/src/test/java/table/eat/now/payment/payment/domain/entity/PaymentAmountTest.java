package table.eat.now.payment.payment.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentAmountTest {

  @Nested
  class create_는 {

    @Test
    void 유효한_금액으로_PaymentAmount를_생성할_수_있다() {
      //given
      BigDecimal validAmount = BigDecimal.valueOf(10000);

      //when & then
      PaymentAmount paymentAmount = assertDoesNotThrow(() -> PaymentAmount.create(validAmount));

      assertThat(paymentAmount).isNotNull();
      assertThat(paymentAmount.getOriginalAmount()).isEqualTo(validAmount);
      assertThat(paymentAmount.getDiscountAmount()).isNull();
      assertThat(paymentAmount.getTotalAmount()).isNull();
    }

    @Test
    void 금액이_null이면_IllegalArgumentException을_던진다() {
      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> PaymentAmount.create(null));

      assertThat(exception.getMessage()).contains("금액은 null이 될 수 없습니다");
    }

    @Test
    void 금액이_0이하이면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal zeroAmount = BigDecimal.ZERO;
      BigDecimal negativeAmount = BigDecimal.valueOf(-1000);

      //when & then
      IllegalArgumentException exception1 = assertThrows(
          IllegalArgumentException.class, () -> PaymentAmount.create(zeroAmount));
      IllegalArgumentException exception2 = assertThrows(
          IllegalArgumentException.class, () -> PaymentAmount.create(negativeAmount));

      assertThat(exception1.getMessage()).contains("금액은 0보다 커야 합니다");
      assertThat(exception2.getMessage()).contains("금액은 0보다 커야 합니다");
    }

    @Test
    void 금액이_8자리를_초과하면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal tooLargeAmount = BigDecimal.valueOf(100000000); // 9자리

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> PaymentAmount.create(tooLargeAmount));

      assertThat(exception.getMessage()).contains("금액은 최대 8자리 정수를 초과할 수 없습니다");
    }

    @Test
    void 금액이_소수점을_포함하면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal decimalAmount = BigDecimal.valueOf(1000.5);

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> PaymentAmount.create(decimalAmount));

      assertThat(exception.getMessage()).contains("금액은 정수여야 합니다");
    }
  }

  @Nested
  class confirm_은 {

    @Test
    void 유효한_할인금액과_총액으로_확정된_PaymentAmount를_생성할_수_있다() {
      //given
      BigDecimal originalAmount = BigDecimal.valueOf(10000);
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      BigDecimal totalAmount = BigDecimal.valueOf(9000);
      PaymentAmount paymentAmount = PaymentAmount.create(originalAmount);

      //when
      PaymentAmount confirmedAmount = assertDoesNotThrow(() ->
          paymentAmount.confirm(discountAmount, totalAmount));

      //then
      assertThat(confirmedAmount).isNotNull();
      assertThat(confirmedAmount.getOriginalAmount()).isEqualTo(originalAmount);
      assertThat(confirmedAmount.getDiscountAmount()).isEqualTo(discountAmount);
      assertThat(confirmedAmount.getTotalAmount()).isEqualTo(totalAmount);
    }

    @Test
    void 할인금액이_null이면_0으로_처리한다() {
      //given
      BigDecimal originalAmount = BigDecimal.valueOf(10000);
      BigDecimal totalAmount = BigDecimal.valueOf(10000);
      PaymentAmount paymentAmount = PaymentAmount.create(originalAmount);

      //when
      PaymentAmount confirmedAmount = assertDoesNotThrow(() ->
          paymentAmount.confirm(null, totalAmount));

      //then
      assertThat(confirmedAmount.getDiscountAmount()).isEqualTo(BigDecimal.ZERO);
      assertThat(confirmedAmount.getTotalAmount()).isEqualTo(totalAmount);
    }

    @Test
    void 총액이_null이면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal originalAmount = BigDecimal.valueOf(10000);
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      PaymentAmount paymentAmount = PaymentAmount.create(originalAmount);

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> paymentAmount.confirm(discountAmount, null));

      assertThat(exception.getMessage()).contains("금액은 null이 될 수 없습니다");
    }

    @Test
    void 할인금액이_원금보다_크면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal originalAmount = BigDecimal.valueOf(10000);
      BigDecimal discountAmount = BigDecimal.valueOf(11000);
      BigDecimal totalAmount = BigDecimal.valueOf(1000);
      PaymentAmount paymentAmount = PaymentAmount.create(originalAmount);

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () ->
              paymentAmount.confirm(discountAmount, totalAmount));

      assertThat(exception.getMessage()).contains("할인 금액은 원래 금액보다 클 수 없습니다");
    }

    @Test
    void 총액이_원금과_할인금액의_차이와_일치하지_않으면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal originalAmount = BigDecimal.valueOf(10000);
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      BigDecimal wrongTotalAmount = BigDecimal.valueOf(8000);
      PaymentAmount paymentAmount = PaymentAmount.create(originalAmount);

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () ->
              paymentAmount.confirm(discountAmount, wrongTotalAmount));

      assertThat(exception.getMessage()).contains("총 금액이 일치하지 않습니다");
    }

    @Test
    void 할인금액이_소수점을_포함하면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal originalAmount = BigDecimal.valueOf(10000);
      BigDecimal discountAmount = BigDecimal.valueOf(1000.5);
      BigDecimal totalAmount = BigDecimal.valueOf(9000);
      PaymentAmount paymentAmount = PaymentAmount.create(originalAmount);

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () ->
              paymentAmount.confirm(discountAmount, totalAmount));

      assertThat(exception.getMessage()).contains("금액은 정수여야 합니다");
    }

    @Test
    void 총액이_소수점을_포함하면_IllegalArgumentException을_던진다() {
      //given
      BigDecimal originalAmount = BigDecimal.valueOf(10000);
      BigDecimal discountAmount = BigDecimal.valueOf(1000);
      BigDecimal totalAmount = BigDecimal.valueOf(9000.5);
      PaymentAmount paymentAmount = PaymentAmount.create(originalAmount);

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () ->
              paymentAmount.confirm(discountAmount, totalAmount));

      assertThat(exception.getMessage()).contains("금액은 정수여야 합니다");
    }
  }
}