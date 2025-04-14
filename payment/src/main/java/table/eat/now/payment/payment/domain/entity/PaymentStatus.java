package table.eat.now.payment.payment.domain.entity;

public enum PaymentStatus {
  CREATED,
  APPROVED,
  CANCELED,
  REFUNDED,
  ;

  public boolean canChangeTo(PaymentStatus nextStatus) {
    return switch (this) {
      case CREATED -> nextStatus == APPROVED;
      case APPROVED -> nextStatus == CANCELED || nextStatus == REFUNDED;
      case CANCELED, REFUNDED -> false;
    };
  }
}
