package table.eat.now.payment.payment.infrastructure.client.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.payment.payment.application.dto.response.ConfirmPgPaymentInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConfirmTossPayResponse(
    String paymentKey,
    @JsonProperty("discount")
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Seoul")
    LocalDateTime approvedAt
) {
    public ConfirmPgPaymentInfo toInfo() {
        return new ConfirmPgPaymentInfo(
            paymentKey, discountAmount, totalAmount, approvedAt);
    }
}
