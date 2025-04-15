package table.eat.now.waiting.waiting.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.waiting.waiting.application.service.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting.domain.entity.DailyWaiting;
import table.eat.now.waiting.waiting.domain.repository.WaitingRepository;

@RequiredArgsConstructor
@Service
public class WaitingServiceImpl implements WaitingService {
  private final WaitingRepository waitingRepository;

  @Override
  public GetDailyWaitingInfo getDailyWaitingInfo(String dailyWaitingUuid) {

    DailyWaiting dailyWaiting = waitingRepository.getDailyWaitingBy(dailyWaitingUuid);
    return GetDailyWaitingInfo.from(dailyWaiting);
  }
}
