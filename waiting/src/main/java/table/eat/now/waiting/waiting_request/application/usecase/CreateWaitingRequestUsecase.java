package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateNoDuplicateWaitingRequest;
import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateWaitingAvailable;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.Command;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.usecase.handler.WaitingRequestNotifyHandler;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.service.WaitingRequestDomainService;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;

@RequiredArgsConstructor
@Service
public class CreateWaitingRequestUsecase implements CommandUsecase<CreateWaitingRequestCommand, String>{

  private final WaitingRequestNotifyHandler notifyHandler;
  private final WaitingClient waitingClient;
  private final WaitingRequestDomainService domainService;
  private final WaitingRequestReader reader;
  private final WaitingRequestStore store;

  @Override
  public Class<? extends Command> getCommandClass() {
    return CreateWaitingRequestCommand.class;
  }

  @Override
  public String execute(CreateWaitingRequestCommand command) {

    GetDailyWaitingInfo dailyWaitingInfo = waitingClient.getDailyWaitingInfo(
        command.dailyWaitingUuid());

    validateWaitingAvailable(dailyWaitingInfo);
    validateNoDuplicateWaitingRequest(
        reader.existsDuplicateWaiting(command.dailyWaitingUuid(), command.phone()));

    String waitingRequestUuid = UUID.randomUUID().toString();
    Long sequence = store.generateNextSequence(command.dailyWaitingUuid());

    WaitingRequest waitingRequest = command.toEntity(
        waitingRequestUuid, dailyWaitingInfo.restaurantUuid(), command.userId(), sequence);
    store.save(waitingRequest);

    store.enqueueWaitingRequest(command.dailyWaitingUuid(), waitingRequestUuid);
    Long rank = reader.getRank(command.dailyWaitingUuid(), waitingRequestUuid);
    Long estimatedWaitingMin = domainService.calculateEstimatedWaitingMin(dailyWaitingInfo.avgWaitingSec(), rank);

    notifyHandler.notifyCreated(
        command, waitingRequestUuid, dailyWaitingInfo.restaurantName(),
        sequence, rank, estimatedWaitingMin);

    return waitingRequestUuid;
  }
}
