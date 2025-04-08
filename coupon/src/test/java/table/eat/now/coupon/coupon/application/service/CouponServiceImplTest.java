package table.eat.now.coupon.coupon.application.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
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
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;
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

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    databaseCleanUp.afterPropertiesSet();
    databaseCleanUp.execute();
  }

  @DisplayName("쿠폰 생성 검증 - 생성 성공")
  @Test
  void createCoupon() {
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

    UUID couponUuid = couponService.createCoupon(command);
    Coupon coupon = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid)
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    Assertions.assertThat(coupon.getCount()).isEqualTo(command.count());
  }
}