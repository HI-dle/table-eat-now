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
import table.eat.now.waiting.waiting_request.application.router.UsecaseRouter;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.GetWaitingRequestInternalQuery;
import table.eat.now.waiting.waiting_request.application.usecase.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.presentation.dto.response.GetWaitingRequestResponse;

@RequiredArgsConstructor
@RequestMapping("/internal/v1/waiting-requests")
@RestController
public class WaitingRequestInternalController {
  private final UsecaseRouter router;

  @AuthCheck(roles = {UserRole.MASTER, UserRole.CUSTOMER})
  @GetMapping("/{waitingRequestUuid}")
  public ResponseEntity<GetWaitingRequestResponse> getWaitingRequestInternal(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestUuid
  ) {

    GetWaitingRequestInternalQuery query = GetWaitingRequestInternalQuery.of(
        userInfo.userId(), userInfo.role(), waitingRequestUuid.toString());
    GetWaitingRequestInfo info = router.execute(query);
    return ResponseEntity.ok().body(GetWaitingRequestResponse.from(info));
  }
}
