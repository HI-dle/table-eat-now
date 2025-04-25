package table.eat.now.coupon.user_coupon.application.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.user_coupon.application.aop.annotation.DistributedLock;
import table.eat.now.coupon.user_coupon.application.client.CouponClient;
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfoI;
import table.eat.now.coupon.user_coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.user_coupon.application.exception.UserCouponErrorCode;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;

@RequiredArgsConstructor
@Service
public class UserCouponServiceImpl implements UserCouponService {
  private final UserCouponRepository userCouponRepository;
  private final CouponClient couponClient;

  @Override
  public void createUserCoupon(IssueUserCouponCommand command) {
    UserCoupon userCoupon = command.toEntity();
    userCouponRepository.save(userCoupon);
  }

  @Transactional
  @Override
  public void preemptUserCoupons(
      CurrentUserInfoDto userInfoDto, PreemptUserCouponCommand command) {

    List<UserCoupon> userCoupons =
        userCouponRepository.findByUserCouponUuidsInAndDeletedAtIsNullWithLock(command.userCouponUuids());
    userCoupons.stream()
        .sorted(Comparator.comparing(UserCoupon::getUserCouponUuid))
        .forEach(userCoupon -> {
          if (userInfoDto.role() == UserRole.CUSTOMER) {
            userCoupon.isOwnedBy(userInfoDto.userId());
          }
          userCoupon.isValidToPreempt(command.reservationUuid());
          userCoupon.preempt(command.reservationUuid());
        });
  }

  @DistributedLock(key = "#command.userCouponUuids")
  @Override
  public void preemptUserCouponsWithDistributedLock(
      CurrentUserInfoDto userInfoDto, PreemptUserCouponCommand command) {

    command.userCouponUuids()
        .stream()
        .sorted()
        .forEach(userCouponUuid -> {
          UserCoupon userCoupon =
              userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
                  .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

          if (userInfoDto.role() == UserRole.CUSTOMER) {
            userCoupon.isOwnedBy(userInfoDto.userId());
          }
          userCoupon.isValidToPreempt(command.reservationUuid());
          userCoupon.preempt(command.reservationUuid());
        });
  }

  @Override
  public PageResponse<GetUserCouponInfo> getUserCouponsByUserId(
      CurrentUserInfoDto userInfoDto, Pageable pageable) {

    Page<GetUserCouponInfo> userCouponInfos =
        userCouponRepository.findByUserIdAndExpiresAtAfterAndDeletedAtIsNull(
            userInfoDto.userId(), LocalDateTime.now(), pageable)
        .map(GetUserCouponInfo::from);
    return PageResponse.from(userCouponInfos);
  }

  @Transactional
  @Override
  public void cancelUserCoupons(String reservationUuid) {

    List<UserCoupon> userCoupons = userCouponRepository.findByReservationUuid(reservationUuid);
    userCoupons.forEach(UserCoupon::release);
  }

  @Override
  public List<GetUserCouponInfoI> getUserCouponsInternalBy(Set<String> userCouponUuids) {

    List<UserCoupon> userCoupons =
        userCouponRepository.findByUserCouponUuidInAndDeletedAtIsNull(userCouponUuids);

    Set<String> couponUuids = userCoupons.stream()
        .map(UserCoupon::getCouponUuid)
        .collect(Collectors.toSet());
    Map<String, GetCouponInfoI> couponsMap = couponClient.getCouponsByCouponUuids(couponUuids);

    return userCoupons.stream()
        .map(userCoupon -> GetUserCouponInfoI.from(
            userCoupon,
            couponsMap.get(userCoupon.getCouponUuid())))
        .toList();
  }
}
