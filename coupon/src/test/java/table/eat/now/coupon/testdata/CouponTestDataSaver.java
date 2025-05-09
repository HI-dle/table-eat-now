package table.eat.now.coupon.testdata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.coupon.infrastructure.persistence.jpa.JpaCouponRepository;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.RedisCouponCacheManager;
import table.eat.now.coupon.helper.IntegrationDataCreationSupport;

@Slf4j
@DisplayName("편의로 만든 클래스 입니다. Disabled 주석 처리하고 돌려서 데이터 세팅하기!")
@Disabled("수동으로 실행할 때만 @Disabled 주석을 제거하세요. CI/CD 파이프라인에서 실행되지 않도록 합니다.")
public class CouponTestDataSaver extends IntegrationDataCreationSupport {

  @Autowired
  private JpaCouponRepository jpaCouponRepository;
  @Autowired
  private CouponStore couponStore;
  @Autowired
  private RedisCouponCacheManager redisCouponCacheManager;

  @DisplayName("발급 가능 쿠폰 생성")
  @Nested
  class CreateIssuableCoupon {

    @DisplayName("쿠폰 10개 생성")
    @Test
    void makeIssuableCoupon() {

      int size = 10;
      List<Coupon> coupons = IntStream.range(0, size)
          .mapToObj(i -> CouponFixture.createHotCoupon(
              i, "FIXED_DISCOUNT", 1000000000, false,
              10000, null, null)
          )
          .toList();

      coupons.forEach(coupon -> {
        ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now());
        ReflectionTestUtils.setField(coupon.getPeriod(), "issueEndAt", LocalDateTime.now().plusDays(14));
      });

      jpaCouponRepository.saveAll(coupons);

      List<CouponCachingAndIndexing> commands = coupons.stream()
          .map(coupon -> {

            Duration duration = TimeProvider.getDuration(coupon.getPeriod().getIssueEndAt(), 60);
            redisCouponCacheManager.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
            redisCouponCacheManager.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
            return CouponCachingAndIndexing.from(coupon);
          })
          .toList();

      redisCouponCacheManager.pipelinedPutCouponsCache(commands);
    }
  }


  @DisplayName("실수했을 때 쓰려고 만들어뒀어요.")
  @Test
  void mistake() {

    List<Coupon> all = jpaCouponRepository.findAll();

    all.forEach(coupon -> {
      Duration duration = TimeProvider.getDuration(coupon.getPeriod().getIssueEndAt(), 60);
      couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
      couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
    });
  }

  @DisplayName("루아스크립트 레디스 저장용")
  @Test
  void saveLua() throws IOException {

    String path = "redis/coupon/limited_nondup.lua";
    ClassPathResource resource = new ClassPathResource(path);
    String luaScript = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

    String sha = redisTemplate.execute((RedisCallback<String>) connection ->
      connection.scriptingCommands().scriptLoad(luaScript.getBytes(StandardCharsets.UTF_8))
    );
    log.info("스크립트 sha: {}", sha);
  }
}
