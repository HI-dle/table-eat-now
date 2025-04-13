package table.eat.now.coupon.user_coupon.application.listener.config;

import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RequiredArgsConstructor
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  private final AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler;

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(30);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("AsyncThread-");
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return asyncUncaughtExceptionHandler;
  }
}
