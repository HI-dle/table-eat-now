package table.eat.now.review.infrastructure.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import table.eat.now.common.constant.UserInfoConstant;

@Configuration
@RequiredArgsConstructor
@EnableFeignClients("table.eat.now.review.infrastructure.client.feign")
public class FeignConfig {

  private final ObjectMapper objectMapper;

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

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder(objectMapper);
  }

  @Bean
  public Retryer retryer() {
    return new Retryer.Default(100, 1000, 3);
  }
}