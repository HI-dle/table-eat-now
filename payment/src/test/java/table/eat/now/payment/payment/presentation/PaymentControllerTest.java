package table.eat.now.payment.payment.presentation;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void 체크아웃_정보를_정상적으로_반환한다() throws Exception {
    // given
    UUID idempotencyKey = UUID.randomUUID();

    // when & then
    mockMvc.perform(get("/api/v1/payments/{idempotencyKey}/checkout-info", idempotencyKey)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void 유효하지_않은_idempotencyKey_입력시_Bad_Request를_반환한다() throws Exception {
    // given
    String invalidUUID = "invalid-uuid";

    // when & then
    mockMvc.perform(get("/api/v1/payments/{idempotencyKey}/checkout-info", invalidUUID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 결제_확인_요청시_정상적으로_처리된다() throws Exception {
    // given
    UUID idempotencyKey = UUID.randomUUID();

    // when & then
    mockMvc.perform(patch("/api/v1/payments/{idempotencyKey}/confirm", idempotencyKey)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}