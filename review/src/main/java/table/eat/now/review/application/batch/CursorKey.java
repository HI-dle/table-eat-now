package table.eat.now.review.application.batch;

public enum CursorKey {

  RATING_UPDATE_RECENT_CURSOR("review:rating:batch:recent:cursor"),
  RATING_UPDATE_DAILY_CURSOR("review:rating:batch:daily:cursor"),
  ;

  private final String value;

  CursorKey(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
