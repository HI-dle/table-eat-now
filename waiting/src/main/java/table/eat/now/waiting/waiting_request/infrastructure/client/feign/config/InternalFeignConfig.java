package table.eat.now.waiting.waiting_request.infrastructure.client.feign.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import table.eat.now.common.constant.UserInfoConstant;

public class InternalFeignConfig {

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
}
