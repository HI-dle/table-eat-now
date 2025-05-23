package table.eat.now.payment.payment.domain.repository;

import java.util.Optional;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.repository.search.PaginatedResult;
import table.eat.now.payment.payment.domain.repository.search.SearchPaymentsResult;
import table.eat.now.payment.payment.domain.repository.search.SearchPaymentsCriteria;

public interface PaymentRepository {

  Payment save(Payment entity);

  Optional<Payment> findByIdentifier_IdempotencyKeyAndDeletedAtNull(String idempotencyKey);

  Optional<Payment> findByReference_ReservationIdAndDeletedAtNull(String reservationId);

  Optional<Payment> findByIdentifier_PaymentUuidAndDeletedAtNull(String paymentUuid);

  PaginatedResult<SearchPaymentsResult> searchPayments(SearchPaymentsCriteria criteria);
}
