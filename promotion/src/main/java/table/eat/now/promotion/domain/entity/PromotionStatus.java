package table.eat.now.promotion.domain.entity;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public enum PromotionStatus {
  READY(Description.READY),
  RUNNING(Description.RUNNING),
  CLOSED(Description.CLOSED);

  private final String description;

  PromotionStatus(String description) {
    this.description = description;
  }
  public String description() {
    return description;
  }

  public static class Description {
    private static final String READY = "READY";
    private static final String RUNNING = "RUNNING";
    private static final String CLOSED = "CLOSED";

  }
}
