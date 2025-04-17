package table.eat.now.payment.payment.presentation;

import static table.eat.now.common.resolver.dto.UserRole.CUSTOMER;
import static table.eat.now.common.resolver.dto.UserRole.MASTER;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.presentation.dto.request.ConfirmPaymentRequest;
import table.eat.now.payment.payment.presentation.dto.request.SearchMyPaymentsRequest;
import table.eat.now.payment.payment.presentation.dto.response.ConfirmPaymentResponse;
import table.eat.now.payment.payment.presentation.dto.response.GetCheckoutDetailResponse;
import table.eat.now.payment.payment.presentation.dto.response.GetPaymentResponse;
import table.eat.now.payment.payment.presentation.dto.response.PaginatedResponse;
import table.eat.now.payment.payment.presentation.dto.response.SearchMyPaymentsResponse;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentApiController {

  private final PaymentService paymentService;

  @GetMapping("/checkout-info")
  public ResponseEntity<GetCheckoutDetailResponse> getCheckoutDetail(
      @RequestParam UUID idempotencyKey) {

    return ResponseEntity.ok(GetCheckoutDetailResponse.from(
        paymentService.getCheckoutDetail(idempotencyKey.toString())));
  }

  @PatchMapping("/confirm")
  public ResponseEntity<ConfirmPaymentResponse> confirmPayment(
      @RequestParam UUID reservationUuid,
      @RequestBody @Valid ConfirmPaymentRequest request,
      @CurrentUserInfo CurrentUserInfoDto userInfo) {

    return ResponseEntity.ok(ConfirmPaymentResponse.from(
        paymentService.confirmPayment(request.toCommand(reservationUuid.toString()), userInfo)));
  }

  @AuthCheck(roles = {CUSTOMER, MASTER})
  @GetMapping("/{paymentUuid}")
  public ResponseEntity<GetPaymentResponse> getPayment(
      @PathVariable UUID paymentUuid, @CurrentUserInfo CurrentUserInfoDto userInfo) {

    return ResponseEntity.ok(
        GetPaymentResponse.from(paymentService.getPayment(paymentUuid.toString(), userInfo)));
  }

  @GetMapping("/my")
  public ResponseEntity<PaginatedResponse<SearchMyPaymentsResponse>> getMyPayments(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @Valid SearchMyPaymentsRequest request, Pageable pageable) {

    return ResponseEntity.ok(PaginatedResponse.from(
            paymentService.searchMyPayments(request.toQuery(pageable, userInfo)))
        .map(SearchMyPaymentsResponse::from));
  }
}

