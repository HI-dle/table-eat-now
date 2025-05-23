package table.eat.now.payment.payment.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import table.eat.now.payment.payment.global.support.ControllerTestSupport;

class PaymentViewControllerTest extends ControllerTestSupport {

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