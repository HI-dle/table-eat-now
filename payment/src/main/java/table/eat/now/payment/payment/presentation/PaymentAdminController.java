package table.eat.now.payment.payment.presentation;

import static table.eat.now.common.resolver.dto.UserRole.MASTER;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.presentation.dto.request.SearchMasterPaymentsRequest;
import table.eat.now.payment.payment.presentation.dto.response.PaginatedResponse;
import table.eat.now.payment.payment.presentation.dto.response.SearchPaymentsResponse;

@RestController
@RequestMapping("/admin/v1/payments")
@RequiredArgsConstructor
public class PaymentAdminController {

  private final PaymentService paymentService;

  @AuthCheck(roles = MASTER)
  @GetMapping
  public ResponseEntity<PaginatedResponse<SearchPaymentsResponse>> getMasterPayments(
      @Valid SearchMasterPaymentsRequest request, Pageable pageable) {

    return ResponseEntity.ok(PaginatedResponse.from(
            paymentService.searchMasterPayments(request.toQuery(pageable)))
        .map(SearchPaymentsResponse::from));
  }
}
