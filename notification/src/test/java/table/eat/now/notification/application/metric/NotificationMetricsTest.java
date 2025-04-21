package table.eat.now.notification.application.metric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 21.
 */
class NotificationMetricsTest {

  private MeterRegistry meterRegistry;
  private NotificationMetrics notificationMetrics;

  @BeforeEach
  void setUp() {
    meterRegistry = mock(MeterRegistry.class);
    notificationMetrics = new NotificationMetrics(meterRegistry);
  }

  @Test
  void testStartSendTimer() {
    try (MockedStatic<Timer> mockedTimer = mockStatic(Timer.class)) {
      // given
      Timer.Sample sampleMock = mock(Timer.Sample.class);
      mockedTimer.when(() -> Timer.start(meterRegistry)).thenReturn(sampleMock);

      // when
      Timer.Sample result = notificationMetrics.startSendTimer();

      // then
      assertThat(result).isEqualTo(sampleMock);
      mockedTimer.verify(() -> Timer.start(meterRegistry), times(1));
    }
  }

  @Test
  void testRecordSendLatency() {
    try (MockedStatic<Timer> mockedTimer = mockStatic(Timer.class)) {
      // given
      Timer.Sample sample = mock(Timer.Sample.class);
      Timer.Builder builder = mock(Timer.Builder.class);
      Timer timer = mock(Timer.class);

      mockedTimer.when(() -> Timer.builder("notification.send.latency")).thenReturn(builder);
      when(builder.description("알림 전송 지연 시간")).thenReturn(builder);
      when(builder.tag("method", "scheduled")).thenReturn(builder);
      when(builder.register(meterRegistry)).thenReturn(timer);

      // when
      notificationMetrics.recordSendLatency(sample, "scheduled");

      // then
      verify(sample).stop(timer);
    }
  }

  @Test
  void testIncrementSendSuccess() {
    Counter counter = mock(Counter.class);
    when(meterRegistry.counter("notification.send.success.count")).thenReturn(counter);

    notificationMetrics.incrementSendSuccess();

    verify(counter).increment();
  }

  @Test
  void testIncrementSendFail() {
    Counter counter = mock(Counter.class);
    when(meterRegistry.counter("notification.send.fail.count")).thenReturn(counter);

    notificationMetrics.incrementSendFail();

    verify(counter).increment();
  }

  @Test
  void testIncrementCreate() {
    Counter counter = mock(Counter.class);
    when(meterRegistry.counter("notification.create.count")).thenReturn(counter);

    notificationMetrics.incrementCreate();

    verify(counter).increment();
  }

  @Test
  void testRecordFetchedScheduledCount() {
    Counter counter = mock(Counter.class);
    when(meterRegistry.counter("notification.scheduled.fetch.count")).thenReturn(counter);

    notificationMetrics.recordFetchedScheduledCount(5);

    verify(counter).increment(5);
  }
}
