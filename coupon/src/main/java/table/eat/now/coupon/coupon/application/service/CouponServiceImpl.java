package table.eat.now.coupon.coupon.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.application.dto.event.IssueUserCouponEvent;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.SearchCouponsQuery;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.response.AvailableCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.application.service.strategy.IssueStrategy;
import table.eat.now.coupon.coupon.application.service.strategy.IssueStrategyKey;
import table.eat.now.coupon.coupon.application.service.strategy.IssueStrategyResolver;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
  private final CouponRepository couponRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final IssueStrategyResolver issueStrategyResolver;

  @Override
  public String createCoupon(CreateCouponCommand command) {

    Coupon coupon = command.toEntity();
    couponRepository.save(coupon);
    return coupon.getCouponUuid();
  }

  @Transactional
  @Override
  public void updateCoupon(String couponUuid, UpdateCouponCommand command) {

    Coupon coupon = findByCouponUuid(couponUuid);
    coupon.modify(command.toDomainCommand());
  }

  @Transactional(readOnly = true)
  @Override
  public GetCouponInfo getCoupon(String couponUuid) {

    Coupon coupon = findByCouponUuid(couponUuid);
    return GetCouponInfo.from(coupon);
  }

  @Transactional
  @Override
  public void deleteCoupon(CurrentUserInfoDto userInfo, String couponUuid) {

    Coupon coupon = findByCouponUuid(couponUuid);
    coupon.delete(userInfo.userId());
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<SearchCouponInfo> searchCoupons(Pageable pageable, SearchCouponsQuery query) {

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

  @Transactional(readOnly = true)
  @Override
  public PageResponse<AvailableCouponInfo> getAvailableCoupons(
      Pageable pageable, LocalDateTime time) {

    Page<AvailableCouponInfo> couponInfoPage = couponRepository.getAvailableCoupons(pageable, time)
        .map(AvailableCouponInfo::from);
    return PageResponse.from(couponInfoPage);
  }

  @Override
  public String requestCouponIssue(CurrentUserInfoDto userInfoDto, String couponUuid) {

    Coupon coupon = findByCouponUuid(couponUuid);
    if (!coupon.getPeriod().isValidIssuePeriod()) {
      throw CustomException.from(CouponErrorCode.INVALID_ISSUE_PERIOD);
    }

    IssueStrategy strategy = issueStrategyResolver.resolve(
        IssueStrategyKey.of(
            coupon.getLabel().toString(),
            coupon.hasStockCount(),
            coupon.getAllowDuplicate()));
    strategy.requestIssue(couponUuid, userInfoDto.userId());

    String userCouponUuid = UUID.randomUUID().toString();
    eventPublisher.publishEvent(IssueUserCouponEvent.from(userCouponUuid, userInfoDto, coupon));
    return userCouponUuid;
  }

  private Coupon findByCouponUuid(String couponUuid) {
    return couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid)
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
  }
}
