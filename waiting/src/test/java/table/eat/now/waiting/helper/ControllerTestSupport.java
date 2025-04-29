package table.eat.now.waiting.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import table.eat.now.common.aop.AuthCheckAspect;
import table.eat.now.common.config.WebConfig;
import table.eat.now.common.exception.GlobalErrorHandler;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;
import table.eat.now.waiting.waiting.application.service.WaitingService;
import table.eat.now.waiting.waiting.presentation.WaitingInternalController;
import table.eat.now.waiting.waiting_request.application.router.UsecaseRouter;
import table.eat.now.waiting.waiting_request.presentation.WaitingRequestAdminController;
import table.eat.now.waiting.waiting_request.presentation.WaitingRequestApiController;
import table.eat.now.waiting.waiting_request.presentation.WaitingRequestInternalController;

@WebMvcTest({
    WaitingInternalController.class,
    WaitingRequestAdminController.class,
    WaitingRequestApiController.class,
    WaitingRequestInternalController.class
})
@Import({
    WebConfig.class,
    CustomPageableArgumentResolver.class,
    CurrentUserInfoResolver.class,
    GlobalErrorHandler.class,
    AuthCheckAspect.class
})
@EnableAspectJAutoProxy
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ControllerTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockitoBean
  protected WaitingService waitingService;

  @MockitoBean
  protected UsecaseRouter router;
}
