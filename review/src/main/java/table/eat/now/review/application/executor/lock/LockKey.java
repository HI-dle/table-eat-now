package table.eat.now.review.application.executor.lock;

public enum LockKey {

  RATING_UPDATE_RECENT("review:scheduler:recent-rating-update"),
  RATING_UPDATE_DAILY("review:scheduler:daily-rating-update"),
  ;

  private final String value;

  LockKey(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
