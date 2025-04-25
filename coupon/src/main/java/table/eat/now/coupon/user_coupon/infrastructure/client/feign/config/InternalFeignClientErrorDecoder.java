package table.eat.now.coupon.user_coupon.infrastructure.client.feign.config;

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
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.exception.ErrorResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class InternalFeignClientErrorDecoder {

  private final ObjectMapper objectMapper;

  public ErrorDecoder decoder() {

    return (methodKey, response) -> {

      if (response.status() == 503 || response.status() == 429) {
        return new RetryableException(
            response.status(),
            "서비스에 일시적으로 문제가 발생하였습니다. 재시도를 수행합니다.",
            response.request().httpMethod(),
            1000L,
            response.request()
        );
      }
      ErrorResponse errorResponse = extractError(response);
      loggingError(methodKey, response, errorResponse);

      throw CustomException.of(
          HttpStatus.valueOf(response.status()),
          errorResponse.message()
      );
    };
  }

  private ErrorResponse extractError(Response response) {
    try (InputStream responseBodyStream = response.body().asInputStream()) {
      String body = StreamUtils.copyToString(responseBodyStream, StandardCharsets.UTF_8);
      return objectMapper.readValue(body, ErrorResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
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
      new String(body, StandardCharsets.UTF_8);
    }
    return EMPTY;
  }
}
