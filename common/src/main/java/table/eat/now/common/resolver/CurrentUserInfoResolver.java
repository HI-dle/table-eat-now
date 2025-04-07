package table.eat.now.common.resolver;

import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;

@Component
public class CurrentUserInfoResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {

    return parameter.hasParameterAnnotation(CurrentUserInfo.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {

    String userId = request.getHeader(USER_ID_HEADER);
    String role = request.getHeader(USER_ROLE_HEADER);

    if (userId != null && role != null) {
      return CurrentUserInfoDto.of(Long.valueOf(userId), UserRole.valueOf(role));
    }
    return null;
  }
}

