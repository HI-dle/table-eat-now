package table.eat.now.review.infrastructure.client.config;

import static org.apache.commons.lang.StringUtils.EMPTY;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.exception.ErrorResponse;

@Slf4j
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

  private final ObjectMapper objectMapper;

  @Override
  public Exception decode(String methodKey, Response response) {

    if (Objects.isNull(response)) {
      log.error("[Feign Error] 응답이 null입니다. methodKey={}", methodKey);
      return CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, "FeignClient 응답 없음");
    }

    int status = response.status();

    if (status == 503 || status == 429) {
      log.warn("[Feign RetryableException] methodKey={}, status={}, url={}",
          methodKey, status, safeUrl(response));

      return new RetryableException(
          status,
          "[Feign Retry] 재시도 대상 오류",
          response.request().httpMethod(),
          1000L,
          response.request()
      );
    }

    ErrorResponse errorResponse = extractErrorResponse(response);

    String errorMessage = (errorResponse != null && errorResponse.message() != null)
        ? errorResponse.message()
        : "FeignClient 호출 실패 (status=" + status + ")";

    loggingError(methodKey, response, errorResponse);

    return CustomException.of(
        HttpStatus.valueOf(status),
        errorMessage
    );
  }

  private ErrorResponse extractErrorResponse(Response response) {
    if (response.body() == null) {
      return null;
    }
    try (InputStream responseBodyStream = response.body().asInputStream()) {
      String body = StreamUtils.copyToString(responseBodyStream, StandardCharsets.UTF_8);
      return objectMapper.readValue(body, ErrorResponse.class);
    } catch (IOException e) {
      log.warn("[Feign ErrorResponse 파싱 실패]", e);
      return null;
    }
  }

  private String safeUrl(Response response) {
    try {
      return response.request().url();
    } catch (Exception e) {
      return "unknown";
    }
  }

  private void loggingError(String methodKey, Response response, ErrorResponse errorResponse) {
    Request request = response.request();

    log.error(""" 
        \s
            Request Fail: {}
            URL: {} {}
            Status: {}
            Request Header: {}
            Request Body: {}
            Response Body: {}
        \s""",
        methodKey,
        request.httpMethod(),
        request.url(),
        response.status(),
        extractRequestHeader(request),
        extractRequestBody(request),
        errorResponse
    );
  }

  private static String extractRequestHeader(Request request) {
    Map<String, Collection<String>> headers = request.headers();

    if (Objects.nonNull(headers)) {
      return headers.toString();
    }
    return EMPTY;
  }

  private static String extractRequestBody(Request request) {
    byte[] body = request.body();

    if (Objects.nonNull(body)) {
      return new String(body, StandardCharsets.UTF_8);
    }
    return EMPTY;
  }
}