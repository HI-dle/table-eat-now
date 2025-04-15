package table.eat.now.waiting.waiting.presentation;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.waiting.waiting.application.service.WaitingService;
import table.eat.now.waiting.waiting.application.service.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting.presentation.dto.response.GetDailyWaitingResponse;

@RequiredArgsConstructor
@RequestMapping("/internal/v1/waitings")
@RestController
public class WaitingInternalController {
  private final WaitingService waitingService;

  @GetMapping("/{dailyWaitingUuid}")
  public ResponseEntity<GetDailyWaitingResponse> getDailyWaitingInfo(
      @PathVariable UUID dailyWaitingUuid
  ) {
    GetDailyWaitingInfo info = waitingService.getDailyWaitingInfo(dailyWaitingUuid.toString());
    return ResponseEntity.ok().body(GetDailyWaitingResponse.from(info));
  }
}
