package table.eat.now.review.domain.entity;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReviewReferenceTest {

  @Nested
  class create_는 {

    @Test
    void 유효한_입력값으로_ReviewReference_를_생성_할_수_있다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      String validServiceId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;
      ServiceType validServiceType = ServiceType.RESERVATION;

      //when & then
      ReviewReference reviewReference = assertDoesNotThrow(() -> ReviewReference.create(
          validRestaurantId, validServiceId, validCustomerId, validServiceType));

      assertThat(reviewReference).isNotNull();
      assertThat(reviewReference.getRestaurantId()).isEqualTo(validRestaurantId);
      assertThat(reviewReference.getServiceId()).isEqualTo(validServiceId);
      assertThat(reviewReference.getCustomerId()).isEqualTo(validCustomerId);
      assertThat(reviewReference.getServiceType()).isEqualTo(validServiceType);
    }

    @Test
    void restaurantId가_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validServiceId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;
      ServiceType validServiceType = ServiceType.RESERVATION;

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> ReviewReference.create(
              null, validServiceId, validCustomerId, validServiceType));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }

    @Test
    void serviceId가_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;
      ServiceType validServiceType = ServiceType.RESERVATION;

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> ReviewReference.create(
              validRestaurantId, null, validCustomerId, validServiceType));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }

    @Test
    void customerId가_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      String validServiceId = UUID.randomUUID().toString();
      ServiceType validServiceType = ServiceType.RESERVATION;

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> ReviewReference.create(
              validRestaurantId, validServiceId, null, validServiceType));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }

    @Test
    void serviceType이_null_이면_IllegalArgumentException_을_던진다() {
      //given
      String validRestaurantId = UUID.randomUUID().toString();
      String validServiceId = UUID.randomUUID().toString();
      Long validCustomerId = 123L;

      //when & then
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> ReviewReference.create(
              validRestaurantId, validServiceId, validCustomerId, null));

      assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
    }
  }
}