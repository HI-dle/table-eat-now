package table.eat.now.payment.payment.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentIdentifierTest {

  @Nested
  class create_는 {

    @Test
    void 유효한_PaymentIdentifier_를_생성_할_수_있다() {
      // when
      PaymentIdentifier identifier = assertDoesNotThrow(PaymentIdentifier::create);

      // then
      assertThat(identifier).isNotNull();
      assertThat(identifier.getPaymentUuid()).isNotNull();
      assertThat(identifier.getIdempotencyKey()).isNotNull();
      assertThat(identifier.getPaymentUuid()).isNotEmpty();
      assertThat(identifier.getIdempotencyKey()).isNotEmpty();
    }

    @Test
    void 서로_다른_UUID_값을_가진_PaymentIdentifier_를_생성한다() {
      // when
      PaymentIdentifier identifier1 = PaymentIdentifier.create();
      PaymentIdentifier identifier2 = PaymentIdentifier.create();

      // then
      assertThat(identifier1.getPaymentUuid()).isNotEqualTo(identifier2.getPaymentUuid());
      assertThat(identifier1.getIdempotencyKey()).isNotEqualTo(identifier2.getIdempotencyKey());
    }

    @Test
    void UUID_형식의_문자열을_생성한다() {
      // when
      PaymentIdentifier identifier = PaymentIdentifier.create();

      // then
      assertThat(identifier.getIdempotencyKey()).isNotNull();
      assertThat(identifier.getIdempotencyKey()).isInstanceOf(String.class);
      assertThat(UUID.fromString(identifier.getIdempotencyKey())).isNotNull();
      assertThat(identifier.getPaymentUuid()).isNotNull();
      assertThat(identifier.getPaymentUuid()).isInstanceOf(String.class);
      assertThat(UUID.fromString(identifier.getPaymentUuid())).isNotNull();
    }
  }
}