package table.eat.now.common.resolver;

import java.util.Set;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CustomPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {

  private static final Set<Integer> ALLOWED_PAGE_SIZES = Set.of(10, 30, 50);
  private static final int DEFAULT_PAGE_SIZE = 10;

  @Override
  public Pageable resolveArgument(MethodParameter methodParameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    int pageSize = pageable.getPageSize();
    int pageNumber = pageable.getPageNumber() == 0 ? 0 : pageable.getPageNumber() - 1;

    if (!ALLOWED_PAGE_SIZES.contains(pageSize)) {
      return PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE, pageable.getSort());
    }
    return PageRequest.of(pageNumber, pageSize, pageable.getSort());
  }
}