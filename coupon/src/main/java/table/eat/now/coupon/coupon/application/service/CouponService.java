package table.eat.now.coupon.coupon.application.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.SearchCouponsQuery;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.response.AvailableCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.dto.response.SearchCouponInfo;

public interface CouponService {

  String createCoupon(CreateCouponCommand command);

  void updateCoupon(String couponUuid, UpdateCouponCommand command);

  GetCouponInfo getCoupon(String couponUuid);

  void deleteCoupon(CurrentUserInfoDto userInfo, String couponUuid);

  PageResponse<SearchCouponInfo> searchCoupons(Pageable pageable, SearchCouponsQuery query);

  GetCouponsInfoI getCouponsInternal(Set<UUID> couponUuids);

  PageResponse<AvailableCouponInfo> getAvailableCoupons(Pageable pageable, LocalDateTime time);

  UUID requestCouponIssue(CurrentUserInfoDto userInfoDto, String couponUuid);
}
