package table.eat.now.waiting.waiting_request.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;
import table.eat.now.waiting.waiting_request.presentation.dto.request.CreateWaitingRequestRequest;

@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting-requests")
@RestController
public class WaitingRequestApiController {

  private final WaitingRequestService waitingRequestService;

  @AuthCheck(roles = {UserRole.CUSTOMER})
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
}
