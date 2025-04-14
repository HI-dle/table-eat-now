package table.eat.now.payment.payment.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import table.eat.now.common.aop.AuthCheckAspect;
import table.eat.now.common.config.WebConfig;
import table.eat.now.common.exception.GlobalErrorHandler;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("test")
@Import({
    WebConfig.class,
    CustomPageableArgumentResolver.class,
    CurrentUserInfoResolver.class,
    GlobalErrorHandler.class,
    AuthCheckAspect.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@WebMvcTest(PaymentViewController.class)
class PaymentViewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void 체크아웃_페이지_요청_시_checkout_뷰를_반환한다() throws Exception {
    // given
    UUID idempotencyKey = UUID.randomUUID();

    // when & then
    mockMvc.perform(get("/checkout")
            .param("idempotencyKey", idempotencyKey.toString()))
        .andExpect(status().isOk())
        .andExpect(view().name("checkout"));
  }

  @Test
  void 성공_페이지_요청_시_success_뷰를_반환한다() throws Exception {
    // when & then
    mockMvc.perform(get("/success"))
        .andExpect(status().isOk())
        .andExpect(view().name("success"));
  }

  @Test
  void 실패_페이지_요청_시_fail_뷰를_반환한다() throws Exception {
    // when & then
    mockMvc.perform(get("/fail"))
        .andExpect(status().isOk())
        .andExpect(view().name("fail"));
  }
}