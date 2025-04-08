package table.eat.now.promotion.presentation.dto.response;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import table.eat.now.promotion.application.dto.response.CreatePromotionInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Builder
public record CreatePromotionResponse(Long promotionId,
                                      UUID promotionUuid,
                                      String promotionName,
                                      String description,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      BigDecimal discountAmount,
                                      String promotionStatus,
                                      String promotionType) {

  public static CreatePromotionResponse from(CreatePromotionInfo createPromotionInfo) {
    return CreatePromotionResponse.builder()
        .promotionId(createPromotionInfo.promotionId())
        .promotionUuid(createPromotionInfo.promotionUuid())
        .promotionName(createPromotionInfo.promotionName())
        .description(createPromotionInfo.description())
        .startTime(createPromotionInfo.startTime())
        .endTime(createPromotionInfo.endTime())
        .discountAmount(createPromotionInfo.discountAmount())
        .promotionStatus(createPromotionInfo.promotionStatus())
        .promotionType(createPromotionInfo.promotionType())
        .build();
  }

}
