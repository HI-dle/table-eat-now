package table.eat.now.coupon.coupon.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.coupon.coupon.application.aop.annotation.DistributedLock;
import table.eat.now.coupon.coupon.application.aop.annotation.WithSimpleTransaction;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.application.messaging.EventPublisher;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent;
import table.eat.now.coupon.coupon.application.service.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.service.dto.request.SearchCouponsQuery;
import table.eat.now.coupon.coupon.application.service.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.service.dto.response.IssuableCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.service.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.application.strategy.IssueStrategy;
import table.eat.now.coupon.coupon.application.strategy.IssueStrategyResolver;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
  private final CouponReader couponReader;
  private final CouponStore couponStore;
  private final ApplicationEventPublisher springEventPublisher;
  private final IssueStrategyResolver issueStrategyResolver;
  private final EventPublisher<CouponRequestedIssueEvent> eventPublisher;

  @Override
  public String createCoupon(CreateCouponCommand command) {

    Coupon coupon = command.toEntity();
    couponStore.save(coupon);

    if (isCachable(coupon)) {
      Duration duration = TimeProvider.getDuration(coupon.calcExpireAt(), 60);
      couponStore.insertCouponCache(coupon.getCouponUuid(), coupon, duration);
    }
    return coupon.getCouponUuid();
  }

  @WithSimpleTransaction
  @DistributedLock(key="#couponUuid")
  @Override
  public void updateCoupon(String couponUuid, UpdateCouponCommand command) {
    Coupon coupon;
    try {
      coupon = getValidCouponBy(couponUuid);
      coupon.modify(command.toDomainCommand());
      couponStore.save(coupon);

    } catch (StaleObjectStateException e) {
      throw CustomException.from(CouponErrorCode.IS_OUTDATED_DATA);
    }
    if (isCachable(coupon)) {
      couponStore.updateCouponCache(coupon.getCouponUuid(), coupon);
    }
  }

  @Transactional
  @Override
  public void deleteCoupon(CurrentUserInfoDto userInfo, String couponUuid) {

    Coupon coupon = getValidCouponBy(couponUuid);
    coupon.delete(userInfo.userId());
    couponStore.save(coupon);
  }

  @Transactional(readOnly = true)
  @Override
  public GetCouponInfo getCouponInfo(String couponUuid) {

    Coupon coupon = getValidCouponBy(couponUuid);
    return GetCouponInfo.from(coupon);
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<SearchCouponInfo> searchCoupons(Pageable pageable, SearchCouponsQuery query) {

    Page<SearchCouponInfo> couponInfoPage =
        couponReader.searchCouponByPageableAndCondition(pageable, query.toCriteria())
        .map(SearchCouponInfo::from);
    return PageResponse.from(couponInfoPage);
  }

  @Transactional(readOnly = true)
  @Override
  public GetCouponsInfoI getCouponsInternal(Set<UUID> couponUuids) {

    Set<String> couponUuidsStr = couponUuids.stream().map(UUID::toString).collect(Collectors.toSet());
    List<Coupon> coupons = couponReader.getValidCouponsByUuids(couponUuidsStr);
    return GetCouponsInfoI.from(coupons);
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<IssuableCouponInfo> getAvailableGeneralCoupons( // todo 발급가능으로 변경 필요
      Pageable pageable, LocalDateTime time) {

    Page<IssuableCouponInfo> couponInfoPage = couponReader.getAvailableGeneralCoupons(pageable, time)
        .map(IssuableCouponInfo::from);
    return PageResponse.from(couponInfoPage);
  }

  @Override
  public GetCouponsInfo getDailyIssuablePromotionCoupons() {
    List<Coupon> coupons = couponReader.getIssuableCouponsCacheIn(CouponLabel.PROMOTION);
    return GetCouponsInfo.from(coupons);
  }

  @Override
  public GetCouponsInfoI getDailyIssuablePromotionCouponsInternal() {
    List<Coupon> coupons = couponReader.getIssuableCouponsCacheIn(CouponLabel.PROMOTION);
    return GetCouponsInfoI.from(coupons);
  }

  @Override
  public String requestCouponIssue(CurrentUserInfoDto userInfoDto, String couponUuid) {

    Coupon coupon = getValidCouponBy(couponUuid);
    if (!coupon.getPeriod().isValidIssuePeriod()) {
      throw CustomException.from(CouponErrorCode.INVALID_ISSUE_PERIOD);
    }

    IssueStrategy strategy = issueStrategyResolver.resolve(coupon);
    strategy.requestIssue(couponUuid, userInfoDto.userId());

    String userCouponUuid = UUID.randomUUID().toString();
    //springEventPublisher.publishEvent(CouponRequestedIssueEvent.of(userCouponUuid, userInfoDto, coupon));
    eventPublisher.publish(CouponRequestedIssueEvent.of(userCouponUuid, userInfoDto.userId(), coupon));
    return userCouponUuid;
  }

  private Coupon getValidCouponBy(String couponUuid) {
    return couponReader.findValidCouponByUuid(couponUuid)
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
  }

  private boolean isCachable(Coupon coupon) {
    LocalDate today = LocalDate.now();
    return coupon.isIssuableIn(today) && (coupon.isPromoLabel() || coupon.isHotLabel());
  }
}
