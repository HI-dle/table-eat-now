package table.eat.now.coupon.coupon.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;
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

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", false,
        1000, null, null);
    couponRepository.save(coupon);
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
    couponService.updateCoupon(UUID.fromString(coupon.getCouponUuid()), command);

    // then
    Coupon updated = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(coupon.getCouponUuid())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    assertThat(updated.getCount()).isEqualTo(command.count());
  }

  @DisplayName("쿠폰 조회 검증 - 조회 성공")
  @Test
  void getCoupon() {
    // given
    // when
    GetCouponInfo couponInfo = couponService.getCoupon(UUID.fromString(coupon.getCouponUuid()));

    // then
    assertThat(couponInfo.name()).isEqualTo(coupon.getName());
    assertThat(couponInfo.count()).isEqualTo(coupon.getCount());
    assertThat(couponInfo.startAt()).isEqualTo(coupon.getPeriod().getStartAt());
    assertThat(couponInfo.endAt()).isEqualTo(coupon.getPeriod().getEndAt());
  }

  @DisplayName("쿠폰 삭제 검증 - 삭제 성공")
  @Test
  void deleteCoupon() {
    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(1L, UserRole.MASTER);

    // when
    couponService.deleteCoupon(userInfo, UUID.fromString(coupon.getCouponUuid()));

    // then
    assertThatThrownBy(() ->
      couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(coupon.getCouponUuid())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID))
    ).isInstanceOf(CustomException.class);
  }
}