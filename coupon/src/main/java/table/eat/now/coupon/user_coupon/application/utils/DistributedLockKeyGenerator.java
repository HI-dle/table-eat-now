package table.eat.now.coupon.user_coupon.application.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.user_coupon.application.exception.UserCouponErrorCode;

public class DistributedLockKeyGenerator {
  private DistributedLockKeyGenerator() {
    throw new IllegalStateException("Utility class");
  }
  private static final String USER_COUPON_LOCK_PREFIX = "user:coupon:lock:";

  public static List<String> generateKeys(
      final String subPrefix,
      final String spelExpression,
      final String[] parameterNames,
      final Object[] args) {

    if (spelExpression == null) {
      return Collections.singletonList(USER_COUPON_LOCK_PREFIX);
    }

    List<String> keys = CustomSpringELParser.parseExpression(
        subPrefix,spelExpression, parameterNames, args, new TypeReference<Set<String>>() {})
        .stream()
        .map(val -> USER_COUPON_LOCK_PREFIX + subPrefix + val)
        .toList();

    if (keys.isEmpty()) {
      throw CustomException.from(UserCouponErrorCode.NON_LOCK_KEY);
    }
    return keys;
  }
}
