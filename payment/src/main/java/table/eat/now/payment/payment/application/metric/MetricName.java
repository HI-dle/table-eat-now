package table.eat.now.payment.payment.application.metric;

public enum MetricName {
  // Service
  PAYMENT_SERVICE_CONFIRM("payment.service.confirm"),
  PAYMENT_SERVICE_CANCEL("payment.service.cancel"),

  // Kafka
  PAYMENT_KAFKA_RESERVATION_CANCEL("payment.kafka.reservation.cancel"),
  PAYMENT_KAFKA_RESERVATION_CANCEL_DLT("payment.kafka.reservation.cancel.dlt"),
  ;

  private final String prefix;

  MetricName(String prefix) {
    this.prefix = prefix;
  }

  public String value() {
    return prefix;
  }
}

