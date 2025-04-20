package table.eat.now.waiting.waiting_request.presentation;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;
import table.eat.now.waiting.waiting_request.presentation.dto.response.GetWaitingRequestResponse;

@RequiredArgsConstructor
@RequestMapping("/internal/v1/waiting-requests")
@RestController
public class WaitingRequestInternalController {
  private final WaitingRequestService waitingRequestService;

  @AuthCheck(roles = {UserRole.MASTER, UserRole.CUSTOMER})
  @GetMapping("/{waitingRequestUuid}")
  public ResponseEntity<GetWaitingRequestResponse> getWaitingRequestInternal(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestUuid
  ) {

    GetWaitingRequestInfo info =
        waitingRequestService.getWaitingRequestInternal(userInfo, waitingRequestUuid.toString());
    return ResponseEntity.ok().body(GetWaitingRequestResponse.from(info));
  }
}
