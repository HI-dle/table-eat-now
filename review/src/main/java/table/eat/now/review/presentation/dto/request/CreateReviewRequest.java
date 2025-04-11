package table.eat.now.review.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;

public record CreateReviewRequest(
    @NotNull @Pattern(regexp = "^(WAITING|RESERVATION)$") String serviceType,
    @NotNull UUID restaurantId,
    @NotNull UUID serviceId,
    @NotNull @Min(0) @Max(5) Integer rating,
    @NotBlank String content,
    @NotNull Boolean isVisible) {

  public CreateReviewCommand toCommand(CurrentUserInfoDto userInfo) {
    return new CreateReviewCommand(
        restaurantId.toString(), serviceId.toString(), userInfo.userId(), serviceType,
        content, rating,
        isVisible, userInfo.role());
  }
}
