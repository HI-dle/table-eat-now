package table.eat.now.payment.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.repository.PaymentRepository;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long>, PaymentRepository {

}
