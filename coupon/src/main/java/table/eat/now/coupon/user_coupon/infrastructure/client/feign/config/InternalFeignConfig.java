package table.eat.now.coupon.user_coupon.infrastructure.client.feign.config;

import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import table.eat.now.common.constant.UserInfoConstant;

@RequiredArgsConstructor
@Configuration
public class InternalFeignConfig {

  private final InternalFeignClientErrorDecoder feignClientErrorDecoder;

  @Bean
  public RequestInterceptor requestInterceptor() {

    return requestTemplate -> {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        String userIdStr = request.getHeader(UserInfoConstant.USER_ID_HEADER);
        String userRoleStr = request.getHeader(UserInfoConstant.USER_ROLE_HEADER);
        if (userIdStr != null) {
          requestTemplate.header(UserInfoConstant.USER_ID_HEADER, userIdStr);
        }
        if (userRoleStr != null) {
          requestTemplate.header(UserInfoConstant.USER_ROLE_HEADER, userRoleStr);
        }
      }
    };
  }

  @Bean
  public Retryer feignRetryer() {
    return new Retryer.Default(1000, 2000, 3);
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return feignClientErrorDecoder.decoder(); // 필요에 따라 구현
  }
}
