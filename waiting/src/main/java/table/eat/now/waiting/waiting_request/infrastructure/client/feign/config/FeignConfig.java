package table.eat.now.waiting.waiting_request.infrastructure.client.feign.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("table.eat.now.waiting.waiting_request.infrastructure.client.feign")
public class FeignConfig {

}
