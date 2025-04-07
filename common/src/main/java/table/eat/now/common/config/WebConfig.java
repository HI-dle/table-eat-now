package table.eat.now.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final CurrentUserInfoResolver currentUserInfoResolver;
  private final CustomPageableArgumentResolver customPageableArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(currentUserInfoResolver);
    resolvers.add(customPageableArgumentResolver);
  }
}
