package table.eat.now.waiting.waiting_request.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.PageResult;
import table.eat.now.waiting.waiting_request.application.service.WaitingRequestService;
import table.eat.now.waiting.waiting_request.presentation.dto.response.GetWaitingRequestResponse;
import table.eat.now.waiting.waiting_request.presentation.dto.response.GetWaitingRequestsResponse;

@RequiredArgsConstructor
@RequestMapping("/admin/v1/waiting-requests")
@RestController
public class WaitingRequestAdminController {
  private final WaitingRequestService waitingRequestService;

  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  @PostMapping("/{waitingRequestsUuid}/entrance")
  public ResponseEntity<Void> processWaitingRequestEntrance(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestsUuid
  ) {

    waitingRequestService.processWaitingRequestEntrance(
        userInfo, waitingRequestsUuid.toString());
    return ResponseEntity.ok().build();
  }

  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  @GetMapping("/{waitingRequestUuid}")
  public ResponseEntity<GetWaitingRequestResponse> getWaitingRequest(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestUuid
  ) {

    GetWaitingRequestInfo info =
        waitingRequestService.getWaitingRequestAdmin(userInfo, waitingRequestUuid.toString());
    return ResponseEntity.ok().body(GetWaitingRequestResponse.from(info));
  }

  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  @GetMapping
  public ResponseEntity<GetWaitingRequestsResponse> getCurrentWaitingRequestsAdmin(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @RequestParam UUID dailyWaitingUuid,
      @PageableDefault Pageable pageable
  ) {

    PageResult<GetWaitingRequestInfo> info =
        waitingRequestService.getCurrentWaitingRequestsAdmin(userInfo, dailyWaitingUuid.toString(), pageable);
    return ResponseEntity.ok().body(GetWaitingRequestsResponse.from(info));
  }

  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  @PatchMapping("/{waitingRequestUuid}/status")
  public ResponseEntity<Void> updateWaitingRequestStatus(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @PathVariable UUID waitingRequestUuid,
      @RequestParam @Valid @Pattern(regexp = "^(?i)(SEATED|LEAVED|NO_SHOW)$") String type
  ) {

    waitingRequestService.updateWaitingRequestStatusAdmin(userInfo, waitingRequestUuid.toString(), type);
    return ResponseEntity.ok().build();
  }
}
