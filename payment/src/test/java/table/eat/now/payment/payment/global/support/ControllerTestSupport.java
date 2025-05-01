package table.eat.now.payment.payment.global.support;

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
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.presentation.PaymentAdminController;
import table.eat.now.payment.payment.presentation.PaymentApiController;
import table.eat.now.payment.payment.presentation.PaymentInternalController;
import table.eat.now.payment.payment.presentation.PaymentViewController;

@WebMvcTest(controllers = {
    PaymentAdminController.class,
    PaymentApiController.class,
    PaymentInternalController.class,
    PaymentViewController.class
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
  protected PaymentService paymentService;

}
