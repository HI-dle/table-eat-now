package table.eat.now.coupon.coupon.application.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.SearchCouponsQuery;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
  private final CouponRepository couponRepository;

  @Override
  public String createCoupon(CreateCouponCommand command) {

    Coupon coupon = command.toEntity();
    couponRepository.save(coupon);
    return coupon.getCouponUuid();
  }

  @Transactional
  @Override
  public void updateCoupon(UUID couponUuid, UpdateCouponCommand command) {

    Coupon coupon = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid.toString())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    coupon.modify(command.toDomainCommand());
  }

  @Transactional(readOnly = true)
  @Override
  public GetCouponInfo getCoupon(UUID couponUuid) {

    Coupon coupon = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid.toString())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    return GetCouponInfo.from(coupon);
  }

  @Transactional
  @Override
  public void deleteCoupon(CurrentUserInfoDto userInfo, UUID couponUuid) {

    Coupon coupon = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid.toString())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    coupon.delete(userInfo.userId());
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<SearchCouponInfo> getCoupons(Pageable pageable, SearchCouponsQuery query) {

    Page<SearchCouponInfo> couponInfoPage =
        couponRepository.searchCouponByPageableAndCondition(pageable, query.toCriteria())
        .map(SearchCouponInfo::from);
    return PageResponse.from(couponInfoPage);
  }

  @Transactional(readOnly = true)
  @Override
  public GetCouponsInfoI getCouponsInternal(Set<UUID> couponUuids) {

    Set<String> couponUuidsStr = couponUuids.stream().map(UUID::toString).collect(Collectors.toSet());
    List<Coupon> coupons = couponRepository.findByCouponUuidsInAndDeletedAtIsNullFetchJoin(couponUuidsStr);
    return GetCouponsInfoI.from(coupons);
  }
}
