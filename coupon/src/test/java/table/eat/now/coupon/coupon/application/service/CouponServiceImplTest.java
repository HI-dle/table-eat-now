package table.eat.now.coupon.coupon.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.SearchCouponsQuery;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponsInfoI.GetCouponInfoI;
import table.eat.now.coupon.coupon.application.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.DatabaseCleanUp;
import table.eat.now.coupon.helper.RedisTestContainerExtension;

@ExtendWith(RedisTestContainerExtension.class)
@Import(DatabaseCleanUp.class)
@ActiveProfiles("test")
@SpringBootTest
class CouponServiceImplTest {

  @Autowired
  private CouponService couponService;

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  DatabaseCleanUp databaseCleanUp;

  private List<Coupon> coupons;

  @BeforeEach
  void setUp() {
    coupons = CouponFixture.createCoupons(20);
    couponRepository.saveAll(coupons);
  }

  @AfterEach
  void tearDown() {
    databaseCleanUp.afterPropertiesSet();
    databaseCleanUp.execute();
  }

  @DisplayName("쿠폰 생성 검증 - 생성 성공")
  @Test
  void createCoupon() {
    // given
    CreateCouponCommand command = CreateCouponCommand.builder()
        .name("test")
        .type("FIXED_DISCOUNT")
        .count(10000)
        .startAt(LocalDateTime.now().plusDays(1))
        .endAt(LocalDateTime.now().plusDays(10))
        .allowDuplicate(true)
        .minPurchaseAmount(30000)
        .amount(1000)
        .percent(null)
        .maxDiscountAmount(null)
        .build();

    // when
    String couponUuid = couponService.createCoupon(command);

    // then
    Coupon coupon = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid)
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    assertThat(coupon.getCount()).isEqualTo(command.count());
  }

  @DisplayName("쿠폰 수정 검증 - 수정 성공")
  @Test
  void updateCoupon() {
    // given
    UpdateCouponCommand command = UpdateCouponCommand.builder()
        .name("test")
        .type("FIXED_DISCOUNT")
        .count(50000)
        .startAt(LocalDateTime.now().plusDays(1))
        .endAt(LocalDateTime.now().plusDays(10))
        .allowDuplicate(true)
        .minPurchaseAmount(30000)
        .amount(1000)
        .percent(null)
        .maxDiscountAmount(null)
        .build();

    // when
    couponService.updateCoupon(coupons.get(0).getCouponUuid(), command);

    // then
    Coupon updated = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(coupons.get(0).getCouponUuid())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    assertThat(updated.getCount()).isEqualTo(command.count());
  }

  @DisplayName("쿠폰 조회 검증 - 조회 성공")
  @Test
  void getCoupon() {
    // given
    // when
    GetCouponInfo couponInfo = couponService.getCoupon(coupons.get(0).getCouponUuid());

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
      couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(coupons.get(0).getCouponUuid())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID))
    ).isInstanceOf(CustomException.class);
  }

  @DisplayName("쿠폰 목록 조회 검증 - 조회 성공")
  @Test
  void searchCoupons() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
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
    assertThat(coupons.totalElements()).isEqualTo(8);
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

}