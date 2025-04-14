package table.eat.now.waiting.waiting_request.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;
import table.eat.now.waiting.waiting_request.presentation.dto.request.CreateWaitingRequestRequest;
import table.eat.now.waiting.waiting_request.presentation.dto.response.GetWaitingRequestResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting-requests")
@RestController
public class WaitingRequestApiController {

  private final WaitingRequestService waitingRequestService;

  @PostMapping
  public ResponseEntity<Void> createWaitingRequest(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @RequestBody CreateWaitingRequestRequest request
  ) {

    String waitingRequestUuid = waitingRequestService.createWaitingRequest(userInfo, request.toCommand());
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

    GetWaitingRequestInfo info =
        waitingRequestService.getWaitingRequest(userInfo, waitingRequestUuid.toString(), phone);
    return ResponseEntity.ok().body(GetWaitingRequestResponse.from(info));
  }
}
