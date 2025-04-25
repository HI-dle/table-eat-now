package table.eat.now.coupon.user_coupon.fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

public class UserCouponFixture {

  public static List<UserCoupon> createList(int size, long userId) {
    return IntStream.range(0, size)
        .mapToObj(i -> UserCouponFixture.create(i, userId))
        .toList();
  }

  public static UserCoupon create(int i, long userId) {
    return UserCoupon.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), userId,
        "test 사용자 쿠폰 " + i, LocalDateTime.of(2025, 11, 30, 0, 0));
  }

  public static List<GetUserCouponInfo> createGetUserCouponInfoList(int size, long userId) {
    return IntStream.range(0, size)
        .mapToObj(i -> GetUserCouponInfo.builder()
            .id((long) i)
            .couponUuid(UUID.randomUUID().toString())
            .userCouponUuid(UUID.randomUUID().toString())
            .reservationUuid(UUID.randomUUID().toString())
            .userId(userId)
            .name("test 쿠폰 " + i)
            .status("ISSUED")
            .expiresAt(LocalDateTime.of(2025, 11, 30, 0, 0))
            .preemptAt(null)
            .usedAt(null)
            .createdAt(LocalDateTime.now())
            .createdBy(2L)
            .build())
        .toList();
  }
//
//  public static List<GetUserCouponInfoI> createGetUserCouponInfoIList(int size, Long userId) {
//
//
//  }
//
}
