package table.eat.now.notification.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.notification.application.service.NotificationService;
import table.eat.now.notification.presentation.dto.request.CreateNotificationRequest;
import table.eat.now.notification.presentation.dto.request.UpdateNotificationRequest;
import table.eat.now.notification.presentation.dto.response.CreateNotificationResponse;
import table.eat.now.notification.presentation.dto.response.UpdateNotificationResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@RestController
@RequestMapping("/admin/v1/notifications")
@RequiredArgsConstructor
public class NotificationAdminController {

  private final NotificationService notificationService;

  @PostMapping
  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF})
  public ResponseEntity<Void> createNotification(
      @Valid @RequestBody CreateNotificationRequest request) {

    CreateNotificationResponse notificationResponse = CreateNotificationResponse
        .from(notificationService
            .createNotification(request.toApplication()));

    return ResponseEntity.created(UriComponentsBuilder.fromUriString("/admin/v1/notifications")
            .buildAndExpand(notificationResponse.notificationUuid())
            .toUri())
        .build();
  }

  @PutMapping("/{notificationsUuid}")
  @AuthCheck(roles = UserRole.MASTER)
  public ResponseEntity<UpdateNotificationResponse> updateNotification(
      @PathVariable("notificationsUuid") UUID notificationUuid,
      @Valid @RequestBody UpdateNotificationRequest request) {

    return ResponseEntity.ok(
        UpdateNotificationResponse.from(
            notificationService.updateNotification(
                request.toApplication(), notificationUuid)));
  }

}
