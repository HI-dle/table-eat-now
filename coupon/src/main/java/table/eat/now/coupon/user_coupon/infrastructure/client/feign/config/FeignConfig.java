package table.eat.now.coupon.user_coupon.infrastructure.client.feign.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("table.eat.now.coupon.user_coupon.infrastructure.client.feign")
public class FeignConfig {

}
