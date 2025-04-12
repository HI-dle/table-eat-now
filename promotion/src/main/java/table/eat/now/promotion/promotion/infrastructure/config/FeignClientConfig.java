package table.eat.now.promotion.promotion.infrastructure.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import table.eat.now.common.constant.UserInfoConstant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@Configuration
@RequiredArgsConstructor
@EnableFeignClients("table.eat.now.promotion.promotion.infrastructure.client.feign")
public class FeignClientConfig {

  @Bean
  public RequestInterceptor requestInterceptor() {

    return requestTemplate -> {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = attributes.getRequest();

      String userIdStr = request.getHeader(UserInfoConstant.USER_ID_HEADER);
      String userRoleStr = request.getHeader(UserInfoConstant.USER_ROLE_HEADER);
      if (userIdStr != null) {
        requestTemplate.header(UserInfoConstant.USER_ID_HEADER, userIdStr);
      }
      if (userRoleStr != null) {
        requestTemplate.header(UserInfoConstant.USER_ROLE_HEADER, userRoleStr);
      }
    };
  }
}
