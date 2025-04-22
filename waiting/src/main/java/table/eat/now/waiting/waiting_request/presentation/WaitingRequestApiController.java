package table.eat.now.waiting.waiting_request.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.router.UsecaseRouter;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CancelWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.PostponeWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.presentation.dto.request.CreateWaitingRequestRequest;
import table.eat.now.waiting.waiting_request.presentation.dto.response.GetWaitingRequestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting-requests")
@RestController
public class WaitingRequestApiController {
  private final UsecaseRouter router;

  @PostMapping
  public ResponseEntity<Void> createWaitingRequest(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @RequestBody CreateWaitingRequestRequest request
  ) {

    CreateWaitingRequestCommand command = request.toCommand(userInfo);
    String waitingRequestUuid = router.execute(command);
    return ResponseEntity.created(
        ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{waitingRequestUuid}")
            .buildAndExpand(waitingRequestUuid)
            .toUri()
    ).build();
  }

  @GetMapping("/{waitingRequestUuid}")
  public ResponseEntity<GetWaitingRequestResponse> getWaitingRequest(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestUuid,
      @RequestParam @Valid @Pattern(regexp = "^[0-9]{8,15}$") String phone
  ) {

    GetWaitingRequestQuery query = GetWaitingRequestQuery.of(
        userInfo.userId(), userInfo.role(), waitingRequestUuid.toString(), phone);
    GetWaitingRequestInfo info = router.execute(query);
    return ResponseEntity.ok().body(GetWaitingRequestResponse.from(info));
  }

  @PatchMapping("/{waitingRequestUuid}/postpone")
  public ResponseEntity<Void> postponeWaitingRequest(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestUuid,
      @RequestParam @Valid @Pattern(regexp = "^[0-9]{8,15}$") String phone
  ) {

    PostponeWaitingRequestCommand command = PostponeWaitingRequestCommand.of(
        userInfo.userId(), userInfo.role(), waitingRequestUuid.toString(), phone);
    router.execute(command);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{waitingRequestUuid}/cancel")
  public ResponseEntity<Void> cancelWaitingRequest(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestUuid,
      @RequestParam @Valid @Pattern(regexp = "^[0-9]{8,15}$") String phone
  ) {

    CancelWaitingRequestCommand command = CancelWaitingRequestCommand.of(
        userInfo.userId(), userInfo.role(), waitingRequestUuid.toString(), phone);
    router.execute(command);
    return ResponseEntity.ok().build();
  }
}
