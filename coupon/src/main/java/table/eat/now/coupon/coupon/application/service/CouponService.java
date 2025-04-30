package table.eat.now.coupon.coupon.application.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.application.service.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.service.dto.request.SearchCouponsQuery;
import table.eat.now.coupon.coupon.application.service.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.service.dto.response.IssuableCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.service.dto.response.SearchCouponInfo;

public interface CouponService {

  String createCoupon(CreateCouponCommand command);

  void updateCoupon(String couponUuid, UpdateCouponCommand command);

  GetCouponInfo getCouponInfo(String couponUuid);

  void deleteCoupon(CurrentUserInfoDto userInfo, String couponUuid);

  PageResponse<SearchCouponInfo> searchCoupons(Pageable pageable, SearchCouponsQuery query);

  GetCouponsInfoI getCouponsInternal(Set<UUID> couponUuids);

  PageResponse<IssuableCouponInfo> getAvailableGeneralCoupons(Pageable pageable, LocalDateTime time);

  GetCouponsInfo getDailyIssuablePromotionCoupons();

  GetCouponsInfoI getDailyIssuablePromotionCouponsInternal();

  String requestCouponIssue(CurrentUserInfoDto userInfoDto, String couponUuid);
}
