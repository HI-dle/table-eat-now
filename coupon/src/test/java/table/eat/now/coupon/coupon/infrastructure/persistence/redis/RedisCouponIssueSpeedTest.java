package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.COUPON_COUNT;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.COUPON_USER_SET;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.strategy.IssueLimitedNonDupHotStrategy;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.command.CouponIssuance;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.coupon.infrastructure.exception.CouponInfraErrorCode;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponLuaResultConstant.IssueResult;
import table.eat.now.coupon.helper.IntegrationTestSupport;

@Slf4j
@Disabled("수동으로 실행할 때만 @Disabled 주석을 제거하세요. CI/CD 파이프라인에서 실행되지 않도록 합니다.")
public class RedisCouponIssueSpeedTest extends IntegrationTestSupport {

  @Autowired
  IssueLimitedNonDupHotStrategy issueLimitedNonDupHotStrategy;
  @Autowired
  private CouponReader couponReader;
  @Autowired
  private CouponStore couponStore;
  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  private Coupon coupon;
  private int requestCount;
  private String sha;
  private RedisScript<Long> simpleCouponIssueRedisScript;

  @BeforeEach
  void setUp() throws IOException {
    requestCount = 100000;
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", "PROMOTION",requestCount, false, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration duration = TimeProvider.getDuration(coupon.getPeriod().getIssueEndAt(), 60);
    Duration cacheDuration = TimeProvider.getDuration(coupon.calcExpireAt(), 60);
    couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
    couponStore.insertCouponCache(coupon.getCouponUuid(), coupon, cacheDuration);

    sha = this.saveLuaAngGetSha();
    simpleCouponIssueRedisScript = loadScript("redis/coupon/limited_nondup_test.lua", Long.class);
  }

  @DisplayName("루아 스크립트 활용: 속도 성능 비교용")
  @Test
  void successWithConcurrentRequest() {
    // given
    List<CouponIssuance> commands = IntStream.range(0, requestCount)
        .mapToObj(i -> CouponIssuance.builder()
            .couponUuid(coupon.getCouponUuid())
            .userId((long) i)
            .couponProfile(CouponProfile.parse(coupon))
            .timestamp(Instant.now().toEpochMilli() + i * 100L)
            .build())
        .toList();

    int threadCount = 20;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    AtomicLong totalTime = new AtomicLong(0);
    // when
    long totalStart = System.nanoTime();
    List<CompletableFuture<Void>> futures = IntStream.range(0, requestCount)
        .mapToObj(i -> CompletableFuture.runAsync(() -> {
          long start = System.nanoTime();
          requestIssueByLuaShaForTest(commands.get(i));
          long duration = System.nanoTime() - start;
          totalTime.addAndGet(duration);
        }, executorService))
        .toList();

    futures.forEach(CompletableFuture::join);

    long totalDuration = (System.nanoTime() - totalStart) / 1_000_000;
    double averageMillis = totalTime.get() / (double) requestCount / 1_000_000.0;
    log.info("평균 수행 시간(ms): {}", averageMillis);
    log.info("전체 수행 시간(ms): {}", totalDuration);

  }

  @DisplayName("레디스 템플릿 활용: 속도 성능 비교용")
  @Test
  void checkSpeed() {
    // given
    int threadCount = 20;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    AtomicLong totalTime = new AtomicLong(0);
    // when
    long totalStart = System.nanoTime();
    List<CompletableFuture<Void>> futures = IntStream.range(0, requestCount)
        .mapToObj(i -> CompletableFuture.runAsync(() -> {
          long start = System.nanoTime();
          issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), (long) i);
          long duration = System.nanoTime() - start;
          totalTime.addAndGet(duration);
        }, executorService))
        .toList();

    futures.forEach(CompletableFuture::join);

    long totalDuration = (System.nanoTime() - totalStart) / 1_000_000;
    double averageMillis = totalTime.get() / (double) requestCount / 1_000_000.0;
    log.info("평균 수행 시간(ms): {}", averageMillis);
    log.info("전체 수행 시간(ms): {}", totalDuration);
  }

  @DisplayName("루아 스크립트(unload) 활용: 속도 성능 비교용")
  @Test
  void checkSpeed2() {
    // given
    List<CouponIssuance> commands = IntStream.range(0, requestCount)
        .mapToObj(i -> CouponIssuance.builder()
            .couponUuid(coupon.getCouponUuid())
            .userId((long) i)
            .couponProfile(CouponProfile.parse(coupon))
            .timestamp(Instant.now().toEpochMilli() + i * 100L)
            .build())
        .toList();

    int threadCount = 20;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    AtomicLong totalTime = new AtomicLong(0);
    // when
    long totalStart = System.nanoTime();
    List<CompletableFuture<Void>> futures = IntStream.range(0, requestCount)
        .mapToObj(i -> CompletableFuture.runAsync(() -> {
          long start = System.nanoTime();
          requestIssueByLuaForTest(commands.get(i));
          long duration = System.nanoTime() - start;
          totalTime.addAndGet(duration);
        }, executorService))
        .toList();

    futures.forEach(CompletableFuture::join);

    long totalDuration = (System.nanoTime() - totalStart) / 1_000_000;
    double averageMillis = totalTime.get() / (double) requestCount / 1_000_000.0;
    log.info("평균 수행 시간(ms): {}", averageMillis);
    log.info("전체 수행 시간(ms): {}", totalDuration);
  }

  private void requestIssueByLuaShaForTest(CouponIssuance command) {

    String userSetKey = COUPON_USER_SET + command.couponUuid();;
    String couponCountKey = COUPON_COUNT + command.couponUuid();

    Long result;
    try {
      result = stringRedisTemplate.execute((RedisCallback<Long>) connection ->
          connection.evalSha(sha.getBytes(StandardCharsets.UTF_8), ReturnType.INTEGER,
              2,
              userSetKey.getBytes(StandardCharsets.UTF_8),
              couponCountKey.getBytes(StandardCharsets.UTF_8),
              command.userId().toString().getBytes(StandardCharsets.UTF_8))
      );
    } catch (Exception e) {
      throw CustomException.from(CouponInfraErrorCode.FAILED_LUA_SCRIPT);
    }

    if (result != 1) {
      throw CustomException.from(IssueResult.parseToErrorCode(result));
    }
  }

  private void requestIssueByLuaForTest(CouponIssuance command) {

    String userSetKey = COUPON_USER_SET + command.couponUuid();
    String couponCountKey = COUPON_COUNT + command.couponUuid();

    List<String> keys = List.of(userSetKey, couponCountKey);
    List<String> args = List.of(command.userId().toString());

    Long result;
    try {
      result = stringRedisTemplate.execute(
          simpleCouponIssueRedisScript,
          keys,
          args.toArray(new String[0]));

    } catch (Exception e) {
      throw CustomException.from(CouponInfraErrorCode.FAILED_LUA_SCRIPT);
    }

    if (result != 1) {
      throw CustomException.from(IssueResult.parseToErrorCode(result));
    }
  }

  private String saveLuaAngGetSha() throws IOException {

    String path = "redis/coupon/limited_nondup_test.lua";

    String luaScript = new String(
        Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:" + path).toURI())),
        StandardCharsets.UTF_8
    );

    return redisTemplate.execute((RedisCallback<String>) connection ->
        connection.scriptingCommands().scriptLoad(luaScript.getBytes(StandardCharsets.UTF_8))
    );
  }

  private <T> RedisScript<T> loadScript(String path, Class<T> resultType) {
    DefaultRedisScript<T> script = new DefaultRedisScript<>();
    script.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
    script.setResultType(resultType);
    return script;
  }
}
