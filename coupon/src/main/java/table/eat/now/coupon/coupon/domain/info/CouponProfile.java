package table.eat.now.coupon.coupon.domain.info;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CouponProfile {
  GENERAL_BASE("기본 발급"),
  GENERAL_LIMITED("기본 한정 수량 발급"),
  GENERAL_NONDUP("기본 중복 제한 발급"),
  GENERAL_LIMITED_NONDUP("기본 한정 수량 및 중복 제한 발급"),
  HOT_LIMITED("핫딜 한정 수량 발급"),
  HOT_NONDUP("핫딜 중복 제한 발급"),
  HOT_LIMITED_NONDUP("핫딜 한정 수량 및 중복 제한 발급"),
  PROMO_LIMITED("프로모션 한정 수량 발급"),
  PROMO_NONDUP("프로모션 중복 제한 발급"),
  PROMO_LIMITED_NONDUP("프로모션 한정 수량 및 중복 제한 발급"),
  SYSTEM_NONDUP("시스템 중복 제한 발급"),
  ;

  private final String description;

  public static CouponProfile parse(Coupon coupon) {
    if (coupon.hasStockCount() && !coupon.getAllowDuplicate()) {
      if (coupon.isPromoLabel()) {
        return CouponProfile.PROMO_LIMITED_NONDUP;
      }
      return CouponProfile.HOT_LIMITED_NONDUP;
    }
    if (coupon.hasStockCount() && coupon.getAllowDuplicate()) {
      return CouponProfile.HOT_LIMITED;
    }
    if (!coupon.hasStockCount() && !coupon.getAllowDuplicate()) {
      return CouponProfile.HOT_NONDUP;
    }
    if (!coupon.hasStockCount() && coupon.getAllowDuplicate()) {
      return CouponProfile.GENERAL_BASE;
    }
    throw CustomException.from(CouponErrorCode.NON_EXIST_STRATEGY);
  }
}
