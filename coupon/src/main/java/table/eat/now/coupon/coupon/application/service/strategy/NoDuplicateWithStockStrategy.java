package table.eat.now.coupon.coupon.application.service.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class NoDuplicateWithStockStrategy implements CouponIssueStrategy {
  private final CouponRepository couponRepository;

  @Override
  public boolean support(Coupon coupon) {
    return !coupon.getAllowDuplicate() && coupon.hasStockCount();
  }

  @Override
  public void issue(String couponUuid, Long userId) {
    checkAlreadyIssued(couponUuid, userId);
    try {
      checkStockAndDecrease(couponUuid);
      markAsIssued(couponUuid, userId);
    } catch (CustomException e) {
      rollbackStock(couponUuid);
      throw e;
    }
  }

  private void checkAlreadyIssued(String couponUuid, Long userId) {
    boolean alreadyIssued = couponRepository.isAlreadyIssued(couponUuid, userId);
    if (alreadyIssued) {
      throw CustomException.from(CouponErrorCode.ALREADY_ISSUED);
    }
  }

  private void checkStockAndDecrease(String couponUuid) {
    Long remainder = couponRepository.decreaseCouponCount(couponUuid);
    boolean result = remainder !=null && remainder >= 0;
    if (!result) {
      throw CustomException.from(CouponErrorCode.INSUFFICIENT_STOCK);
    }
  }

  private void markAsIssued(String couponUuid, Long userId) {
    boolean result = couponRepository.markAsIssued(couponUuid, userId);
    if (!result) {
      throw CustomException.from(CouponErrorCode.ALREADY_ISSUED);
    }
  }

  private void rollbackStock(String couponUuid) {
    Long remainder = couponRepository.increaseCouponCount(couponUuid);
    boolean result = remainder !=null;
    if (!result) {
      log.error("쿠폰 재고 수량 롤백 수행 실패::couponUuid:{}", couponUuid);
      throw CustomException.from(CouponErrorCode.FAILED_ROLLBACK_COUNT);
    }
  }
}
