package table.eat.now.review.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.service.dto.request.UpdateReviewCommand;

public record UpdateReviewRequest(
    @NotBlank String content, @NotNull @Min(0) @Max(5) Integer rating) {

  public UpdateReviewCommand toCommand(CurrentUserInfoDto userInfo) {
    return new UpdateReviewCommand(
        content, rating, userInfo);
  }
}
