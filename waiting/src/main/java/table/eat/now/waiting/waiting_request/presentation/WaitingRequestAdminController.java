package table.eat.now.waiting.waiting_request.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;
import table.eat.now.waiting.waiting_request.presentation.dto.request.EnterWaitingRequestRequest;

@RequiredArgsConstructor
@RequestMapping("/admin/v1/waiting-requests")
@RestController
public class WaitingRequestAdminController {
  private final WaitingRequestService waitingRequestService;

  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  @PostMapping("/{waitingRequestsUuid}/entrance")
  public ResponseEntity<Void> processWaitingRequestEntrance(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestsUuid,
      @RequestBody @Valid EnterWaitingRequestRequest request
  ) {

    waitingRequestService.processWaitingRequestEntrance(
        userInfo, waitingRequestsUuid.toString(), request.toCommand());
    return ResponseEntity.ok().build();
  }
}
