package table.eat.now.payment.payment.domain.entity;

public enum PaymentStatus {
  PENDING,
  APPROVED,
  CANCELED,
  REFUNDED,
  ;

  public boolean canChangeTo(PaymentStatus nextStatus) {
    return switch (this) {
      case PENDING -> nextStatus == APPROVED || nextStatus == CANCELED;
      case APPROVED -> nextStatus == CANCELED || nextStatus == REFUNDED;
      case CANCELED, REFUNDED -> false;
    };
  }
}
