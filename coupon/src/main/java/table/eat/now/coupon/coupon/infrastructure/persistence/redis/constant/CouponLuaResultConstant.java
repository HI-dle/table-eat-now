package table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant;

import java.util.Arrays;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.exception.type.ErrorCode;
import table.eat.now.coupon.coupon.infrastructure.exception.CouponInfraErrorCode;

public class CouponLuaResultConstant {

  @Getter
  @RequiredArgsConstructor
  public enum IssueResult {
    SUCCESS(
        (result) -> result == 1,
        null),
    IDEMPOTENCY_ERROR(
        (result) -> result == 2,
        CouponInfraErrorCode.IDEMPOTENCY_ERROR),
    DUPLICATED_REQUEST(
        (result) -> result ==-1,
        CouponInfraErrorCode.DUPLICATED_REQUEST),
    NOT_ENOUGH_STOCK(
        (result) -> result ==-2,
        CouponInfraErrorCode.NOT_ENOUGH_STOCK),
    INVALID_COUPON_STOCK(
        (result) -> result ==-3,
        CouponInfraErrorCode.INVALID_COUPON_STOCK),
    ;

    private final Predicate<Long> predicate;
    private final CouponInfraErrorCode errorCode;

    public static ErrorCode parseToErrorCode(Long result) {
      return Arrays.stream(IssueResult.values())
          .filter(issueResult -> issueResult.getPredicate().test(result))
          .findFirst()
          .map(IssueResult::getErrorCode)
          .orElse(CouponInfraErrorCode.REQUESTED_ISSUE_FAILED);
    }
  }
}
