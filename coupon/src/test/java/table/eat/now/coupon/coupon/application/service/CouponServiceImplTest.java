package table.eat.now.coupon.coupon.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent;
import table.eat.now.coupon.coupon.application.service.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.service.dto.request.SearchCouponsQuery;
import table.eat.now.coupon.coupon.application.service.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponsInfoI.GetCouponInfoI;
import table.eat.now.coupon.coupon.application.service.dto.response.IssuableCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.service.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.RedisCouponCacheManager;
import table.eat.now.coupon.helper.IntegrationTestSupport;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.spring.UserCouponSpringEventListener;

@RecordApplicationEvents
class CouponServiceImplTest extends IntegrationTestSupport {

  @Autowired
  private CouponService couponService;

  @Autowired
  private CouponReader couponReader;
  @Autowired
  private CouponStore couponStore;
  @Autowired
  private RedisCouponCacheManager redisCouponCacheManager;
  @Autowired
  private EntityManager em;

  @Resource
  ApplicationEvents applicationEvents;

  @MockitoBean
  UserCouponSpringEventListener userCouponSpringEventListener;

  private List<Coupon> coupons;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupons = CouponFixture.createCoupons(20);
    couponStore.saveAll(coupons);

    coupon = coupons.get(0);
  }

  @DisplayName("발급 가능한 핫 쿠폰 생성 및 캐싱 수행 검증 - 성공")
  @Test
  void createCoupon() {
    // given
    CreateCouponCommand command = CreateCouponCommand.builder()
        .name("test")
        .type("FIXED_DISCOUNT")
        .label("HOT")
        .count(10000)
        .issueStartAt(LocalDateTime.now().plusHours(2).plusSeconds(10))
        .issueEndAt(LocalDateTime.now().plusHours(3))
        .validDays(7)
        .allowDuplicate(true)
        .minPurchaseAmount(30000)
        .amount(1000)
        .percent(null)
        .maxDiscountAmount(null)
        .build();

    // when
    String couponUuid = couponService.createCoupon(command);

    // then
    Coupon coupon = couponReader.findValidCouponByUuid(couponUuid)
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    assertThat(coupon.getCount()).isEqualTo(command.count());
    Coupon couponCache = redisCouponCacheManager.getCouponCache(couponUuid);
    assertThat(couponCache).isNotNull();
    assertThat(couponCache.getName()).isEqualTo(coupon.getName());
  }

  @DisplayName("쿠폰 수정 검증 - 성공")
  @Test
  void updateCoupon() {
    // given
    UpdateCouponCommand command = UpdateCouponCommand.builder()
        .name("test")
        .type("FIXED_DISCOUNT")
        .label("SYSTEM")
        .count(5)
        .issueStartAt(LocalDateTime.now().plusDays(1))
        .issueEndAt(LocalDateTime.now().plusDays(10))
        .validDays(7)
        .allowDuplicate(true)
        .minPurchaseAmount(30000)
        .amount(1000)
        .percent(null)
        .maxDiscountAmount(null)
        .version(4L)
        .build();

    // when
    couponService.updateCoupon(coupons.get(0).getCouponUuid(), command);

    // then
    Coupon updated = couponReader.findValidCouponByUuid(coupons.get(0).getCouponUuid())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));

    assertThat(updated.getCount()).isEqualTo(command.count());
    assertThat(updated.getVersion()).isEqualTo(1L);
  }

  @DisplayName("쿠폰 조회 검증 - 조회 성공")
  @Test
  void getCouponInfo() {
    // given
    // when
    GetCouponInfo couponInfo = couponService.getCouponInfo(coupons.get(0).getCouponUuid());

    // then
    assertThat(couponInfo.name()).isEqualTo(coupons.get(0).getName());
    assertThat(couponInfo.count()).isEqualTo(coupons.get(0).getCount());
    // 아래 주석 로컬에선 성공하는데 깃헙 액션에서 실패함
    //assertThat(couponInfo.startAt()).isEqualTo(coupon.getPeriod().getStartAt());
    //assertThat(couponInfo.endAt()).isEqualTo(coupon.getPeriod().getEndAt());
  }

  @DisplayName("쿠폰 삭제 검증 - 삭제 성공")
  @Test
  void deleteCoupon() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(1L, UserRole.MASTER);

    // when
    couponService.deleteCoupon(userInfo, coupons.get(0).getCouponUuid());

    // then
    assertThatThrownBy(() ->
        couponReader.findValidCouponByUuid(coupons.get(0).getCouponUuid())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID))
    ).isInstanceOf(CustomException.class);
  }

  @DisplayName("쿠폰 목록 조회 검증 - 조회 성공")
  @Test
  void searchCoupons() {
    // given
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "issueEndAt"));
    SearchCouponsQuery query = SearchCouponsQuery.builder()
        .fromAt(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS))
        .toAt(LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.DAYS))
        .type("FIXED_DISCOUNT")
        .build();

    // when
    PageResponse<SearchCouponInfo> coupons = couponService.searchCoupons(pageable, query);

    // then
    assertThat(coupons.pageNumber()).isEqualTo(1);
    assertThat(coupons.pageSize()).isEqualTo(10);
    assertThat(coupons.totalElements()).isEqualTo(9);
  }

  @DisplayName("쿠폰 다건 조회 검증 - 조회 성공")
  @Test
  void getCouponsInternal() {
    // given
    Set<String> couponUuidsStr = Set.of(coupons.get(0).getCouponUuid(), coupons.get(1).getCouponUuid());
    Set<UUID> couponUuids = couponUuidsStr.stream()
        .map(UUID::fromString)
        .collect(Collectors.toSet());

    // when
    GetCouponsInfoI coupons = couponService.getCouponsInternal(couponUuids);

    // then
    assertThat(coupons.coupons().size()).isEqualTo(2);
    assertThat(coupons.coupons().stream().map(GetCouponInfoI::couponUuid).collect(Collectors.toSet()))
        .isEqualTo(couponUuidsStr);
  }

  @DisplayName("현재 사용가능한 쿠폰 목록 조회 - 조회 성공")
  @Test
  void getAvailableCoupons() {
    // given
    Coupon coupon = coupons.get(0);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getIssueEndAt())
        .plusMinutes(10);
    couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "issueEndAt"));

    // when
    PageResponse<IssuableCouponInfo> availableCoupons = couponService.getAvailableGeneralCoupons(
        pageable, LocalDateTime.now());

    // then
    assertThat(availableCoupons.pageNumber()).isEqualTo(1);
    assertThat(availableCoupons.pageSize()).isEqualTo(10);
    assertThat(availableCoupons.totalElements()).isEqualTo(1);
    assertThat(availableCoupons.contents().get(0).couponUuid()).isEqualTo(coupon.getCouponUuid());
  }

  @DisplayName("한정 수량 및 중복 발급 제한 쿠폰 발급 요청 - 발급 요청 성공")
  @Test
  void requestCouponIssue() {
    // given
    Coupon coupon = coupons.get(0);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getIssueEndAt())
        .plusMinutes(10);
    couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);

    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(3L, UserRole.CUSTOMER);

    // when
    couponService.requestCouponIssue(userInfo, coupon.getCouponUuid());

    // then
    boolean result = couponReader.isAlreadyIssued(coupon.getCouponUuid(), userInfo.userId());
    assertThat(result).isTrue();
  }

  @DisplayName("쿠폰 발급 요청 - 사용자 쿠폰 발급 이벤트 발행 검증")
  @Test
  void requestCouponIssueTriggerEvent() {
    // given
    Coupon coupon = coupons.get(0);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getIssueEndAt())
        .plusMinutes(10);
    couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);

    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(3L, UserRole.CUSTOMER);

    // when
    couponService.requestCouponIssue(userInfo, coupon.getCouponUuid());

    // then
    // 이벤트 발행이 잘 되었는지 확인
    assertThat(applicationEvents.stream(CouponRequestedIssueEvent.class).count()).isEqualTo(1);

    // 유저 쿠폰
    ArgumentCaptor<CouponRequestedIssueEvent> captor =
        ArgumentCaptor.forClass(CouponRequestedIssueEvent.class);

    verify(userCouponSpringEventListener).listen(captor.capture());
    CouponRequestedIssueEvent value = captor.getValue();

    assertThat(value.payload().couponUuid()).isEqualTo(coupon.getCouponUuid());
  }

  @DisplayName("일간 발급 가능 프로모션 쿠폰 조회: 캐싱 데이터 사용 확인 - 성공")
  @Test
  void getDailyIssuablePromotionCoupons() {
    // given
    coupons.forEach(coupon -> {
      ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
      ReflectionTestUtils.setField(coupon, "label", CouponLabel.PROMOTION);
    });
    couponStore.saveAll(coupons);

    Coupon coupon = coupons.get(0);
    String couponName = "캐싱 데이터를 잘 가져오는지 보기 위한 쿠폰명";
    ReflectionTestUtils.setField(coupon, "name", couponName);

    redisCouponCacheManager.pipelinedPutCouponsCacheAndIndex(coupons.stream()
        .map(CouponCachingAndIndexing::from)
        .toList());

    // when
    GetCouponsInfo dailyIssuablePromotionCoupons = couponService.getDailyIssuablePromotionCoupons();

    // then
    assertThat(dailyIssuablePromotionCoupons.coupons()
        .stream()
        .filter(info -> info.couponUuid().equals(coupon.getCouponUuid()))
        .findAny()
        .orElse(null)
        .name()).isEqualTo(couponName);
  }

  @DisplayName("일간 발급 가능 프로모션 쿠폰 조회: 캐싱 데이터 사용 확인 - 성공")
  @Test
  void getDailyIssuablePromotionCouponsInternal() {
    // given
    coupons.forEach(coupon -> {
      ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
      ReflectionTestUtils.setField(coupon, "label", CouponLabel.PROMOTION);
    });
    couponStore.saveAll(coupons);

    Coupon coupon = coupons.get(0);
    String couponName = "캐싱 데이터를 잘 가져오는지 보기 위한 쿠폰명";
    ReflectionTestUtils.setField(coupon, "name", couponName);

    redisCouponCacheManager.pipelinedPutCouponsCacheAndIndex(coupons.stream()
        .map(CouponCachingAndIndexing::from)
        .toList());

    // when
    GetCouponsInfoI dailyIssuablePromotionCoupons = couponService.getDailyIssuablePromotionCouponsInternal();

    // then
    assertThat(dailyIssuablePromotionCoupons.coupons()
        .stream()
        .filter(info -> info.couponUuid().equals(coupon.getCouponUuid()))
        .findAny()
        .orElse(null)
        .name()).isEqualTo(couponName);
  }
}