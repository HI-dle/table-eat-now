package table.eat.now.payment.payment.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PaymentViewController {

  @GetMapping("/checkout")
  public String getPage(@RequestParam String idempotencyKey) {
    return "checkout";
  }

  @GetMapping("/success")
  public String getSuccessPage() {
    return "success";
  }

  @GetMapping("/fail")
  public String getFailPage() {
    return "fail";
  }
}
