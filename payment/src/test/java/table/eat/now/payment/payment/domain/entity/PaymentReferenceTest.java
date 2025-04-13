package table.eat.now.payment.payment.domain.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentReferenceTest {

  @Nested
  class create_는 {

    @Test
    void 유효한_입력값으로_ReviewReference_를_생성_할_수_있다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      String validReservationId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;
      String validReservationName = "멋진 이름을 가진 주문명";

      //when & then
      PaymentReference reference = assertDoesNotThrow(() -> PaymentReference.create(
          validRestaurantId, validReservationId, validCustomerId, validReservationName));

      assertThat(reference).isNotNull();
      assertThat(reference.getRestaurantId()).isEqualTo(validRestaurantId);
      assertThat(reference.getReservationId()).isEqualTo(validReservationId);
      assertThat(reference.getCustomerId()).isEqualTo(validCustomerId);
      assertThat(reference.getReservationName()).isEqualTo(validReservationName);
    }

    @Test
    void restaurantId가_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validReservationId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;
      String validReservationName = "멋진 이름을 가진 주문명";

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> PaymentReference.create(
              null, validReservationId, validCustomerId, validReservationName));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }

    @Test
    void reservationId가_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;
      String validReservationName = "멋진 이름을 가진 주문명";

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> PaymentReference.create(
              validRestaurantId, null, validCustomerId, validReservationName));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }

    @Test
    void customerId가_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      String validReservationId = UUID.randomUUID().toString();
      String validReservationName = "멋진 이름을 가진 주문명";

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> PaymentReference.create(
              validRestaurantId, validReservationId, null, validReservationName));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }

    @Test
    void reservationName이_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      String validReservationId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> PaymentReference.create(
              validRestaurantId, validReservationId, validCustomerId, null));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }

    @Test
    void 생성된_Review는_UUID_형식의_문자열의_customerKey를_갖는다() {
      // given
      String validRestaurantId = UUID.randomUUID().toString();
      String validReservationId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;
      String validReservationName = "멋진 이름을 가진 주문명";

      //when & then
      PaymentReference reference = assertDoesNotThrow(() -> PaymentReference.create(
          validRestaurantId, validReservationId, validCustomerId, validReservationName));

      assertThat(reference.getCustomerKey()).isNotNull();
      assertThat(reference.getCustomerKey()).isInstanceOf(String.class);
      assertThat(UUID.fromString(reference.getCustomerKey())).isNotNull();
    }
  }

}