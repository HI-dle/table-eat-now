package table.eat.now.payment.payment.infrastructure.client.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import table.eat.now.payment.payment.application.dto.response.CancelPgPaymentInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CancelTossPayResponse(
    String paymentKey,
    Cancel cancel
) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Cancel(
      String cancelReason,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Seoul")
      LocalDateTime canceledAt
  ) {
  }

  public CancelPgPaymentInfo toInfo(){
    return new CancelPgPaymentInfo(
        paymentKey,
        cancel.cancelReason,
        cancel.canceledAt
    );
  }
}