package table.eat.now.payment.payment.infrastructure.client.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import table.eat.now.common.constant.UserInfoConstant;

@Configuration
@EnableFeignClients("table.eat.now.payment.payment.infrastructure.client.feign")
public class FeignConfig {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      ServletRequestAttributes attrs =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

      if (attrs != null) {
        HttpServletRequest request = attrs.getRequest();
        String userId = request.getHeader(UserInfoConstant.USER_ID_HEADER);
        String role = request.getHeader(UserInfoConstant.USER_ROLE_HEADER);

        if (userId != null) requestTemplate.header(UserInfoConstant.USER_ID_HEADER, userId);
        if (role != null) requestTemplate.header(UserInfoConstant.USER_ROLE_HEADER, role);
      }
    };
  }
}