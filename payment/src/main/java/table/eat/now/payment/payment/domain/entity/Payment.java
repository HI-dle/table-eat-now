package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import table.eat.now.common.domain.BaseEntity;

@Getter
@Entity
@Table(name = "p_payment")
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private PaymentReference reference;

  @Embedded
  private PaymentIdentifier identifier;
  
  @Column(unique = true)
  private String paymentKey;
  @Column(nullable = false)
  
  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  @Embedded
  private PaymentAmount amount;

  private LocalDateTime approvedAt;

  private LocalDateTime cancelledAt;

  public static Payment create(PaymentReference reference, PaymentAmount amount) {
    validateNull(reference, amount);
    return new Payment(reference, amount);
  }

  private static void validateNull(PaymentReference reference, PaymentAmount amount) {
    if (reference == null || amount == null) {
      throw new IllegalArgumentException("null일 수 없습니다.");
    }
  }

  public void confirm(ConfirmPayment confirmPayment) {
    validatePaymentKey(confirmPayment.paymentKey());

    this.paymentKey = confirmPayment.paymentKey();
    this.paymentStatus = validateStatus(PaymentStatus.APPROVED);
    this.amount = amount.confirm(confirmPayment.discountAmount(), confirmPayment.totalAmount());
    //LocalDAteTime.now()
    this.approvedAt = confirmPayment.approvedAt();
  }

  private void validatePaymentKey(String paymentKey) {
    if (paymentKey == null || paymentKey.isBlank()) {
      throw new IllegalArgumentException("paymentKey는 null이거나 빈 값일 수 없습니다");
    }
  }

  private PaymentStatus validateStatus(PaymentStatus nextStatus) {
    if(this.paymentStatus.canChangeTo(nextStatus)){
      return nextStatus;
    }
    throw new IllegalArgumentException("해당 상태로 변경할 수 없습니다.");
  }

  public void cancel(CancelPayment cancelPayment) {
    validatePaymentKey(cancelPayment.paymentKey());

    this.paymentStatus = validateStatus(PaymentStatus.CANCELED);
    this.cancelledAt = cancelPayment.cancelledAt();
  }

  public String getIdempotencyKey() {
    return this.identifier.getIdempotencyKey();
  }

  private Payment(PaymentReference reference, PaymentAmount amount) {
    this.reference = reference;
    this.identifier = PaymentIdentifier.create();
    this.paymentStatus = PaymentStatus.CREATED;
    this.amount = amount;
  }

  protected Payment() {
  }
}
