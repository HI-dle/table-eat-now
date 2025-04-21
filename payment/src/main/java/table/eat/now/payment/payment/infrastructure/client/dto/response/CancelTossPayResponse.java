package table.eat.now.payment.payment.infrastructure.client.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CancelTossPayResponse(
    String paymentKey,
    List<Cancel> cancels,
    BigDecimal balanceAmount
) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Cancel(
      String cancelReason,
      BigDecimal cancelAmount,
      BigDecimal balanceAmount,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Seoul")
      LocalDateTime canceledAt
  ) {
  }

  public CancelPgPaymentInfo toInfo(){
    Cancel cancelledResult = cancels.get(0);
    return new CancelPgPaymentInfo(
        paymentKey,
        cancelledResult.cancelReason,
        cancelledResult.cancelAmount,
        balanceAmount,
        cancelledResult.canceledAt
    );
  }
}