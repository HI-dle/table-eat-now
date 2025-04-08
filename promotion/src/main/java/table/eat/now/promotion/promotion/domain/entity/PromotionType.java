package table.eat.now.promotion.promotion.domain.entity;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public enum PromotionType {
  COUPON(Description.COUPON),
  RESTAURANT(Description.RESTAURANT);

  private final String description;

  PromotionType(String description) {
    this.description = description;
  }
  public String description() {
    return description;
  }

  public static class Description {
    private static final String COUPON = "COUPON";
    private static final String RESTAURANT = "RESTAURANT";

  }
}
