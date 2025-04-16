package table.eat.now.notification.infrastructure.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 16.
 */
@Configuration
public class WebClientConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }
}
