package table.eat.now.coupon.coupon.application.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;

@Slf4j
@RequiredArgsConstructor
@Component
public class IssueLimitedNonDupHotStrategy implements IssueStrategy {
  private final CouponReader couponReader;
  private final CouponStore couponStore;

  @Override
  public void requestIssue(String couponUuid, Long userId) {
    checkAlreadyIssued(couponUuid, userId);
    try {
      checkStockAndDecrease(couponUuid);
      markAsIssued(couponUuid, userId);
    } catch (CustomException e) {
      rollbackStock(couponUuid);
      throw e;
    }
  }

  @Override
  public CouponProfile couponProfile() {
    return CouponProfile.HOT_LIMITED_NONDUP;
  }

  private void checkAlreadyIssued(String couponUuid, Long userId) {
    boolean alreadyIssued = couponReader.isAlreadyIssued(couponUuid, userId);
    if (alreadyIssued) {
      throw CustomException.from(CouponErrorCode.ALREADY_ISSUED);
    }
  }

  private void checkStockAndDecrease(String couponUuid) {
    Long remainder = couponStore.decreaseCouponCount(couponUuid);
    boolean result = remainder !=null && remainder >= 0;
    if (!result) {
      throw CustomException.from(CouponErrorCode.INSUFFICIENT_STOCK);
    }
  }

  private void markAsIssued(String couponUuid, Long userId) {
    boolean result = couponStore.markAsIssued(couponUuid, userId);
    if (!result) {
      throw CustomException.from(CouponErrorCode.ALREADY_ISSUED);
    }
  }

  private void rollbackStock(String couponUuid) {
    Long remainder = couponStore.increaseCouponCount(couponUuid);
    boolean result = remainder !=null;
    if (!result) {
      log.error("쿠폰 재고 수량 롤백 수행 실패::couponUuid:{}", couponUuid);
      throw CustomException.from(CouponErrorCode.FAILED_ROLLBACK_COUNT);
    }
  }
}
