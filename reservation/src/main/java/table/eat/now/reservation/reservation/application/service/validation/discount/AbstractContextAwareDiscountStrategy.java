/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.discount;

import java.util.Map;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;

public abstract class AbstractContextAwareDiscountStrategy implements DiscountStrategy {
  protected Map<String, Coupon> couponMap;
  protected Map<String, GetPromotionsInfo.Promotion> promotionMap;

  public void setContext(Map<String, Coupon> couponMap, Map<String, GetPromotionsInfo.Promotion> promotionMap) {
    this.couponMap = couponMap;
    this.promotionMap = promotionMap;
  }
}
