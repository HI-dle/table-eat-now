package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateUserPhoneNumber;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.Command;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.PostponeWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.usecase.handler.WaitingRequestNotifyHandler;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingStatus;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.service.WaitingRequestDomainService;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;

@RequiredArgsConstructor
@Service
public class PostponeWaitingRequestUsecase implements CommandUsecase<PostponeWaitingRequestCommand, Void>{

  private final WaitingRequestNotifyHandler notifyHandler;
  private final WaitingClient waitingClient;
  private final WaitingRequestDomainService domainService;
  private final WaitingRequestReader reader;
  private final WaitingRequestStore store;

  @Override
  public Class<? extends Command> getCommandClass() {
    return PostponeWaitingRequestCommand.class;
  }

  @Override
  @Transactional
  public Void execute(PostponeWaitingRequestCommand command) {

    WaitingRequest waitingRequest = reader.getWaitingRequestBy(command.waitingRequestUuid());

    validateUserPhoneNumber(command.phone(), waitingRequest.getPhone());

    waitingRequest.updateStatus(WaitingStatus.POSTPONED);

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        waitingRequest.getDailyWaitingUuid());
    store.enqueueWaitingRequest(waitingRequest.getDailyWaitingUuid(), command.waitingRequestUuid());
    Long rank = reader.getRank(waitingRequest.getDailyWaitingUuid(), command.waitingRequestUuid());
    Long estimatedWaitingMin = domainService.calculateEstimatedWaitingMin(dailyWaitingInfo.avgWaitingSec(), rank);;

    notifyHandler.notifyPostponed(
        waitingRequest, dailyWaitingInfo.restaurantName(), rank, estimatedWaitingMin);
    return null;
  }
}
