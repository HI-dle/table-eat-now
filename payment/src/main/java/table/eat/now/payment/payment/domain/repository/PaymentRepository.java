package table.eat.now.payment.payment.domain.repository;

import table.eat.now.payment.payment.domain.entity.Payment;

public interface PaymentRepository {

  Payment save(Payment entity);
}
