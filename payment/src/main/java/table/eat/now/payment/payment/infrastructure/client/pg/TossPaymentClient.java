package table.eat.now.payment.payment.infrastructure.client.pg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import table.eat.now.common.exception.CustomException;
import table.eat.now.payment.payment.infrastructure.client.dto.request.CancelTossPayRequest;
import table.eat.now.payment.payment.infrastructure.client.dto.request.ConfirmTossPayRequest;
import table.eat.now.payment.payment.infrastructure.client.dto.response.CancelTossPayResponse;
import table.eat.now.payment.payment.infrastructure.client.dto.response.ConfirmTossPayResponse;

@Slf4j
@Component
public class TossPaymentClient {

  private static final String BASIC_AUTH_PREFIX = "Basic ";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
  private static final String EMPTY_SECRET_KEY_SUFFIX = ":";
  private static final String DEFAULT_ERROR_MESSAGE = "결제 처리 중 오류가 발생했습니다.";

  private final ObjectMapper objectMapper;
  private final RestClient restClient;

  public TossPaymentClient(
      @Value("${payment.toss.secret-key}") String secretKey,
      @Value("${payment.toss.api-url}") String tossApiUrl,
      ObjectMapper objectMapper) {

    this.objectMapper = objectMapper;
    this.restClient = RestClient.builder()
        .baseUrl(tossApiUrl)
        .defaultHeader(AUTHORIZATION_HEADER,
            BASIC_AUTH_PREFIX + Base64
                .getEncoder()
                .encodeToString((secretKey + EMPTY_SECRET_KEY_SUFFIX).getBytes()))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public ConfirmTossPayResponse confirmPayment(
      ConfirmTossPayRequest request, String idempotencyKey) {

    return withCustomException(()->
        restClient.post()
            .uri("/payments/confirm")
            .header(IDEMPOTENCY_HEADER, idempotencyKey)
            .body(request)
            .retrieve()
            .body(ConfirmTossPayResponse.class));
  }

  public CancelTossPayResponse cancelPayment(
      CancelTossPayRequest request, String idempotencyKey, String paymentKey) {

    return withCustomException(()->
        restClient.post()
        .uri("/payments/{paymentKey}/cancel", paymentKey)
        .header(IDEMPOTENCY_HEADER, idempotencyKey)
        .body(request)
        .retrieve()
        .body(CancelTossPayResponse.class));
  }

  private <T> T withCustomException(Supplier<T> supplier) {
    try {
      return supplier.get();
    } catch (HttpClientErrorException e) {
      HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
      throw CustomException.of(status, extractErrorMessage(e));
    }
  }

  private String extractErrorMessage(HttpClientErrorException e) {
    try {
      JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());
      JsonNode errorNode = rootNode.path("error");
      return errorNode.path("message").asText(DEFAULT_ERROR_MESSAGE);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return DEFAULT_ERROR_MESSAGE;
    }
  }
}