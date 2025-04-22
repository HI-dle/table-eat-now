/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import table.eat.now.common.aop.AuthCheckAspect;
import table.eat.now.common.config.WebConfig;
import table.eat.now.common.exception.GlobalErrorHandler;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;
import table.eat.now.reservation.reservation.application.service.ReservationService;
import table.eat.now.reservation.reservation.presentation.ReservationAdminController;
import table.eat.now.reservation.reservation.presentation.ReservationApiController;
import table.eat.now.reservation.reservation.presentation.ReservationInternalController;

@WebMvcTest(controllers = {
    ReservationApiController.class,
    ReservationAdminController.class,
    ReservationInternalController.class
})
@Import({
    WebConfig.class,
    CustomPageableArgumentResolver.class,
    CurrentUserInfoResolver.class,
    GlobalErrorHandler.class,
    AuthCheckAspect.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public abstract class ControllerTestSupport {
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockitoBean
  protected ReservationService reservationService;
}
