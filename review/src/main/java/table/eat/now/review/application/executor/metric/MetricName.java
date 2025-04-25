package table.eat.now.review.application.executor.metric;

public enum MetricName {
  // Scheduler
  RATING_UPDATE_RECENT("review.scheduler.update.recent"),
  RATING_UPDATE_DAILY("review.scheduler.update.daily"),
  ;

  private final String prefix;

  MetricName(String prefix) {
    this.prefix = prefix;
  }

  public String value() {
    return prefix;
  }
}

