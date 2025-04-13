package table.eat.now.payment.payment.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.presentation.dto.request.CreatePaymentRequest;
import table.eat.now.payment.payment.presentation.dto.response.CreatePaymentResponse;

@RestController
@RequestMapping("/internal/v1/payments")
@RequiredArgsConstructor
public class PaymentInternalController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<CreatePaymentResponse> createPayment(
      @RequestBody @Valid CreatePaymentRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED).body(
        CreatePaymentResponse.from(paymentService.createPayment(request.toCommand())));
  }
}
