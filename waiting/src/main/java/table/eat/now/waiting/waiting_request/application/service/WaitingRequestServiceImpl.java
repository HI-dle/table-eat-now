package table.eat.now.waiting.waiting_request.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.dto.event.SendCreatedWaitingRequestEvent;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.application.utils.TimeProvider;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.repository.WaitingRequestRepository;

@RequiredArgsConstructor
@Service
public class WaitingRequestServiceImpl implements WaitingRequestService {
  private final WaitingRequestRepository waitingRequestRepository;

  @Override
  public String createWaitingRequest(
      CurrentUserInfoDto userInfo, CreateWaitingRequestCommand command) {

    // todo. 현재 가게가 대기 가능한지 검증

    validateNoDuplicateWaitingRequest(command);

    String waitingRequestUuid = UUID.randomUUID().toString();
    Long sequence = generateSequence(command.dailyWaitingUuid());
    Long rank = enqueueWaitingRequestAndGetRank(command.dailyWaitingUuid(), waitingRequestUuid);
    long estimatedWaitingTime = 0L * rank; // todo

    WaitingRequest waitingRequest = command.toEntity(waitingRequestUuid, userInfo.userId(), sequence);
    waitingRequestRepository.save(waitingRequest);

    // todo. 대기 결과 안내 문자 발송
    sendWaitingRequestConfirmedInfoMessage(command, sequence, rank, estimatedWaitingTime);

    return waitingRequestUuid;
  }

  private void sendWaitingRequestConfirmedInfoMessage(
      CreateWaitingRequestCommand command, Long sequence, Long rank, long estimatedWaitingTime) {

    SendCreatedWaitingRequestEvent event = SendCreatedWaitingRequestEvent.of(
        command.phone(), command.slackId(), sequence, rank, estimatedWaitingTime);
  }

  private Long enqueueWaitingRequestAndGetRank(String dailyWaitingUuid, String waitingRequestUuid) {

    long epochMilli = TimeProvider.currentTimeMillis();
    boolean result = waitingRequestRepository.enqueueWaitingRequest(
        dailyWaitingUuid, waitingRequestUuid, epochMilli);
    if (!result) {
      throw CustomException.from(WaitingRequestErrorCode.FAILED_ENQUEUE);
    }
    return waitingRequestRepository.getRank(dailyWaitingUuid, waitingRequestUuid);
  }

  private Long generateSequence(String dailyWaitingUuid) {
    return waitingRequestRepository.generateNextSequence(dailyWaitingUuid);
  }

  private void validateNoDuplicateWaitingRequest(CreateWaitingRequestCommand command) {
    boolean existsBy = waitingRequestRepository.existsByConditionAndStatusIsWaitingAndDeletedAtIsNull(
        command.dailyWaitingUuid(), command.phone());
    if (existsBy) {
      throw CustomException.from(WaitingRequestErrorCode.ALREADY_EXISTS_WAITING);
    }
  }
}
