package table.eat.now.waiting.waiting_request.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.Builder;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CreateWaitingRequestCommand;

@Builder
public record CreateWaitingRequestRequest(
    @NotNull UUID dailyWaitingUuid,
    @NotBlank @Pattern(regexp = "^[0-9]{8,15}$") String phone,
    @NotBlank @Email String slackId,
    @NotNull @Positive int seatSize
) {

  public CreateWaitingRequestCommand toCommand(CurrentUserInfoDto userInfo) {
    return CreateWaitingRequestCommand.builder()
        .userId(userInfo.userId())
        .userRole(userInfo.role())
        .dailyWaitingUuid(dailyWaitingUuid.toString())
        .phone(phone)
        .slackId(slackId)
        .seatSize(seatSize)
        .build();
  }
}
