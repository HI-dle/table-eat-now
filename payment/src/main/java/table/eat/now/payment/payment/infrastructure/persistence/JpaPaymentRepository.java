package table.eat.now.payment.payment.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.repository.PaymentRepository;

public interface JpaPaymentRepository extends
    JpaRepository<Payment, Long>, PaymentRepository, JpaPaymentRepositoryCustom {

  Optional<Payment> findByIdentifier_IdempotencyKeyAndDeletedAtNull(String idempotencyKey);

  Optional<Payment> findByReference_ReservationIdAndDeletedAtNull(String reservationId);

  Optional<Payment> findByIdentifier_PaymentUuidAndDeletedAtNull(String paymentUuid);
}
