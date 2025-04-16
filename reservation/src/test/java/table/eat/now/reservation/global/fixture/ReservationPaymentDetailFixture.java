/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
package table.eat.now.reservation.global.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import table.eat.now.reservation.global.util.LongIdGenerator;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail.PaymentType;

public class ReservationPaymentDetailFixture {
  public static ReservationPaymentDetail create(
      BigDecimal amount,
      String detailReferenceId,
      PaymentType type
  ) {
    return ReservationPaymentDetail.builder()
        .amount(amount)
        .detailReferenceId(detailReferenceId)
        .type(type)
        .build();
  }

  public static ReservationPaymentDetail createRandomByType(PaymentType type) {
    Long num = LongIdGenerator.makeLong();
    BigDecimal amount = BigDecimal.valueOf(num + 1000);
    String detailReferenceId = UUID.randomUUID().toString();
    return create(
        amount,
        detailReferenceId,
        type
    );
  }
}
