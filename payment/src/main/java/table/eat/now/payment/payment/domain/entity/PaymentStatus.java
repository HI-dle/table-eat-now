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

  public static PaymentStatus from(String name){
    try {
      return valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("유효하지 않은 결제 상태 입니다: " + name);
    }
  }
}
